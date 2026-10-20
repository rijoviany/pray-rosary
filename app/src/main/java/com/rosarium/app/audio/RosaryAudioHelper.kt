package com.rosarium.app.audio

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

    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
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
        vibrator?.let { v ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                v.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(15)
            }
        }
    }

    fun performHapticPulse() {
        vibrator?.let { v ->
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                v.vibrate(VibrationEffect.createOneShot(25, 80))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(25)
            }
        }
    }

    fun performImpactClack(decayLevel: Float = 1.0f) {
        vibrator?.let { v ->
            val amplitude = (255 * decayLevel.coerceIn(0.1f, 1.0f)).toInt()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                v.vibrate(VibrationEffect.createOneShot(35, amplitude))
            } else {
                @Suppress("DEPRECATION")
                v.vibrate(35)
            }
        }
    }

    fun release() {
        soundPool.release()
    }
}
