package com.smafty.synapsekeyboard.auth

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.util.Log
import java.security.MessageDigest

private const val TAG = "DeviceUtils"

/**
 * DeviceUtils — Generates a stable, privacy-safe device fingerprint.
 *
 * The fingerprint is built from:
 *   - ANDROID_ID  : unique per device + app signing key (resets on factory reset)
 *   - Build.MANUFACTURER : e.g. "samsung", "google"
 *   - Build.MODEL        : e.g. "Pixel 7", "SM-S918B"
 *   - Build.BOARD        : hardware board identifier
 *
 * All combined and SHA-256 hashed → 64-char lowercase hex string.
 *
 * WHY SHA-256 instead of storing raw values?
 *   1. Privacy  — ANDROID_ID is PII; hashing prevents exposing it in Supabase DB
 *   2. Fixed length — always 64 hex chars regardless of input
 *   3. One-way — cannot reverse-engineer device info from the stored hash
 *   4. Collision-resistant — two different devices produce different hashes
 *   5. GDPR-friendly — storing a hash is safer than raw device identifiers
 */
@SuppressLint("HardwareIds")
fun generateDeviceFingerprint(context: Context): String {
    return try {
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        ) ?: "unknown"

        // Combine stable hardware identifiers
        val raw = "${androidId}_${Build.MANUFACTURER}_${Build.MODEL}_${Build.BOARD}"

        // SHA-256 hash → hex string
        val digest = MessageDigest.getInstance("SHA-256")
        val hashBytes = digest.digest(raw.toByteArray(Charsets.UTF_8))
        val fingerprint = hashBytes.joinToString("") { "%02x".format(it) }

        Log.d(TAG, "Device fingerprint generated (${fingerprint.take(8)}...)")
        fingerprint

    } catch (e: Exception) {
        // Fallback: use a truncated ANDROID_ID hash if anything fails
        Log.w(TAG, "Fingerprint generation failed, using fallback: ${e.message}")
        try {
            val androidId = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
            ) ?: "fallback_unknown"
            val digest = MessageDigest.getInstance("SHA-256")
            val hashBytes = digest.digest(androidId.toByteArray(Charsets.UTF_8))
            hashBytes.joinToString("") { "%02x".format(it) }
        } catch (ex: Exception) {
            Log.e(TAG, "Fingerprint fallback also failed: ${ex.message}")
            "fallback_no_device_id"
        }
    }
}
