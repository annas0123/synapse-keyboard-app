package com.smafty.synapsekeyboard.ui.keyboard

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.exp
import kotlin.math.sin
import kotlin.math.cos

// ---------------------------------------------------------------------------
// KeySoundPreset — enum of available key-click sounds (10 total)
// ---------------------------------------------------------------------------
enum class KeySoundPreset(val displayName: String, val description: String) {
    NONE(           "Off",                    "No key sound"),
    MODERN_DIGITAL( "Modern Digital",         "Sleek Gboard-style tick"),
    MECHANICAL(     "Classic Mechanical",     "Tactile blue switch clack"),
    BUBBLE(         "Futuristic Bubble",      "Soft sci-fi pop"),
    WOODEN(         "Wooden Block",           "Natural retro percussion"),
    TYPEWRITER(     "Typewriter",             "Vintage mechanical clatter"),
    CRYSTAL_PING(   "Crystal Ping",           "Clean glass bell chime"),
    SOFT_RUBBER(    "Soft Rubber",            "Quiet muted dampened tap"),
    NEON_ZAP(       "Neon Zap",              "Retro synth electric buzz"),
    WATER_DROP(     "Water Drop",             "Gentle liquid plink")
}

// ---------------------------------------------------------------------------
// KeySoundEngine — procedural audio synthesizer using AudioTrack
// Zero-latency, zero-file-dependency click sounds for the IME keyboard.
// ---------------------------------------------------------------------------
object KeySoundEngine {

    private const val SAMPLE_RATE = 44_100
    private const val PREFS_KEY_SOUND = "key_sound_preset"

    // Currently active preset — drives playback per key tap
    var activePreset: KeySoundPreset = KeySoundPreset.NONE
        private set

    /** Loads the persisted sound preset from SharedPreferences. */
    fun loadPreset(prefs: android.content.SharedPreferences) {
        val name = prefs.getString(PREFS_KEY_SOUND, KeySoundPreset.NONE.name)
        activePreset = try {
            KeySoundPreset.valueOf(name ?: KeySoundPreset.NONE.name)
        } catch (e: Exception) {
            KeySoundPreset.NONE
        }
    }

    /** Saves the selected sound preset to SharedPreferences. */
    fun savePreset(prefs: android.content.SharedPreferences, preset: KeySoundPreset) {
        prefs.edit().putString(PREFS_KEY_SOUND, preset.name).apply()
        activePreset = preset
    }

    /**
     * Plays the active key click sound on a background thread.
     * Safe to call from the UI thread — uses a daemon thread internally.
     */
    fun playClick() {
        if (activePreset == KeySoundPreset.NONE) return
        Thread {
            try {
                val samples = generateSamples(activePreset)
                playPcmSamples(samples)
            } catch (_: Exception) { /* swallow — audio must never crash the IME */ }
        }.also { it.isDaemon = true }.start()
    }

    /** Plays a one-shot preview of any preset (used in Settings). */
    fun preview(preset: KeySoundPreset) {
        if (preset == KeySoundPreset.NONE) return
        Thread {
            try {
                val samples = generateSamples(preset)
                playPcmSamples(samples)
            } catch (_: Exception) { }
        }.also { it.isDaemon = true }.start()
    }

    // -----------------------------------------------------------------------
    // Waveform generators
    // -----------------------------------------------------------------------

    private fun generateSamples(preset: KeySoundPreset): ShortArray = when (preset) {
        KeySoundPreset.NONE           -> ShortArray(0)
        KeySoundPreset.MODERN_DIGITAL -> generateModernDigital()
        KeySoundPreset.MECHANICAL     -> generateMechanical()
        KeySoundPreset.BUBBLE         -> generateBubble()
        KeySoundPreset.WOODEN         -> generateWooden()
        KeySoundPreset.TYPEWRITER     -> generateTypewriter()
        KeySoundPreset.CRYSTAL_PING   -> generateCrystalPing()
        KeySoundPreset.SOFT_RUBBER    -> generateSoftRubber()
        KeySoundPreset.NEON_ZAP       -> generateNeonZap()
        KeySoundPreset.WATER_DROP     -> generateWaterDrop()
    }

