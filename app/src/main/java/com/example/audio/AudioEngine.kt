package com.example.audio

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import com.example.model.AudioSettings
import com.example.model.MoveStep
import com.example.model.PlaybackStyle
import com.example.model.PronunciationMode
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext
import java.util.Locale
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class AudioEngine(context: Context) {

    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private val metronome = MetronomeSynthesizer()

    private val engineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private var activePlaybackJob: Job? = null
    private var fluidTimerJob: Job? = null

    // State tracking
    var isPlaying: Boolean = false
        private set

    private var currentSteps: List<MoveStep> = emptyList()
    private var currentSettings: AudioSettings = AudioSettings()
    private var currentStepIndex: Int = 0
    private var currentLoop: Boolean = true
    private var stepChangeCallback: ((Int) -> Unit)? = null
    private var playStateCallback: ((Boolean) -> Unit)? = null

    private val utteranceCompletions = ConcurrentHashMap<String, CompletableDeferred<Boolean>>()
    private val utteranceStepRanges = ConcurrentHashMap<String, List<StepCharRange>>()

    private data class StepCharRange(
        val start: Int,
        val end: Int,
        val originalIndex: Int
    )

    init {
        initTts()
    }

    private fun initTts() {
        tts = TextToSpeech(appContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.let { engine ->
                    val result = engine.setLanguage(Locale.US)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.w("AudioEngine", "US English missing or not supported, trying default locale")
                        engine.setLanguage(Locale.getDefault())
                    }
                    engine.setSpeechRate(currentSettings.speed)
                    engine.setPitch(currentSettings.pitch)
                    setupUtteranceListener(engine)
                    isTtsReady = true
                }
            } else {
                Log.e("AudioEngine", "TTS initialization failed: $status")
            }
        }
    }

    private fun setupUtteranceListener(engine: TextToSpeech) {
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String) {}

            override fun onDone(utteranceId: String) {
                utteranceCompletions[utteranceId]?.complete(true)
                utteranceCompletions.remove(utteranceId)
                utteranceStepRanges.remove(utteranceId)
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String) {
                utteranceCompletions[utteranceId]?.complete(false)
                utteranceCompletions.remove(utteranceId)
                utteranceStepRanges.remove(utteranceId)
            }

            override fun onError(utteranceId: String, errorCode: Int) {
                utteranceCompletions[utteranceId]?.complete(false)
                utteranceCompletions.remove(utteranceId)
                utteranceStepRanges.remove(utteranceId)
            }

            override fun onRangeStart(utteranceId: String, start: Int, end: Int, frame: Int) {
                val ranges = utteranceStepRanges[utteranceId] ?: return
                val matched = ranges.firstOrNull { start in it.start until it.end }
                if (matched != null) {
                    currentStepIndex = matched.originalIndex
                    stepChangeCallback?.invoke(matched.originalIndex)
                }
            }
        })
    }

    /**
     * Synthesizes an audible woodblock/click tick.
     */
    fun playMetronomeTick(highPitch: Boolean = false) {
        metronome.playMetronomeTick(highPitch)
    }

    /**
     * Stops any currently active playback.
     */
    fun stopPlayback() {
        activePlaybackJob?.cancel()
        activePlaybackJob = null
        fluidTimerJob?.cancel()
        fluidTimerJob = null

        isPlaying = false
        playStateCallback?.invoke(false)
        stepChangeCallback?.invoke(-1)

        try {
            tts?.stop()
        } catch (_: Exception) {}

        // Complete any pending utterance awaits with false
        utteranceCompletions.values.forEach { it.complete(false) }
        utteranceCompletions.clear()
        utteranceStepRanges.clear()
    }

    /**
     * Starts algorithm audio drilling.
     */
    fun startAlgorithmPlayback(
        steps: List<MoveStep>,
        settings: AudioSettings,
        startIndex: Int = 0,
        loop: Boolean = true,
        onStepChange: ((Int) -> Unit)? = null,
        onPlayStateChange: ((Boolean) -> Unit)? = null
    ) {
        stopPlayback()
        if (steps.isEmpty()) return

        currentSteps = steps
        currentSettings = settings
        currentStepIndex = startIndex.coerceIn(0, steps.lastIndex)
        currentLoop = loop
        stepChangeCallback = onStepChange
        playStateCallback = onPlayStateChange

        isPlaying = true
        playStateCallback?.invoke(true)

        tts?.setSpeechRate(settings.speed)
        tts?.setPitch(settings.pitch)

        activePlaybackJob = engineScope.launch {
            if (settings.playbackStyle == PlaybackStyle.CONTINUOUS) {
                runFluidMode(steps, settings, currentStepIndex, loop)
            } else {
                runPacedMode(steps, settings, currentStepIndex, loop)
            }
        }
    }

    /**
     * Immediately applies speed update to TTS voice and ongoing playback.
     */
    fun updateSpeed(newSpeed: Float) {
        currentSettings = currentSettings.copy(speed = newSpeed)
        tts?.setSpeechRate(newSpeed)

        if (isPlaying) {
            // Immediate rate application as requested by user
            if (currentSettings.playbackStyle == PlaybackStyle.CONTINUOUS) {
                // Restart fluid from current step or from 0
                val resumeIndex = currentStepIndex.coerceAtLeast(0)
                startAlgorithmPlayback(
                    steps = currentSteps,
                    settings = currentSettings,
                    startIndex = resumeIndex,
                    loop = currentLoop,
                    onStepChange = stepChangeCallback,
                    onPlayStateChange = playStateCallback
                )
            }
        }
    }

    /**
     * Immediately restarts playback when switching between Paced and Fluid modes.
     */
    fun updatePlaybackStyle(newStyle: PlaybackStyle) {
        if (currentSettings.playbackStyle == newStyle) return
        currentSettings = currentSettings.copy(playbackStyle = newStyle)

        if (isPlaying) {
            // Restart audio immediately as requested by user
            val resumeIndex = currentStepIndex.coerceAtLeast(0)
            startAlgorithmPlayback(
                steps = currentSteps,
                settings = currentSettings,
                startIndex = resumeIndex,
                loop = currentLoop,
                onStepChange = stepChangeCallback,
                onPlayStateChange = playStateCallback
            )
        }
    }

    /**
     * Updates full settings and handles instant audio rate/style adaptation.
     */
    fun updateSettings(newSettings: AudioSettings) {
        val speedChanged = newSettings.speed != currentSettings.speed
        val styleChanged = newSettings.playbackStyle != currentSettings.playbackStyle
        currentSettings = newSettings
        currentLoop = newSettings.loop

        tts?.setSpeechRate(newSettings.speed)
        tts?.setPitch(newSettings.pitch)

        if (isPlaying && (speedChanged || styleChanged)) {
            val resumeIndex = currentStepIndex.coerceAtLeast(0)
            startAlgorithmPlayback(
                steps = currentSteps,
                settings = currentSettings,
                startIndex = resumeIndex,
                loop = currentLoop,
                onStepChange = stepChangeCallback,
                onPlayStateChange = playStateCallback
            )
        }
    }

    // ==========================================
    // 1. FLUID (CONTINUOUS) PLAYBACK
    // ==========================================
    private suspend fun runFluidMode(
        steps: List<MoveStep>,
        settings: AudioSettings,
        startIndex: Int,
        loop: Boolean
    ) {
        var startIdx = startIndex
        while (coroutineContext.isActive) {
            val subSteps = if (startIdx in steps.indices) steps.drop(startIdx) else steps
            val stepOffset = if (startIdx in steps.indices) startIdx else 0
            startIdx = 0

            // 1. Construct single fluid rhythmic phrase with commas
            val phraseBuilder = StringBuilder()
            val ranges = mutableListOf<StepCharRange>()
            val segmentDurations = mutableListOf<Long>()

            subSteps.forEachIndexed { i, step ->
                val originalIndex = stepOffset + i
                val movePhrase = if (settings.pronunciationMode == PronunciationMode.DESCRIPTIVE) {
                    "${step.spoken}, ${step.fingerHint}"
                } else {
                    step.spoken
                }

                val isLast = i == subSteps.size - 1
                val separator = if (isLast) "." else ", "
                val segment = movePhrase + separator

                val start = phraseBuilder.length
                val end = start + segment.length
                ranges.add(StepCharRange(start, end, originalIndex))
                phraseBuilder.append(segment)

                // Approximate speaking time per segment for visual timer fallback:
                // Avg speaking speed is ~3.5 syllables per sec; scale by settings.speed
                val wordCount = movePhrase.split(" ").size.coerceAtLeast(1)
                val estDurationMs = ((wordCount * 360L + 120L) / settings.speed.coerceAtLeast(0.5f)).toLong()
                segmentDurations.add(estDurationMs)
            }

            val fullPhrase = phraseBuilder.toString()
            val utteranceId = UUID.randomUUID().toString()
            utteranceStepRanges[utteranceId] = ranges

            // Fallback timer coroutine for devices where TTS onRangeStart is absent or infrequent
            fluidTimerJob?.cancel()
            fluidTimerJob = engineScope.launch {
                for (idx in subSteps.indices) {
                    val origIdx = stepOffset + idx
                    currentStepIndex = origIdx
                    stepChangeCallback?.invoke(origIdx)
                    val delayMs = segmentDurations.getOrElse(idx) { 400L }
                    delay(delayMs)
                }
            }

            val finished = speakAsync(fullPhrase, utteranceId, settings)
            fluidTimerJob?.cancel()
            fluidTimerJob = null

            if (!finished || !coroutineContext.isActive || !loop) {
                break
            }

            // Loop pause & optional countdown
            stepChangeCallback?.invoke(-1)
            if (settings.loopCountdown) {
                speakCountdown(settings)
            } else {
                delay(maxOf(800L, settings.moveDelayMs))
            }
        }

        isPlaying = false
        playStateCallback?.invoke(false)
        stepChangeCallback?.invoke(-1)
    }

    // ==========================================
    // 2. PACED (STEP-BY-STEP) PLAYBACK
    // ==========================================
    private suspend fun runPacedMode(
        steps: List<MoveStep>,
        settings: AudioSettings,
        startIndex: Int,
        loop: Boolean
    ) {
        var currentIndex = startIndex

        while (coroutineContext.isActive) {
            if (currentIndex >= steps.size) {
                if (!loop) break
                stepChangeCallback?.invoke(-1)
                if (settings.loopCountdown) {
                    speakCountdown(settings)
                } else {
                    delay(600L)
                }
                currentIndex = 0
                continue
            }

            val step = steps[currentIndex]
            currentStepIndex = currentIndex
            stepChangeCallback?.invoke(currentIndex)

            // Optional metronome woodblock click before move
            if (settings.metronomeTick) {
                playMetronomeTick(currentIndex == 0)
            }

            // Speak current move
            val textToSpeak = if (settings.pronunciationMode == PronunciationMode.DESCRIPTIVE) {
                "${step.spoken}, ${step.fingerHint}"
            } else {
                step.spoken
            }

            val utteranceId = UUID.randomUUID().toString()
            speakAsync(textToSpeak, utteranceId, settings)

            if (!coroutineContext.isActive) break

            // Paced delay before next move
            val delayDuration = maxOf(100L, settings.moveDelayMs)
            delay(delayDuration)

            currentIndex++
        }

        isPlaying = false
        playStateCallback?.invoke(false)
        stepChangeCallback?.invoke(-1)
    }

    private suspend fun speakAsync(text: String, utteranceId: String, settings: AudioSettings): Boolean {
        val ttsEngine = tts ?: return false
        val deferred = CompletableDeferred<Boolean>()
        utteranceCompletions[utteranceId] = deferred

        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, settings.volume)
        }

        ttsEngine.setSpeechRate(settings.speed)
        ttsEngine.setPitch(settings.pitch)

        ttsEngine.speak(text, TextToSpeech.QUEUE_FLUSH, params, utteranceId)

        return try {
            deferred.await()
        } catch (e: CancellationException) {
            try {
                ttsEngine.stop()
            } catch (_: Exception) {}
            false
        }
    }

    private suspend fun speakCountdown(settings: AudioSettings) {
        val countdown = listOf("three", "two", "one")
        for (num in countdown) {
            if (!coroutineContext.isActive) return
            playMetronomeTick(num == "one")
            val id = UUID.randomUUID().toString()
            speakAsync(num, id, settings.copy(speed = 1.2f))
            delay(200L)
        }
    }

    fun release() {
        stopPlayback()
        metronome.release()
        try {
            tts?.shutdown()
        } catch (_: Exception) {}
        tts = null
        isTtsReady = false
    }
}
