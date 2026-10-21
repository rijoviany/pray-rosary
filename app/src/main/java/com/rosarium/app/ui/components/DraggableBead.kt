package com.rosarium.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rosarium.app.data.model.DecadeStep
import com.rosarium.app.physics.PhysicsConstants
import com.rosarium.app.ui.theme.GoldGlow
import com.rosarium.app.ui.theme.MedievalGold
import com.rosarium.app.ui.theme.MedievalGoldBright
import com.rosarium.app.ui.theme.MedievalGoldDark
import com.rosarium.app.ui.theme.ParchmentLight
import com.rosarium.app.ui.theme.PrayerThemes
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * The core physical interaction of Rosarium:
 * Draggable bottom bead with hold-to-pray karaoke timing, rubber-band resistance,
 * spring snap-up physics directly into the Center Receiving Slot, rotation wobble,
 * and tension glow.
 */
@Composable
fun DraggableBead(
    isWhiteBead: Boolean,
    isPrayingActive: Boolean,
    isTapToPrayEnabled: Boolean,
    prayerProgress: Float,
    isSnappingUp: Boolean,
    currentStep: DecadeStep = DecadeStep.HAIL_MARY,
    onHoldStart: () -> Unit,
    onHoldReleaseEarly: () -> Unit,
    onPrayerFinished: () -> Unit,
    onBounceImpact: (decay: Float) -> Unit,
    onStringTension: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    val maxDragPx = with(density) { PhysicsConstants.MAX_DRAG_DISTANCE_DP.dp.toPx() }
    val dragThresholdPx = with(density) { PhysicsConstants.DRAG_THRESHOLD_DP.dp.toPx() }

    // Physical vertical displacement (0f = resting position, >0 = pulled down)
    val dragOffsetY = remember { Animatable(0f) }
    // Rotational wobble on snap-up
    val rotationWobble = remember { Animatable(0f) }

    var isDragging by remember { mutableStateOf(false) }
    var rawAccumulatedDrag by remember { mutableFloatStateOf(0f) }

    // Pulse animation for the resting guidance arrow
    val infiniteTransition = rememberInfiniteTransition(label = "ArrowPulse")
    val arrowFloatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 6.dp.value,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "arrowFloatY"
    )

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        val screenHeightPx = with(density) { maxHeight.toPx() }
        // Exact vertical distance from active bead center (0.81h) to center receiving slot (0.535h)
        val targetReceivingOffsetPx = if (isWhiteBead) {
            -screenHeightPx * 0.52f // Snap up to top Our Father socket (0.29h)
        } else {
            -screenHeightPx * 0.275f // Snap up to center receiving socket (0.535h)
        }

        // Handle Snap-Up Spring Animation when prayer completes
        LaunchedEffect(isSnappingUp) {
            if (isSnappingUp) {
                // Play initial tension release wobble
                launch {
                    rotationWobble.animateTo(
                        targetValue = PhysicsConstants.MAX_WOBBLE_DEGREES,
                        animationSpec = tween(120, easing = FastOutSlowInEasing)
                    )
                    rotationWobble.animateTo(
                        targetValue = -PhysicsConstants.MAX_WOBBLE_DEGREES * 0.6f,
                        animationSpec = tween(140)
                    )
                    rotationWobble.animateTo(0f, animationSpec = tween(160))
                }

                // Spring up with 2-3 realistic decaying bounces right into the receiving slot!
                dragOffsetY.animateTo(
                    targetValue = targetReceivingOffsetPx,
                    animationSpec = PhysicsConstants.SnapUpSpringSpec
                ) {
                    val velocity = velocity
                    if (kotlin.math.abs(velocity) > 800f) {
                        onBounceImpact(0.85f)
                    } else if (kotlin.math.abs(velocity) > 300f) {
                        onBounceImpact(0.45f)
                    }
                }

                onPrayerFinished()
                // Reset position smoothly for the fresh bead
                dragOffsetY.snapTo(0f)
                rawAccumulatedDrag = 0f
                isDragging = false
            }
        }

        val beadYOffsetPx = dragOffsetY.value

        // 1. Vertical Golden Chain connecting Center Receiving Slot downward to Active Bead
        Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val cx = size.width / 2f
            val centerReceivingY = size.height * 0.535f + 14.dp.toPx()
            val beadCenterY = size.height * 0.81f + beadYOffsetPx

            // Golden chain links
            drawVerticalChainLinks(
                cx = cx,
                startY = centerReceivingY,
                endY = beadCenterY - 26.dp.toPx(),
                isUnderTension = isPrayingActive
            )

            // Tension light aura if pulled down
            if (isPrayingActive) {
                val auraBrush = Brush.verticalGradient(
                    colors = listOf(
                        GoldGlow.copy(alpha = 0.25f),
                        MedievalGoldBright.copy(alpha = 0.50f),
                        GoldGlow.copy(alpha = 0.75f)
                    ),
                    startY = centerReceivingY,
                    endY = beadCenterY
                )
                drawRect(
                    brush = auraBrush,
                    topLeft = Offset(cx - 10.dp.toPx(), centerReceivingY),
                    size = androidx.compose.ui.geometry.Size(20.dp.toPx(), beadCenterY - centerReceivingY)
                )
            }
        }

        // 2. The Active Bead with Gesture Detection
        Box(
            modifier = Modifier
                .offset { IntOffset(x = 0, y = (beadYOffsetPx - 130.dp.toPx()).roundToInt()) }
                .graphicsLayer {
                    rotationZ = rotationWobble.value
                }
                .pointerInput(isTapToPrayEnabled) {
                    if (isTapToPrayEnabled) {
                        detectTapGestures(
                            onPress = {
                                onHoldStart()
                                tryAwaitRelease()
                                onHoldReleaseEarly()
                            }
                        )
                    } else {
                        detectDragGestures(
                            onDragStart = {
                                isDragging = true
                                rawAccumulatedDrag = 0f
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                rawAccumulatedDrag = (rawAccumulatedDrag + dragAmount.y).coerceAtLeast(0f)
                                val calculatedOffset = PhysicsConstants.calculateRubberBandedDrag(
                                    rawDrag = rawAccumulatedDrag,
                                    maxDistance = maxDragPx
                                )

                                coroutineScope.launch {
                                    dragOffsetY.snapTo(calculatedOffset)
                                }

                                if (calculatedOffset >= dragThresholdPx && !isPrayingActive) {
                                    onHoldStart()
                                    onStringTension()
                                }
                            },
                            onDragEnd = {
                                isDragging = false
                                rawAccumulatedDrag = 0f
                                if (isPrayingActive) {
                                    onHoldReleaseEarly()
                                }
                                coroutineScope.launch {
                                    dragOffsetY.animateTo(
                                        targetValue = 0f,
                                        animationSpec = PhysicsConstants.EarlyReleaseSpringSpec
                                    )
                                }
                            },
                            onDragCancel = {
                                isDragging = false
                                rawAccumulatedDrag = 0f
                                if (isPrayingActive) {
                                    onHoldReleaseEarly()
                                }
                                coroutineScope.launch {
                                    dragOffsetY.animateTo(
                                        targetValue = 0f,
                                        animationSpec = PhysicsConstants.EarlyReleaseSpringSpec
                                    )
                                }
                            }
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Bead Visual with prayer-specific liturgical color and aura
            Canvas(modifier = Modifier.size(72.dp)) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val radius = 24.dp.toPx()
                val prayerTheme = PrayerThemes.forStep(currentStep)

                // Pulsing glow if active
                if (isPrayingActive) {
                    val glowBrush = Brush.radialGradient(
                        colors = listOf(
                            prayerTheme.auraGlow.copy(alpha = 0.85f),
                            prayerTheme.secondaryAccent.copy(alpha = 0.45f),
                            Color.Transparent
                        ),
                        center = Offset(cx, cy),
                        radius = radius * 2.2f
                    )
                    drawCircle(brush = glowBrush, radius = radius * 2.2f, center = Offset(cx, cy))
                }

                // Draw prayer-specific 3D bead
                drawPrayerBead(
                    step = currentStep,
                    cx = cx,
                    cy = cy,
                    radius = radius,
                    isCompleted = isPrayingActive,
                    pulse = 1f
                )

                // Ornate bottom filigree cap in theme accent
                val filigreePath = Path().apply {
                    moveTo(cx - 10.dp.toPx(), cy + radius * 0.85f)
                    quadraticBezierTo(cx, cy + radius * 1.35f, cx + 10.dp.toPx(), cy + radius * 0.85f)
                }
                drawPath(
                    path = filigreePath,
                    color = prayerTheme.secondaryAccent,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.5.dp.toPx())
                )
            }

            // Downward Pull Indicator Arrow (only shown when resting)
            if (!isPrayingActive && dragOffsetY.value < 10f) {
                Canvas(
                    modifier = Modifier
                        .size(24.dp)
                        .offset { IntOffset(x = 0, y = (42.dp.toPx() + arrowFloatY).roundToInt()) }
                ) {
                    val w = size.width
                    val h = size.height
                    val arrowPath = Path().apply {
                        moveTo(w / 2f, h)
                        lineTo(w * 0.2f, h * 0.4f)
                        lineTo(w * 0.4f, h * 0.4f)
                        lineTo(w * 0.4f, 0f)
                        lineTo(w * 0.6f, 0f)
                        lineTo(w * 0.6f, h * 0.4f)
                        lineTo(w * 0.8f, h * 0.4f)
                        close()
                    }
                    drawPath(
                        path = arrowPath,
                        brush = Brush.verticalGradient(
                            listOf(MedievalGoldBright, MedievalGoldDark)
                        )
                    )
                }
            }
        }
    }
}

/**
 * Draws vertical interlocking chain links with tension styling.
 */
private fun DrawScope.drawVerticalChainLinks(
    cx: Float,
    startY: Float,
    endY: Float,
    isUnderTension: Boolean
) {
    if (endY <= startY) return

    val linkSpacing = 9.dp.toPx()
    val steps = ((endY - startY) / linkSpacing).toInt().coerceAtLeast(1)

    for (i in 0..steps) {
        val ly = startY + (i * linkSpacing)
        val linkW = 5.dp.toPx()
        val linkH = 7.dp.toPx()

        val linkColor = if (isUnderTension) MedievalGoldBright else MedievalGold

        drawOval(
            color = Color.Black.copy(alpha = 0.35f),
            topLeft = Offset(cx - linkW / 2f + 1.dp.toPx(), ly - linkH / 2f + 1.dp.toPx()),
            size = androidx.compose.ui.geometry.Size(linkW, linkH)
        )

        drawOval(
            color = linkColor,
            topLeft = Offset(cx - linkW / 2f, ly - linkH / 2f),
            size = androidx.compose.ui.geometry.Size(linkW, linkH),
            style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.2.dp.toPx())
        )
    }
}
