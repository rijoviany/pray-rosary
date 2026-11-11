package com.trivium.rosarium.ui.components

import android.os.Build
import android.view.HapticFeedbackConstants
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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
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
import com.trivium.rosarium.ui.theme.UltramarineDark
import com.trivium.rosarium.ui.theme.UltramarineLapis
import com.trivium.rosarium.ui.theme.UltramarineSpecular
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

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
    primaryColor: Color = UltramarineLapis,
    specularColor: Color = UltramarineSpecular,
    darkColor: Color = UltramarineDark,
    targetSnapOffsetPx: Float = -1000f,
    prayerDurationMs: Long = 5_000L,
    isDark: Boolean = false,
    onHoldStart: () -> Unit,
    onReleaseBead: () -> Unit,
    onPrayerFinished: () -> Unit,
    onBounceImpact: (decay: Float) -> Unit,
    onStringTension: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val coroutineScope = rememberCoroutineScope()
    val view = LocalView.current

    // Up-to-date state references to prevent stale closures in pointer gestures
    val currentOnHoldStart by rememberUpdatedState(onHoldStart)
    val currentOnReleaseBead by rememberUpdatedState(onReleaseBead)
    val currentOnPrayerFinished by rememberUpdatedState(onPrayerFinished)
    val currentOnBounceImpact by rememberUpdatedState(onBounceImpact)
    val currentOnStringTension by rememberUpdatedState(onStringTension)
    val currentIsPrayingActive by rememberUpdatedState(isPrayingActive)
    val currentIsReadyToRelease by rememberUpdatedState(isReadyToRelease)
    val currentIsSnappingUp by rememberUpdatedState(isSnappingUp)

    val maxDragPx = with(density) { 68.dp.toPx() }
    val dragThresholdPx = with(density) { 26.dp.toPx() }
    val restingBeadCenterYPx = with(density) { 20.dp.toPx() }
    val snapUpTargetOffsetPx = with(density) { -35.dp.toPx() }
    val indicatorBaseOffsetPx = with(density) { 30.dp.toPx() }

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
                // If an existing bead is occupying the top center socket,
                // pause briefly so the top bead glides out of the socket before this bead launches
                if (!isWhiteBead || completedBeadIndex > 0) {
                    delay(130L)
                }

                // Play tension release wobble during upward flight
                launch {
                    rotationWobble.animateTo(
                        targetValue = PhysicsConstants.MAX_WOBBLE_DEGREES * 1.2f,
                        animationSpec = tween(90, easing = FastOutSlowInEasing)
                    )
                    rotationWobble.animateTo(
                        targetValue = -PhysicsConstants.MAX_WOBBLE_DEGREES * 0.7f,
                        animationSpec = tween(110)
                    )
                    rotationWobble.animateTo(0f, animationSpec = tween(120))
                }

                val targetOffset = if (targetSnapOffsetPx < -80f) targetSnapOffsetPx else with(density) { -410.dp.toPx() }
                val bounceOvershootPx = with(density) { 7.dp.toPx() }

                // 1. High-speed upward elastic flight reaching the vacated central socket
                dragOffsetY.animateTo(
                    targetValue = targetOffset,
                    animationSpec = tween(260, easing = FastOutSlowInEasing)
                )

                // 2. Initial impact clack sound & haptic
                currentOnBounceImpact(1.0f)

                // 1st rebound: overshoots slightly into the socket claws
                dragOffsetY.animateTo(
                    targetValue = targetOffset - bounceOvershootPx,
                    animationSpec = tween(70, easing = FastOutSlowInEasing)
                )

                // 2nd rebound: bounces back down slightly with decay
                dragOffsetY.animateTo(
                    targetValue = targetOffset + (bounceOvershootPx * 0.45f),
                    animationSpec = tween(80, easing = FastOutSlowInEasing)
                ) {
                    currentOnBounceImpact(0.4f)
                }

                // Final settlement: snaps firmly and clings into the central socket
                dragOffsetY.animateTo(
                    targetValue = targetOffset,
                    animationSpec = tween(60, easing = FastOutSlowInEasing)
                )

                // Hold clinging in socket for a brief beat before rosary shifts left
                delay(90L)

            } finally {
                // Guaranteed reset of offsets and drag flags
                dragOffsetY.snapTo(0f)
                rawAccumulatedDrag = 0f
                isDragging = false
            }
            // Advance to the next bead -> BeadChain shifts entire rosary towards left!
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

        // 2. The Active Bead with Gesture Detection (Generous touch target)
        val beadTouchSizeDp = 88.dp
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
            Canvas(modifier = Modifier.size(88.dp)) {
                val cx = size.width / 2f
                val cy = size.height / 2f

                // Scale bead radius smoothly as it flies into the top central socket
                val fullRadiusPx = 22.dp.toPx()
                val socketRadiusPx = if (isWhiteBead) 11.5.dp.toPx() else 9.2.dp.toPx()
                val currentTargetOffset = if (targetSnapOffsetPx < -80f) targetSnapOffsetPx else -410.dp.toPx()
                val flightFrac = if (currentTargetOffset != 0f && beadYOffsetPx < 0f) {
                    (beadYOffsetPx / currentTargetOffset).coerceIn(0f, 1f)
                } else {
                    0f
                }
                val radius = fullRadiusPx + (socketRadiusPx - fullRadiusPx) * flightFrac

                val prayerTheme = PrayerThemes.forStep(currentStep)
                val activeThemeGlow = if (currentStep == DecadeStep.HAIL_MARY) primaryColor else prayerTheme.auraGlow
                val activeSecondary = if (currentStep == DecadeStep.HAIL_MARY) specularColor else prayerTheme.secondaryAccent

                // 1. Pulsing ambient sacred glow around the bead if active
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
                                activeThemeGlow.copy(alpha = 0.85f),
                                activeSecondary.copy(alpha = 0.45f),
                                Color.Transparent
                            )
                        },
                        center = Offset(cx, cy),
                        radius = radius * (if (currentIsReadyToRelease) 2.5f else 2.2f)
                    )
                    drawCircle(brush = glowBrush, radius = radius * 2.3f, center = Offset(cx, cy))
                }

                // 2. Draw prayer-specific 3D sacred bead
                drawPrayerBead(
                    step = currentStep,
                    cx = cx,
                    cy = cy,
                    radius = radius,
                    isCompleted = currentIsPrayingActive,
                    pulse = 1f,
                    primaryColor = primaryColor,
                    specularColor = specularColor,
                    darkColor = darkColor
                )

                // 3. Ornate bottom filigree cap in theme accent
                val filigreePath = Path().apply {
                    moveTo(cx - 9.dp.toPx(), cy + radius * 0.82f)
                    quadraticTo(cx, cy + radius * 1.30f, cx + 9.dp.toPx(), cy + radius * 0.82f)
                }
                drawPath(
                    path = filigreePath,
                    color = if (currentIsReadyToRelease) MedievalGoldBright else activeSecondary,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.2.dp.toPx())
                )

                // 4. Circular Progress Bar showing time left to release the bead
                if ((currentIsPrayingActive || beadYOffsetPx > 15f) && !currentIsSnappingUp) {
                    val ringRadiusPx = 34.dp.toPx()

                    // Subtle background track
                    drawCircle(
                        color = if (isDark) Color(0xFF233B57).copy(alpha = 0.65f) else MedievalGoldDark.copy(alpha = 0.35f),
                        radius = ringRadiusPx,
                        center = Offset(cx, cy),
                        style = Stroke(width = 3.dp.toPx())
                    )

                    // Active sweeping progress arc
                    val sweepAngle = (360f * prayerProgress).coerceIn(0f, 360f)
                    if (sweepAngle > 0f) {
                        // Ambient luminous halo
                        drawArc(
                            color = if (currentIsReadyToRelease) MedievalGoldBright.copy(alpha = 0.65f) else GoldGlow.copy(alpha = 0.45f),
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = Offset(cx - ringRadiusPx, cy - ringRadiusPx),
                            size = Size(ringRadiusPx * 2f, ringRadiusPx * 2f),
                            style = Stroke(width = 6.5.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Golden progress arc
                        drawArc(
                            brush = Brush.sweepGradient(
                                colors = listOf(
                                    MedievalGold,
                                    MedievalGoldBright,
                                    if (currentIsReadyToRelease) Color.White else MedievalGoldBright
                                ),
                                center = Offset(cx, cy)
                            ),
                            startAngle = -90f,
                            sweepAngle = sweepAngle,
                            useCenter = false,
                            topLeft = Offset(cx - ringRadiusPx, cy - ringRadiusPx),
                            size = Size(ringRadiusPx * 2f, ringRadiusPx * 2f),
                            style = Stroke(width = 3.6.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Leading-edge jewel indicator dot
                        val currentAngleRad = Math.toRadians((-90f + sweepAngle).toDouble())
                        val tipX = cx + (ringRadiusPx * cos(currentAngleRad)).toFloat()
                        val tipY = cy + (ringRadiusPx * sin(currentAngleRad)).toFloat()

                        drawCircle(
                            color = Color.White,
                            radius = 2.8.dp.toPx(),
                            center = Offset(tipX, tipY)
                        )
                        drawCircle(
                            color = MedievalGoldBright,
                            radius = 1.8.dp.toPx(),
                            center = Offset(tipX, tipY)
                        )
                    }
                }
            }
        }

        // 3. Status Guidance Indicator
        val arrowFloatPx = with(density) { arrowFloatY.dp.toPx() }
        val indicatorY = (restingBeadCenterYPx + indicatorBaseOffsetPx + beadYOffsetPx + arrowFloatPx).roundToInt()

        if (!currentIsPrayingActive && beadYOffsetPx < 12f) {
            // Resting state: DOWN arrow & "Drag Down to Pray"
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
                    text = "Drag Down to Pray",
                    color = if (isDark) Color(0xFFF7F3EA) else Color(0xFF1E2836),
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.4.sp
                )
            }
        } else if (currentIsPrayingActive && !currentIsReadyToRelease) {
            // Actively holding & praying: Live countdown and instructions
            val remainingMs = (prayerDurationMs * (1f - prayerProgress)).coerceAtLeast(0f)
            val remainingSec = remainingMs / 1000f
            val remainingText = String.format(Locale.US, "%.1fs", remainingSec)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.offset {
                    IntOffset(x = 0, y = indicatorY)
                }
            ) {
                Text(
                    text = "Release in $remainingText",
                    color = MedievalGoldBright,
                    fontSize = 12.5.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Keep holding bead",
                    color = if (isDark) Color(0xFFD4C8B4) else Color(0xFF1E2836).copy(alpha = 0.75f),
                    fontSize = 10.5.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Normal
                )
            }
        } else if (currentIsPrayingActive && currentIsReadyToRelease) {
            // Completed hold: UP arrow & "RELEASE"
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
                    fontSize = 12.sp,
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
