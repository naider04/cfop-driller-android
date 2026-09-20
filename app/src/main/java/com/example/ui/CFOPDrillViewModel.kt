package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioEngine
import com.example.data.AlgorithmRepository
import com.example.model.*
import com.example.parser.AlgorithmParser
import kotlinx.coroutines.flow.*

data class DrillUiState(
    val selectedStage: CubeStage = CubeStage.PLL,
    val selectedSubCategory: String? = null,
    val searchQuery: String = "",
    val currentCase: AlgorithmCase? = null,
    val activeAlgorithm: String = "",
    val currentSteps: List<MoveStep> = emptyList(),
    val activeStepIndex: Int = -1,
    val isPlaying: Boolean = false,
    val audioSettings: AudioSettings = AudioSettings(),
    val showSettingsSheet: Boolean = false,
    val showAddDialog: Boolean = false
)

class CFOPDrillViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AlgorithmRepository()
    val audioEngine = AudioEngine(application)

    private val _uiState = MutableStateFlow(DrillUiState())
    val uiState: StateFlow<DrillUiState> = _uiState.asStateFlow()

    val allCases: StateFlow<List<AlgorithmCase>> = repository.cases

    init {
        // Pick initial default case (e.g. T-Perm)
        val initialCase = repository.cases.value.firstOrNull { it.id == "pll_t" }
            ?: repository.cases.value.first()
        selectCase(initialCase)
    }

    fun selectStage(stage: CubeStage) {
        _uiState.update {
            it.copy(selectedStage = stage, selectedSubCategory = null)
        }
        // Auto-select first case of that stage if current is not in that stage
        val current = _uiState.value.currentCase
        if (current == null || (stage != CubeStage.CUSTOM && current.category != stage)) {
            val nextCase = repository.cases.value.firstOrNull { it.category == stage }
            if (nextCase != null) {
                selectCase(nextCase)
            }
        }
    }

    fun selectSubCategory(sub: String?) {
        _uiState.update { it.copy(selectedSubCategory = sub) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectCase(case: AlgorithmCase) {
        audioEngine.stopPlayback()
        val parsedSteps = AlgorithmParser.parseAlgorithmMoves(case.algorithm)
        _uiState.update {
            it.copy(
                currentCase = case,
                activeAlgorithm = case.algorithm,
                currentSteps = parsedSteps,
                activeStepIndex = -1,
                isPlaying = false
            )
        }
    }

    fun selectAlgorithm(algorithm: String) {
        audioEngine.stopPlayback()
        val parsedSteps = AlgorithmParser.parseAlgorithmMoves(algorithm)
        _uiState.update {
            it.copy(
                activeAlgorithm = algorithm,
                currentSteps = parsedSteps,
                activeStepIndex = -1,
                isPlaying = false
            )
        }
    }

    fun togglePlayPause() {
        val state = _uiState.value
        val steps = state.currentSteps
        if (steps.isEmpty()) return

        if (state.isPlaying) {
            audioEngine.stopPlayback()
        } else {
            val startIdx = if (state.activeStepIndex in steps.indices) state.activeStepIndex else 0
            audioEngine.startAlgorithmPlayback(
                steps = steps,
                settings = state.audioSettings,
                startIndex = startIdx,
                loop = state.audioSettings.loop,
                onStepChange = { stepIdx ->
                    _uiState.update { it.copy(activeStepIndex = stepIdx) }
                },
                onPlayStateChange = { playing ->
                    _uiState.update { it.copy(isPlaying = playing) }
                }
            )
        }
    }

    fun setSpeed(newSpeed: Float) {
        _uiState.update {
            it.copy(audioSettings = it.audioSettings.copy(speed = newSpeed))
        }
        // Immediately updates TTS voice rate
        audioEngine.updateSpeed(newSpeed)
    }

    fun setPlaybackStyle(style: PlaybackStyle) {
        _uiState.update {
            it.copy(audioSettings = it.audioSettings.copy(playbackStyle = style))
        }
        // Immediately restarts audio if currently playing
        audioEngine.updatePlaybackStyle(style)
    }

    fun updateAudioSettings(newSettings: AudioSettings) {
        _uiState.update { it.copy(audioSettings = newSettings) }
        audioEngine.updateSettings(newSettings)
    }

    fun restartFromBeginning() {
        val state = _uiState.value
        val steps = state.currentSteps
        if (steps.isEmpty()) return

        if (state.isPlaying) {
            audioEngine.startAlgorithmPlayback(
                steps = steps,
                settings = state.audioSettings,
                startIndex = 0,
                loop = state.audioSettings.loop,
                onStepChange = { stepIdx ->
                    _uiState.update { it.copy(activeStepIndex = stepIdx) }
                },
                onPlayStateChange = { playing ->
                    _uiState.update { it.copy(isPlaying = playing) }
                }
            )
        } else {
            _uiState.update { it.copy(activeStepIndex = 0) }
        }
    }

    fun jumpToStep(index: Int) {
        val state = _uiState.value
        val steps = state.currentSteps
        if (index !in steps.indices) return

        if (state.isPlaying) {
            audioEngine.startAlgorithmPlayback(
                steps = steps,
                settings = state.audioSettings,
                startIndex = index,
                loop = state.audioSettings.loop,
                onStepChange = { stepIdx ->
                    _uiState.update { it.copy(activeStepIndex = stepIdx) }
                },
                onPlayStateChange = { playing ->
                    _uiState.update { it.copy(isPlaying = playing) }
                }
            )
        } else {
            _uiState.update { it.copy(activeStepIndex = index) }
        }
    }

    fun stepPrevious() {
        val state = _uiState.value
        val prev = (state.activeStepIndex - 1).coerceAtLeast(0)
        jumpToStep(prev)
    }

    fun stepNext() {
        val state = _uiState.value
        val next = (state.activeStepIndex + 1).coerceAtMost(state.currentSteps.lastIndex)
        jumpToStep(next)
    }

    fun testMetronomeTick() {
        audioEngine.playMetronomeTick(highPitch = false)
    }

    fun setShowSettingsSheet(show: Boolean) {
        _uiState.update { it.copy(showSettingsSheet = show) }
    }

    fun setShowAddDialog(show: Boolean) {
        _uiState.update { it.copy(showAddDialog = show) }
    }

    fun addCustomAlgorithm(case: AlgorithmCase) {
        repository.addCustomAlgorithm(case)
        selectCase(case)
        setShowAddDialog(false)
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }
}