    /**
     * MODERN DIGITAL: A short, crisp high-frequency click (3.2 kHz)
     * with a fast exponential decay. Similar to Gboard default.
     * Duration: ~30ms
     */
    private fun generateModernDigital(): ShortArray {
        val durationMs = 30
        val numSamples = SAMPLE_RATE * durationMs / 1000
        val freq = 3200.0
        return ShortArray(numSamples) { i ->
            val t = i.toDouble() / SAMPLE_RATE
            val decay = exp(-t * 90.0)
            val wave = sin(2 * PI * freq * t)
            (decay * wave * Short.MAX_VALUE * 0.75).toInt().toShort()
        }
    }

    /**
     * MECHANICAL: A two-stage click — a sharp transient (attack) at
     * high amplitude followed by a short resonance tail at lower pitch.
     * Duration: ~55ms
     */
    private fun generateMechanical(): ShortArray {
        val durationMs = 55
        val numSamples = SAMPLE_RATE * durationMs / 1000
        val attackFreq = 1800.0
        val bodyFreq = 900.0
        return ShortArray(numSamples) { i ->
            val t = i.toDouble() / SAMPLE_RATE
            val attackDecay = exp(-t * 140.0)
            val bodyDecay   = exp(-t * 55.0)
            val wave = (attackDecay * sin(2 * PI * attackFreq * t) * 0.9) +
                       (bodyDecay   * sin(2 * PI * bodyFreq * t)   * 0.5)
            (wave * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
    }

    /**
     * BUBBLE: A soft, rounded sine chirp that sweeps upward from 500Hz
     * to 1200Hz over 40ms, giving it a pleasant "bloop" feel.
     * Duration: ~40ms
     */
    private fun generateBubble(): ShortArray {
        val durationMs = 40
        val numSamples = SAMPLE_RATE * durationMs / 1000
        val freqStart = 500.0
        val freqEnd   = 1200.0
        return ShortArray(numSamples) { i ->
            val t = i.toDouble() / SAMPLE_RATE
            val progress = t / (durationMs / 1000.0)
            val freq = freqStart + (freqEnd - freqStart) * progress
            val decay = exp(-t * 50.0) * sin(PI * progress) // bell-shaped envelope
            val wave  = sin(2 * PI * freq * t)
            (decay * wave * Short.MAX_VALUE * 0.65).toInt().toShort()
        }
    }

    /**
     * WOODEN: A low-frequency thump with strong harmonic content,
     * emulating a finger tap on a hollow wooden surface.
     * Duration: ~50ms
     */
    private fun generateWooden(): ShortArray {
        val durationMs = 50
        val numSamples = SAMPLE_RATE * durationMs / 1000
        val fundamental = 280.0
        return ShortArray(numSamples) { i ->
            val t = i.toDouble() / SAMPLE_RATE
            val decay = exp(-t * 70.0)
            // Fundamental + 2nd & 3rd harmonic = richer wooden tone
            val wave = sin(2 * PI * fundamental * t)       * 0.7 +
                       sin(2 * PI * fundamental * 2 * t)   * 0.2 +
                       sin(2 * PI * fundamental * 3 * t)   * 0.1
            (decay * wave * Short.MAX_VALUE * 0.85).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
    }

    /**
     * TYPEWRITER: Classic vintage clatter — a sharp noise burst followed
     * by a metallic ring. Uses band-limited noise + resonant sine.
     * Duration: ~60ms
     */
    private fun generateTypewriter(): ShortArray {
        val durationMs = 60
        val numSamples = SAMPLE_RATE * durationMs / 1000
        val ringFreq = 2400.0
        var noiseSeed = 1L
        return ShortArray(numSamples) { i ->
            val t = i.toDouble() / SAMPLE_RATE
            // LCG pseudo-noise for the clatter attack
            noiseSeed = (noiseSeed * 6364136223846793005L + 1442695040888963407L) and 0x7FFFFFFF
            val noise = (noiseSeed.toDouble() / 0x7FFFFFFF - 0.5) * 2.0
            val noiseDecay = exp(-t * 200.0) * 0.7
            val ringDecay  = exp(-t * 40.0) * 0.4
            val wave = noise * noiseDecay + sin(2 * PI * ringFreq * t) * ringDecay
            (wave * Short.MAX_VALUE * 0.9).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
    }

    /**
     * CRYSTAL PING: A clean bell-like tone using additive harmonics.
     * Two partials with different decay rates create a shimmering glass quality.
     * Duration: ~80ms
     */
    private fun generateCrystalPing(): ShortArray {
        val durationMs = 80
        val numSamples = SAMPLE_RATE * durationMs / 1000
        val f1 = 2200.0
        val f2 = 3700.0
        return ShortArray(numSamples) { i ->
            val t = i.toDouble() / SAMPLE_RATE
            val d1 = exp(-t * 30.0) * 0.6
            val d2 = exp(-t * 60.0) * 0.4
            val wave = d1 * sin(2 * PI * f1 * t) + d2 * sin(2 * PI * f2 * t)
            (wave * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
    }


    /**
     * SOFT RUBBER: A very quiet, dampened tap with a near-silent profile.
     * High decay rate, low amplitude — for quiet environments.
     * Duration: ~25ms
     */
    private fun generateSoftRubber(): ShortArray {
        val durationMs = 25
        val numSamples = SAMPLE_RATE * durationMs / 1000
        val freq = 800.0
        return ShortArray(numSamples) { i ->
            val t = i.toDouble() / SAMPLE_RATE
            val decay = exp(-t * 200.0)
            val wave = sin(2 * PI * freq * t)
            (decay * wave * Short.MAX_VALUE * 0.35).toInt().toShort()
        }
    }

    /**
     * NEON ZAP: An electric retro-synth buzz using a frequency-modulated
     * sawtooth approximation. Drops from 1800Hz to 600Hz over 35ms.
     * Duration: ~35ms
     */
    private fun generateNeonZap(): ShortArray {
        val durationMs = 35
        val numSamples = SAMPLE_RATE * durationMs / 1000
        val freqStart = 1800.0
        val freqEnd   = 600.0
        return ShortArray(numSamples) { i ->
            val t = i.toDouble() / SAMPLE_RATE
            val progress = t / (durationMs / 1000.0)
            val freq = freqStart + (freqEnd - freqStart) * progress
            val decay = exp(-t * 60.0)
            // Sawtooth approximation: sin + harmonics with alternating signs
            val wave = sin(2 * PI * freq * t)       * 0.5 +
                       sin(2 * PI * freq * 2 * t)   * 0.25 +
                       sin(2 * PI * freq * 3 * t)   * 0.15 +
                       sin(2 * PI * freq * 4 * t)   * 0.10
            (decay * wave * Short.MAX_VALUE * 0.8).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
    }

    /**
     * WATER DROP: A rising frequency chirp like a water drop hitting a surface.
     * Sweeps from 300Hz up to 2500Hz with a bell-shaped amplitude envelope.
     * Duration: ~50ms
     */
    private fun generateWaterDrop(): ShortArray {
        val durationMs = 50
        val numSamples = SAMPLE_RATE * durationMs / 1000
        val freqStart = 300.0
        val freqEnd   = 2500.0
        return ShortArray(numSamples) { i ->
            val t = i.toDouble() / SAMPLE_RATE
            val progress = t / (durationMs / 1000.0)
            // Exponential frequency sweep upward
            val freq = freqStart * Math.pow(freqEnd / freqStart, progress)
            // Bell envelope: rises then falls
            val envelope = sin(PI * progress) * exp(-t * 25.0)
            val wave = sin(2 * PI * freq * t)
            (envelope * wave * Short.MAX_VALUE * 0.7).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
        }
    }

    // -----------------------------------------------------------------------
    // AudioTrack PCM playback helper
    // -----------------------------------------------------------------------

    private fun playPcmSamples(samples: ShortArray) {
        if (samples.isEmpty()) return

        val bufferSize = AudioTrack.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        )

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val audioFormat = AudioFormat.Builder()
            .setSampleRate(SAMPLE_RATE)
            .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
            .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
            .build()

        val track = AudioTrack.Builder()
            .setAudioAttributes(audioAttributes)
            .setAudioFormat(audioFormat)
            .setBufferSizeInBytes(maxOf(bufferSize, samples.size * 2))
            .setTransferMode(AudioTrack.MODE_STATIC)
            .build()

        track.write(samples, 0, samples.size)
        track.play()

        // Wait for playback to finish then release resources
        val durationMs = samples.size.toLong() * 1000L / SAMPLE_RATE + 20L
        Thread.sleep(durationMs)
        track.stop()
        track.release()
    }
}
