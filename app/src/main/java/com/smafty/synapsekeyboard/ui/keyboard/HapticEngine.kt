package com.smafty.synapsekeyboard.ui.keyboard

import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

// ---------------------------------------------------------------------------
// HapticEngine — key-tap vibration (haptic feedback) for the IME keyboard.
//
// Mirrors the KeySoundEngine pattern: a lightweight singleton that holds a
// cached enabled flag (read once at IME start), persists to the shared
// "synapse_prefs" SharedPreferences, and triggers a short predefined tick on
// every key press. Default is OFF.
//
// All calls are fail-safe: if the device has no vibrator or the platform
// rejects the effect, the call is silently swallowed so the IME never crashes.
// ---------------------------------------------------------------------------
object HapticEngine {

    private const val PREFS_KEY_HAPTICS = "key_vibration_enabled"

    // Cached flag — read from disk once at IME onCreate, never per key tap.
    // This keeps fast typing lag-free.
    @Volatile
    var enabled: Boolean = false
        private set

    /** Loads the persisted haptics flag from SharedPreferences. */
    fun loadEnabled(prefs: android.content.SharedPreferences) {
        enabled = prefs.getBoolean(PREFS_KEY_HAPTICS, false)
    }

    /** Saves the haptics flag to SharedPreferences and updates the cached value. */
    fun setEnabled(prefs: android.content.SharedPreferences, value: Boolean) {
        prefs.edit().putBoolean(PREFS_KEY_HAPTICS, value).apply()
        enabled = value
    }

    /**
     * Triggers a short haptic tick. Safe to call on every key press from the
     * UI thread — Vibrator calls are non-blocking. No-op when disabled.
     */
    fun vibrate(vibrator: Vibrator?) {
        if (!enabled) return
        val vib = vibrator ?: return
        if (!vib.hasVibrator()) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Standard "tick" predefined effect — matches what system
                // keyboards use. Falls back to EFFECT_CLICK on devices that
                // don't support TICK.
                val effectId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    VibrationEffect.EFFECT_TICK
                } else {
                    VibrationEffect.EFFECT_CLICK
                }
                vib.vibrate(VibrationEffect.createPredefined(effectId))
            } else {
                // Pre-Q fallback: a short 20ms one-shot pulse.
                @Suppress("DEPRECATION")
                vib.vibrate(20)
            }
        } catch (_: Exception) {
            // Vibration must never crash the IME — silently ignore.
        }
    }
}
