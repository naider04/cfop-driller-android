package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.util.Log
import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sin

/**
 * Low-latency audio synthesizer that reproduces the speedcubing woodblock/metronome tick
 * using pitch-swept sine wave generation and exponential volume decay.
 */
class MetronomeSynthesizer {

    private var normalTrack: AudioTrack? = null
    private var highTrack: AudioTrack? = null
    private var isInitialized = false

    init {
        try {
            initTracks()
        } catch (e: Exception) {
            Log.e("MetronomeSynthesizer", "Failed to init AudioTracks: ${e.message}")
        }
    }

    private fun initTracks() {
        val sampleRate = 44100
        val durationSeconds = 0.045
        val numSamples = (sampleRate * durationSeconds).toInt()

        val normalBuffer = generateTickSamples(numSamples, sampleRate, startFreq = 800.0, endFreq = 300.0)
        val highBuffer = generateTickSamples(numSamples, sampleRate, startFreq = 1200.0, endFreq = 300.0)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val audioFormat = AudioFormat.Builder()
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setSampleRate(sampleRate)
            .build()

        val bufferSizeBytes = numSamples * 2

        normalTrack = AudioTrack(
            audioAttributes,
            audioFormat,
            bufferSizeBytes,
            AudioTrack.MODE_STATIC,
            AudioManager.AUDIO_SESSION_ID_GENERATE
        ).apply {
            write(normalBuffer, 0, normalBuffer.size)
        }

        highTrack = AudioTrack(
            audioAttributes,
            audioFormat,
            bufferSizeBytes,
            AudioTrack.MODE_STATIC,
            AudioManager.AUDIO_SESSION_ID_GENERATE
        ).apply {
            write(highBuffer, 0, highBuffer.size)
        }

        isInitialized = true
    }

    private fun generateTickSamples(
        numSamples: Int,
        sampleRate: Int,
        startFreq: Double,
        endFreq: Double
    ): ShortArray {
        val buffer = ShortArray(numSamples)
        var phase = 0.0
        val decayTime = 0.04

        for (i in 0 until numSamples) {
            val t = i.toDouble() / sampleRate
            val progress = (t / decayTime).coerceIn(0.0, 1.0)

            // Exponential frequency sweep: startFreq -> endFreq
            val freq = startFreq * (endFreq / startFreq).pow(progress)
            phase += 2.0 * PI * freq / sampleRate

            // Exponential gain ramp: 0.18 -> 0.001
            val gain = 0.22 * (0.001 / 0.22).pow(progress)

            val sample = (sin(phase) * gain * 32767.0).toInt().coerceIn(-32768, 32767)
            buffer[i] = sample.toShort()
        }
        return buffer
    }

    /**
     * Plays a single audible click/woodblock tick.
     * High pitch is used for the start of algorithm or final countdown beat.
     */
    fun playMetronomeTick(highPitch: Boolean = false) {
        if (!isInitialized) return
        try {
            val track = if (highPitch) highTrack else normalTrack
            track?.let {
                it.stop()
                it.reloadStaticData()
                it.play()
            }
        } catch (e: Exception) {
            Log.w("MetronomeSynthesizer", "Metronome tick playback error: ${e.message}")
        }
    }

    fun release() {
        try {
            normalTrack?.release()
            highTrack?.release()
        } catch (_: Exception) {}
        normalTrack = null
        highTrack = null
        isInitialized = false
    }
}
