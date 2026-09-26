package com.example.audio

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class SoundFeedbackManager(private val context: Context) {

    private var toneGenerator: ToneGenerator? = null
    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
        } catch (e: Exception) {
            toneGenerator = null
        }
    }

    fun playCountdownBeep(soundEnabled: Boolean = true, vibrationEnabled: Boolean = true) {
        if (soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
            } catch (e: Exception) {
                // Ignore audio errors
            }
        }
        if (vibrationEnabled) {
            vibrate(80)
        }
    }

    fun playWorkStartBeep(soundEnabled: Boolean = true, vibrationEnabled: Boolean = true) {
        if (soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_ACK, 350)
            } catch (e: Exception) {
                // Ignore audio errors
            }
        }
        if (vibrationEnabled) {
            vibrate(200)
        }
    }

    fun playRestStartBeep(soundEnabled: Boolean = true, vibrationEnabled: Boolean = true) {
        if (soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 350)
            } catch (e: Exception) {
                // Ignore audio errors
            }
        }
        if (vibrationEnabled) {
            vibrate(150)
        }
    }

    fun playFinishBeep(soundEnabled: Boolean = true, vibrationEnabled: Boolean = true) {
        if (soundEnabled) {
            try {
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_HIGH_L, 700)
            } catch (e: Exception) {
                // Ignore audio errors
            }
        }
        if (vibrationEnabled) {
            vibratePattern(longArrayOf(0, 200, 100, 300))
        }
    }

    private fun vibrate(durationMs: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (e: Exception) {
            // Ignore vibration errors
        }
    }

    private fun vibratePattern(pattern: LongArray) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, -1)
            }
        } catch (e: Exception) {
            // Ignore vibration errors
        }
    }

    fun release() {
        toneGenerator?.release()
        toneGenerator = null
    }
}
