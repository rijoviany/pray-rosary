package com.trivium.rosarium.physics

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.spring

/**
 * Single source of truth for all physics, drag limits, spring simulations,
 * and bounce parameters in Rosarium.
 * All constants are easily tunable here.
 */
object PhysicsConstants {

    // --- Drag & Rubberband Constants ---
    /** Distance in DP required to engage the hold-to-pray gesture */
    const val DRAG_THRESHOLD_DP = 70f

    /** Maximum allowed visual drag distance in DP before strong rubber-band resistance */
    const val MAX_DRAG_DISTANCE_DP = 120f

    /** Rubber-band resistance factor past the MAX_DRAG_DISTANCE_DP (lower = stiffer) */
    const val RUBBER_BAND_COEFFICIENT = 0.28f

    // --- Hold-to-Pray Karaoke Timing ---
    /** Minimum and default duration for Hail Mary (5 seconds) */
    const val MIN_HAIL_MARY_DURATION_MS = 5_000L

    /** Minimum and default duration for Our Father (10 seconds) */
    const val MIN_OUR_FATHER_DURATION_MS = 10_000L

    /** Default duration to recite a full prayer while holding the bead */
    const val DEFAULT_PRAYER_DURATION_MS = 5_000L

    /** Interval for updating progress and evaluating karaoke phrases */
    const val PROGRESS_TICK_INTERVAL_MS = 32L // ~30 fps updates for smooth text highlighting

    /** Interval between subtle rhythmic haptic pulses while held down */
    const val HAPTIC_PULSE_INTERVAL_MS = 1200L

    // --- Snap-Up & Settle Animation Specs ---
    /**
     * Damping ratio for the snap-up spring animation:
     * Lower value (< 0.5) creates 2-3 expressive decaying bounces.
     */
    const val SNAP_DAMPING_RATIO = 0.45f

    /**
     * Stiffness for the snap-up spring:
     * Spring.StiffnessLow creates a graceful, realistic physical weight.
     */
    const val SNAP_STIFFNESS = Spring.StiffnessLow

    /** Compose spring spec for the bead snapping up to the chain */
    val SnapUpSpringSpec: SpringSpec<Float> = spring(
        dampingRatio = SNAP_DAMPING_RATIO,
        stiffness = SNAP_STIFFNESS
    )

    /** Early release spring: snappy return when user lets go before finishing prayer */
    val EarlyReleaseSpringSpec: SpringSpec<Float> = spring(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessMedium
    )

    // --- Decaying Wobble & Bounce Physics ---
    /** Maximum rotational wobble degrees on snap impact */
    const val MAX_WOBBLE_DEGREES = 14f

    /** Number of decaying oscillations on impact */
    const val BOUNCE_COUNT = 3

    /** Decay factor per bounce */
    const val BOUNCE_DECAY_FACTOR = 0.42f

    /**
     * Calculates the rubber-banded offset for any raw drag distance (in pixels or dp).
     * If distance <= maxDistance, returns distance.
     * If distance > maxDistance, smoothly decelerates using logarithmic dampening.
     */
    fun calculateRubberBandedDrag(rawDrag: Float, maxDistance: Float): Float {
        return if (rawDrag <= maxDistance) {
            rawDrag
        } else {
            val overflow = rawDrag - maxDistance
            maxDistance + (overflow * RUBBER_BAND_COEFFICIENT)
        }
    }
}
