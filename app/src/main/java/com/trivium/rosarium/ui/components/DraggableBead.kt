package com.trivium.rosarium.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trivium.rosarium.data.model.DecadeStep
import com.trivium.rosarium.physics.PhysicsConstants
import com.trivium.rosarium.ui.theme.GoldGlow
import com.trivium.rosarium.ui.theme.MedievalGold
import com.trivium.rosarium.ui.theme.MedievalGoldBright
import com.trivium.rosarium.ui.theme.MedievalGoldDark
import com.trivium.rosarium.ui.theme.PrayerThemes
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * The core physical interaction of Rosarium:
 * Draggable active bead suspended on a vertical golden chain directly below the prayer banner.
 * Drag downward to pray. When held 5s (10s for Our Father), releasing the bead rolls it up
 * into the top center socket, which then moves left along the chain.
 */
@Composable
fun DraggableBead(
    isWhiteBead: Boolean,
    completedBeadIndex: Int = 0,
    isPrayingActive: Boolean,
    isReadyToRelease: Boolean,
    isTapToPrayEnabled: Boolean,
    prayerProgress: Float,
    isSnappingUp: Boolean,
    currentStep: DecadeStep = DecadeStep.HAIL_MARY,
    onHoldStart: () -> Unit,
    onReleaseBead: () -> Unit,
    onPrayerFinished: () -> Unit,
    onBounceImpact: (decay: Float) -> Unit,
    onStringTension: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()

    // Up-to-date state references to prevent stale closures in pointer gestures
    val currentOnHoldStart by rememberUpdatedState(onHoldStart)
    val currentOnReleaseBead by rememberUpdatedState(onReleaseBead)
    val currentOnPrayerFinished by rememberUpdatedState(onPrayerFinished)
    val currentOnBounceImpact by rememberUpdatedState(onBounceImpact)
    val currentOnStringTension by rememberUpdatedState(onStringTension)
    val currentIsPrayingActive by rememberUpdatedState(isPrayingActive)
    val currentIsReadyToRelease by rememberUpdatedState(isReadyToRelease)
    val currentIsSnappingUp by rememberUpdatedState(isSnappingUp)

    val maxDragPx = with(density) { 90.dp.toPx() }
    val dragThresholdPx = with(density) { 34.dp.toPx() }
    val restingBeadCenterYPx = with(density) { 28.dp.toPx() }
    val snapUpTargetOffsetPx = with(density) { -35.dp.toPx() }
    val indicatorBaseOffsetPx = with(density) { 36.dp.toPx() }

    // Physical vertical displacement (0f = resting position just below banner, >0 = pulled down)
    val dragOffsetY = remember { Animatable(0f) }
    // Rotational wobble on snap-up
    val rotationWobble = remember { Animatable(0f) }

    var isDragging by remember { mutableStateOf(false) }
    var rawAccumulatedDrag by remember { mutableFloatStateOf(0f) }

    // Pulse animation for the resting guidance arrow
    val infiniteTransition = rememberInfiniteTransition(label = "ArrowPulse")
    val arrowFloatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 5.dp.value,
        animationSpec = infiniteRepeatable(
            animation = tween(850, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "arrowFloatY"
    )

    // Handle Snap-Up Spring Animation when prayer completes
    LaunchedEffect(isSnappingUp) {
        if (isSnappingUp) {
            try {
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

                // Crisp snap up towards the top center socket
                dragOffsetY.animateTo(
                    targetValue = snapUpTargetOffsetPx,
                    animationSpec = tween(220, easing = FastOutSlowInEasing)
                ) {
                    currentOnBounceImpact(0.8f)
                }

                // Settle smoothly back to 0f
                dragOffsetY.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(60)
                )
            } finally {
                // Guaranteed reset of offsets and drag flags
                dragOffsetY.snapTo(0f)
                rawAccumulatedDrag = 0f
                isDragging = false
            }
            // Advance to the next bead
            currentOnPrayerFinished()
        }
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        val beadYOffsetPx = dragOffsetY.value
        val beadCenterY = restingBeadCenterYPx + beadYOffsetPx

        // 1. Vertical Golden Chain connecting Banner Cross downward to Active Bead
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val startChainY = 0f
            val endChainY = beadCenterY - 24.dp.toPx()

            // Golden chain links
            drawVerticalChainLinks(
                cx = cx,
                startY = startChainY,
                endY = endChainY,
                isUnderTension = currentIsPrayingActive || isDragging
            )

            // Tension light aura along chain if pulled down
            if (currentIsPrayingActive || beadYOffsetPx > 15f) {
                val auraBrush = Brush.verticalGradient(
                    colors = listOf(
                        GoldGlow.copy(alpha = 0.20f),
                        MedievalGoldBright.copy(alpha = if (currentIsReadyToRelease) 0.65f else 0.45f),
                        GoldGlow.copy(alpha = if (currentIsReadyToRelease) 0.85f else 0.70f)
                    ),
                    startY = startChainY,
                    endY = beadCenterY
                )
                drawRect(
                    brush = auraBrush,
                    topLeft = Offset(cx - 8.dp.toPx(), startChainY),
                    size = androidx.compose.ui.geometry.Size(16.dp.toPx(), (beadCenterY - startChainY).coerceAtLeast(0f))
                )
            }
        }

        // 2. The Active Bead with Gesture Detection (Generous 80x80 dp touch target)
        val beadTouchSizeDp = 80.dp
        val beadHalfSizePx = with(density) { (beadTouchSizeDp / 2).toPx() }
        val beadBoxY = (restingBeadCenterYPx - beadHalfSizePx + beadYOffsetPx).roundToInt()

        Box(
            modifier = Modifier
                .offset { IntOffset(x = 0, y = beadBoxY) }
                .size(beadTouchSizeDp)
                .graphicsLayer {
                    rotationZ = rotationWobble.value
                }
                .pointerInput(isTapToPrayEnabled, currentStep, completedBeadIndex) {
                    if (isTapToPrayEnabled) {
                        detectTapGestures(
                            onTap = {
                                if (!currentIsPrayingActive && !currentIsSnappingUp) {
                                    currentOnHoldStart()
                                    currentOnStringTension()
                                    coroutineScope.launch {
                                        dragOffsetY.animateTo(
                                            targetValue = dragThresholdPx,
                                            animationSpec = tween(140)
                                        )
                                    }
                                }
                            }
                        )
                    } else {
                        detectDragGestures(
                            onDragStart = {
                                if (!currentIsPrayingActive && !currentIsSnappingUp) {
                                    isDragging = true
                                    rawAccumulatedDrag = 0f
                                    coroutineScope.launch {
                                        dragOffsetY.stop()
                                    }
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                if (!currentIsSnappingUp) {
                                    rawAccumulatedDrag = (rawAccumulatedDrag + dragAmount.y).coerceAtLeast(0f)
                                    val calculatedOffset = PhysicsConstants.calculateRubberBandedDrag(
                                        rawDrag = rawAccumulatedDrag,
                                        maxDistance = maxDragPx
                                    )

                                    coroutineScope.launch {
                                        dragOffsetY.snapTo(calculatedOffset)
                                    }

                                    if (calculatedOffset >= dragThresholdPx && !currentIsPrayingActive) {
                                        currentOnHoldStart()
                                        currentOnStringTension()
                                    }
                                }
                            },
                            onDragEnd = {
                                isDragging = false
                                rawAccumulatedDrag = 0f
                                if (currentIsPrayingActive) {
                                    if (!currentIsReadyToRelease) {
                                        coroutineScope.launch {
                                            dragOffsetY.animateTo(0f, tween(150))
                                        }
                                    }
                                    currentOnReleaseBead()
                                } else {
                                    coroutineScope.launch {
                                        dragOffsetY.animateTo(
                                            targetValue = 0f,
                                            animationSpec = PhysicsConstants.EarlyReleaseSpringSpec
                                        )
                                    }
                                }
                            },
                            onDragCancel = {
                                isDragging = false
                                rawAccumulatedDrag = 0f
                                if (currentIsPrayingActive) {
                                    if (!currentIsReadyToRelease) {
                                        coroutineScope.launch {
                                            dragOffsetY.animateTo(0f, tween(150))
                                        }
                                    }
                                    currentOnReleaseBead()
                                } else {
                                    coroutineScope.launch {
                                        dragOffsetY.animateTo(
                                            targetValue = 0f,
                                            animationSpec = PhysicsConstants.EarlyReleaseSpringSpec
                                        )
                                    }
                                }
                            }
                        )
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            // Bead Visual with prayer-specific liturgical color and aura
            Canvas(modifier = Modifier.size(68.dp)) {
                val cx = size.width / 2f
                val cy = size.height / 2f
                val radius = 22.dp.toPx()
                val prayerTheme = PrayerThemes.forStep(currentStep)

                // Pulsing glow if active
                if (currentIsPrayingActive) {
                    val glowBrush = Brush.radialGradient(
                        colors = if (currentIsReadyToRelease) {
                            listOf(
                                MedievalGoldBright,
                                GoldGlow.copy(alpha = 0.75f),
                                Color.Transparent
                            )
                        } else {
                            listOf(
                                prayerTheme.auraGlow.copy(alpha = 0.85f),
                                prayerTheme.secondaryAccent.copy(alpha = 0.45f),
                                Color.Transparent
                            )
                        },
                        center = Offset(cx, cy),
                        radius = radius * (if (currentIsReadyToRelease) 2.5f else 2.2f)
                    )
                    drawCircle(brush = glowBrush, radius = radius * 2.3f, center = Offset(cx, cy))
                }

                // Draw prayer-specific 3D bead
                drawPrayerBead(
                    step = currentStep,
                    cx = cx,
                    cy = cy,
                    radius = radius,
                    isCompleted = currentIsPrayingActive,
                    pulse = 1f
                )

                // Ornate bottom filigree cap in theme accent
                val filigreePath = Path().apply {
                    moveTo(cx - 9.dp.toPx(), cy + radius * 0.82f)
                    quadraticTo(cx, cy + radius * 1.30f, cx + 9.dp.toPx(), cy + radius * 0.82f)
                }
                drawPath(
                    path = filigreePath,
                    color = if (currentIsReadyToRelease) MedievalGoldBright else prayerTheme.secondaryAccent,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.2.dp.toPx())
                )
            }
        }

        // 3. Status Guidance Indicator
        val arrowFloatPx = with(density) { arrowFloatY.dp.toPx() }
        val indicatorY = (restingBeadCenterYPx + indicatorBaseOffsetPx + beadYOffsetPx + arrowFloatPx).roundToInt()

        if (!currentIsPrayingActive && beadYOffsetPx < 12f) {
            // Resting state: DOWN arrow & "DRAG DOWN"
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset {
                    IntOffset(x = 0, y = indicatorY)
                }
            ) {
                Canvas(modifier = Modifier.size(16.dp)) {
                    val w = size.width
                    val h = size.height
                    val arrowPath = Path().apply {
                        moveTo(w / 2f, h)
                        lineTo(w * 0.15f, h * 0.42f)
                        lineTo(w * 0.38f, h * 0.42f)
                        lineTo(w * 0.38f, 0f)
                        lineTo(w * 0.62f, 0f)
                        lineTo(w * 0.62f, h * 0.42f)
                        lineTo(w * 0.85f, h * 0.42f)
                        close()
                    }
                    drawPath(
                        path = arrowPath,
                        brush = Brush.verticalGradient(
                            listOf(MedievalGoldBright, MedievalGoldDark)
                        )
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "DRAG DOWN",
                    color = MedievalGoldBright,
                    fontSize = 10.5.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp
                )
            }
        } else if (currentIsPrayingActive && currentIsReadyToRelease) {
            // Completed 5s hold: UP arrow & "RELEASE"
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset {
                    IntOffset(x = 0, y = indicatorY)
                }
            ) {
                Canvas(modifier = Modifier.size(16.dp)) {
                    val w = size.width
                    val h = size.height
                    val arrowPath = Path().apply {
                        moveTo(w / 2f, 0f)
                        lineTo(w * 0.15f, h * 0.58f)
                        lineTo(w * 0.38f, h * 0.58f)
                        lineTo(w * 0.38f, h)
                        lineTo(w * 0.62f, h)
                        lineTo(w * 0.62f, h * 0.58f)
                        lineTo(w * 0.85f, h * 0.58f)
                        close()
                    }
                    drawPath(
                        path = arrowPath,
                        brush = Brush.verticalGradient(
                            listOf(MedievalGoldBright, MedievalGoldDark)
                        )
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "RELEASE",
                    color = MedievalGoldBright,
                    fontSize = 11.5.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.8.sp
                )
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

    val linkSpacing = 8.dp.toPx()
    val steps = ((endY - startY) / linkSpacing).toInt().coerceAtLeast(1)

    for (i in 0..steps) {
        val ly = startY + (i * linkSpacing)
        val linkW = 4.8.dp.toPx()
        val linkH = 6.8.dp.toPx()

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
