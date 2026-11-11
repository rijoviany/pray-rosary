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

    /**
     * Procedural paper tearing sound synthesized via PCM audio:
     * Generates a rapid high-passed burst with jagged fiber-snap modulation.
     */
    fun playPaperTear(volume: Float = 0.85f) {
        Thread {
            try {
                val sampleRate = 22050
                val durationMs = 240
                val numSamples = (sampleRate * durationMs) / 1000
                val buffer = ShortArray(numSamples)
                val random = java.util.Random()
                var filterState = 0f

                for (i in 0 until numSamples) {
                    val progress = i.toFloat() / numSamples
                    val whiteNoise = (random.nextFloat() * 2f - 1f)
                    // High-pass filter simulating paper friction
                    filterState = 0.62f * filterState + 0.38f * whiteNoise
                    val highPassed = whiteNoise - filterState

                    // Jagged tearing fiber bursts
                    val burstModulation = 0.55f + 0.45f * kotlin.math.sin(progress * 52.0).toFloat()
                    val attack = if (progress < 0.08f) progress / 0.08f else 1f
                    val decay = 1f - progress
                    val envelope = attack * decay * burstModulation

                    val sampleValue = (highPassed * envelope * 22000f * volume).toInt()
                    buffer[i] = sampleValue.coerceIn(-32768, 32767).toShort()
                }

                val track = android.media.AudioTrack.Builder()
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_GAME)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .build()
                    )
                    .setAudioFormat(
                        android.media.AudioFormat.Builder()
                            .setEncoding(android.media.AudioFormat.ENCODING_PCM_16BIT)
                            .setSampleRate(sampleRate)
                            .setChannelMask(android.media.AudioFormat.CHANNEL_OUT_MONO)
                            .build()
                    )
                    .setBufferSizeInBytes(numSamples * 2)
                    .setTransferMode(android.media.AudioTrack.MODE_STATIC)
                    .build()

                track.write(buffer, 0, numSamples)
                track.play()
                Thread.sleep(durationMs.toLong() + 40L)
                track.release()
            } catch (e: Exception) {
                playStringTension(volume)
            }
        }.start()
    }

    private fun getVibrator(): Vibrator? {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator ?: (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }
        } catch (_: Exception) {
            try {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            } catch (_: Exception) {
                null
            }
        }
    }

    /**
     * Executes robust vibration across all Android versions and OEM devices.
     * Uses USAGE_ALARM to prevent OEM suppression when system "touch feedback" is disabled,
     * with cascaded fallbacks to ensure ERM and LRA vibration motors actuate properly.
     */
    private fun safeVibrate(
        durationMs: Long,
        amplitude: Int = VibrationEffect.DEFAULT_AMPLITUDE,
        pattern: LongArray? = null
    ) {
        val v = getVibrator() ?: return
        if (!v.hasVibrator()) return

        val hasAmp = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try { v.hasAmplitudeControl() } catch (_: Exception) { false }
        } else false

        val effectiveAmp = if (hasAmp && amplitude > 0) amplitude.coerceIn(1, 255) else VibrationEffect.DEFAULT_AMPLITUDE

        // USAGE_ALARM ensures vibration is delivered even if system touch haptics are turned off
        val alarmAttrs = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()

        var success = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val effect = if (pattern != null) {
                    if (hasAmp) {
                        val amps = IntArray(pattern.size) { idx -> if (idx % 2 == 1) effectiveAmp else 0 }
                        VibrationEffect.createWaveform(pattern, amps, -1)
                    } else {
                        VibrationEffect.createWaveform(pattern, -1)
                    }
                } else {
                    VibrationEffect.createOneShot(durationMs, effectiveAmp)
                }
                v.vibrate(effect, alarmAttrs)
                success = true
            } catch (e: Exception) {
                Log.w("RosaryAudio", "Alarm attrs vibrate failed, falling back", e)
            }
        }

        if (!success && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val effect = if (pattern != null) {
                    VibrationEffect.createWaveform(pattern, -1)
                } else {
                    VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
                }
                v.vibrate(effect)
                success = true
            } catch (_: Exception) {}
        }

        // Direct legacy fallback: guaranteed on 100% of Android hardware
        if (!success) {
            try {
                @Suppress("DEPRECATION")
                if (pattern != null) {
                    v.vibrate(pattern, -1)
                } else {
                    v.vibrate(durationMs)
                }
            } catch (e: Exception) {
                Log.e("RosaryAudio", "Legacy vibrate failed", e)
            }
        }
    }

    /**
     * Tactile vibration pulse pattern simulating paper fibers tearing:
     * A series of micro ticks with progressive acceleration and crisp cutoff.
     */
    fun performPaperTearHaptic() {
        safeVibrate(
            durationMs = 90L,
            amplitude = 230,
            pattern = longArrayOf(0, 25, 20, 35, 15, 60)
        )
    }

    /**
     * 1st Haptic: Clean, crisp tactile click when the bead is clicked / touch starts.
     */
    fun performHapticTick() {
        safeVibrate(durationMs = 50L, amplitude = 220)
    }

    /**
     * 2nd Haptic: Distinct tactile vibration when prayer duration finishes and bead is ready to release.
     */
    fun performReleaseReadyHaptic() {
        safeVibrate(durationMs = 120L, amplitude = 255)
    }

    fun performHapticPulse() {
        safeVibrate(durationMs = 70L, amplitude = 180)
    }

    fun performImpactClack(decayLevel: Float = 1.0f) {
        val amp = (255 * decayLevel.coerceIn(0.4f, 1.0f)).toInt()
        val dur = (80L * decayLevel.coerceIn(0.5f, 1.0f)).toLong().coerceAtLeast(45L)
        safeVibrate(durationMs = dur, amplitude = amp)
    }

    fun release() {
        soundPool.release()
    }
}
