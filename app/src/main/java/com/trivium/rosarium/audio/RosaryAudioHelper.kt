package com.trivium.rosarium.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

class RosaryAudioHelper(private val context: Context) {

    private val soundPool: SoundPool
    private val soundIds = mutableMapOf<String, Int>()
    private var isLoaded = false

    private val vibrator: Vibrator? = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator ?: (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    } catch (_: Exception) {
        null
    }

    init {
        val attributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_GAME)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(attributes)
            .build()

        loadSounds()
    }

    private fun loadSounds() {
        val soundNames = listOf("bead_clack", "string_tension", "prayer_bell", "bead_snap")
        for (name in soundNames) {
            val resId = context.resources.getIdentifier(name, "raw", context.packageName)
            if (resId != 0) {
                try {
                    val id = soundPool.load(context, resId, 1)
                    soundIds[name] = id
                } catch (e: Exception) {
                    Log.w("RosaryAudio", "Could not load sound $name", e)
                }
            }
        }
        isLoaded = true
    }

    fun playBeadClack(volume: Float = 1.0f) {
        val id = soundIds["bead_clack"] ?: soundIds["bead_snap"]
        if (id != null && id != 0) {
            soundPool.play(id, volume, volume, 1, 0, 1.0f)
        }
    }

    fun playStringTension(volume: Float = 0.5f) {
        val id = soundIds["string_tension"]
        if (id != null && id != 0) {
            soundPool.play(id, volume, volume, 1, 0, 1.0f)
        }
    }

    fun playCompletionBell(volume: Float = 0.8f) {
        val id = soundIds["prayer_bell"]
        if (id != null && id != 0) {
            soundPool.play(id, volume, volume, 1, 0, 1.0f)
        }
    }

    fun performHapticTick() {
        val v = vibrator ?: (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator) ?: return
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .build()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK), audioAttributes)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(30, 180), audioAttributes)
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(30)
            }
        } catch (_: Exception) {
            try {
                @Suppress("DEPRECATION")
                v.vibrate(30)
            } catch (_: Exception) {}
        }
    }

    /**
     * Tactile, distinct vibration specifically when the bead has completed its prayer duration
     * and is ready to be released by the user.
     * Uses USAGE_ASSISTANCE_SONIFICATION to bypass OEM background suppression, and a double pulse
     * (70ms pulse, 50ms gap, 70ms pulse) at max amplitude (255) so it is guaranteed to actuate both
     * high-end LRA motors and standard ERM rotational motors.
     */
    fun performReleaseReadyHaptic() {
        val v = vibrator ?: (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator) ?: return
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .build()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val timings = longArrayOf(0, 70, 50, 70)
                val amplitudes = intArrayOf(0, 255, 0, 255)
                val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
                v.vibrate(effect, audioAttributes)
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(longArrayOf(0, 70, 50, 70), -1)
            }
        } catch (e: Exception) {
            try {
                @Suppress("DEPRECATION")
                v.vibrate(100)
            } catch (_: Exception) {}
        }
    }

    fun performHapticPulse() {
        val v = vibrator ?: (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator) ?: return
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .build()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(35, 200), audioAttributes)
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(35)
            }
        } catch (_: Exception) {
            try {
                @Suppress("DEPRECATION")
                v.vibrate(35)
            } catch (_: Exception) {}
        }
    }

    fun performImpactClack(decayLevel: Float = 1.0f) {
        val v = vibrator ?: (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator) ?: return
        val amplitude = (255 * decayLevel.coerceIn(0.2f, 1.0f)).toInt()
        try {
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_SONIFICATION)
                .build()

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(45, amplitude), audioAttributes)
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(45)
            }
        } catch (_: Exception) {
            try {
                @Suppress("DEPRECATION")
                v.vibrate(45)
            } catch (_: Exception) {}
        }
    }

    fun release() {
        soundPool.release()
    }
}
