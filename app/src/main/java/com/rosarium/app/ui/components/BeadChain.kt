package com.rosarium.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.rosarium.app.ui.theme.GoldGlow
import com.rosarium.app.ui.theme.MedievalGold
import com.rosarium.app.ui.theme.MedievalGoldBright
import com.rosarium.app.ui.theme.MedievalGoldDark
import com.rosarium.app.ui.theme.OxbloodRed
import com.rosarium.app.ui.theme.ParchmentBase
import com.rosarium.app.ui.theme.PearlShadow
import com.rosarium.app.ui.theme.PearlWhite
import com.rosarium.app.ui.theme.UltramarineDark
import com.rosarium.app.ui.theme.UltramarineLapis
import com.rosarium.app.ui.theme.UltramarineSpecular
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Continuous 10-bead Decade Rosary:
 * - Empty filigree wire placeholders/sockets initially awaiting beads to arrive.
 * - The center slot at the bottom of the loop is the dedicated Receiving Slot,
 *   illuminated and ready to receive the active bead.
 * - When a bead arrives, it settles in the center slot with a clack & sparkle burst.
 * - The rosary then smoothly rotates bead-by-bead along the loop, shifting completed
 *   beads along the chain and bringing the next empty placeholder into the center!
 */
@Composable
fun BeadChain(
    completedHailMarys: Int,
    isOurFatherCompleted: Boolean,
    justCompletedIndex: Int?,
    isHoldPraying: Boolean = false,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    // Rotation offset animation: advances from 0 -> 1 -> 2 ... -> 10
    val rotationAnimatable = remember { Animatable(completedHailMarys.toFloat()) }

    LaunchedEffect(completedHailMarys) {
        if (completedHailMarys == 0) {
            rotationAnimatable.snapTo(0f)
        } else {
            // Give 300ms for user to see the bead settle & sparkle in the center slot
            delay(300L)
            // Smoothly rotate the rosary loop by 1 bead position
            rotationAnimatable.animateTo(
                targetValue = completedHailMarys.toFloat(),
                animationSpec = tween(
                    durationMillis = 580,
                    easing = FastOutSlowInEasing
                )
            )
        }
    }

    // Breathing pulse for the center receiving socket
    val infiniteTransition = rememberInfiniteTransition(label = "ReceivingPulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.82f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f

            // Hierarchy vertical anchors
            val ourFatherY = h * 0.29f
            val crucifixY = h * 0.36f
            val loopCenterY = h * 0.44f
            val loopRadiusX = w * 0.36f
            val loopRadiusY = h * 0.095f

            // The Center Receiving Position (lowest point of the decade loop)
            val receivingY = loopCenterY + loopRadiusY

            val blueBeadRadius = 14.5.dp.toPx()
            val whiteBeadRadius = 17.5.dp.toPx()

            // 1. Calculate positions for the 10 slots on the rosary loop
            val currentRotation = rotationAnimatable.value
            val slotPositions = mutableListOf<Offset>()
            val slotAngles = mutableListOf<Double>()

            for (i in 0 until 10) {
                // When (i - currentRotation) is 0, angle is 0 => bottom center receiving position
                val angleRad = (i - currentRotation) * (2.0 * Math.PI / 10.0)
                slotAngles.add(angleRad)
                val sx = cx + (loopRadiusX * sin(angleRad)).toFloat()
                val sy = loopCenterY + (loopRadiusY * cos(angleRad)).toFloat()
                slotPositions.add(Offset(sx, sy))
            }

            // 2. Draw Golden Chain Links connecting each slot in the continuous loop
            for (i in 0 until 10) {
                val next = (i + 1) % 10
                drawGoldChainSegment(slotPositions[i], slotPositions[next])
            }

            // Chain connecting top Our Father bead down to Crucifix
            drawGoldChainSegment(
                Offset(cx, ourFatherY + whiteBeadRadius),
                Offset(cx, crucifixY - 18.dp.toPx())
            )

            // Connector links joining the decade loop to the Crucifix
            drawGoldChainSegment(
                Offset(cx - 10.dp.toPx(), loopCenterY - loopRadiusY + 6.dp.toPx()),
                Offset(cx, crucifixY + 16.dp.toPx())
            )
            drawGoldChainSegment(
                Offset(cx + 10.dp.toPx(), loopCenterY - loopRadiusY + 6.dp.toPx()),
                Offset(cx, crucifixY + 16.dp.toPx())
            )

            // 3. Draw Top Crucifix Centerpiece
            drawCrucifixPendant(cx, crucifixY)

            // 4. Draw Our Father Bead / Socket at the top
            if (isOurFatherCompleted) {
                drawMotherOfPearlBead(
                    cx = cx,
                    cy = ourFatherY,
                    radius = whiteBeadRadius,
                    isCompleted = true,
                    pulse = 1f
                )
            } else {
                // Empty Mother-of-Pearl socket awaiting Our Father
                drawEmptyPlaceholderSocket(
                    cx = cx,
                    cy = ourFatherY,
                    radius = whiteBeadRadius,
                    isCenterReceiving = true,
                    isPearl = true,
                    pulse = pulseGlow,
                    isHoldPraying = isHoldPraying
                )
            }

            // 5. Draw the 10 Slots (Empty Placeholders or Filled Beads)
            for (i in 0 until 10) {
                val pos = slotPositions[i]
                val angle = slotAngles[i]
                val isFilled = i < completedHailMarys

                // Check if this slot is near the center receiving position (bottom center)
                // Normalize angle to [-PI, PI]
                val normalizedAngle = kotlin.math.atan2(sin(angle), cos(angle))
                val isAtCenter = kotlin.math.abs(normalizedAngle) < 0.22

                if (isFilled) {
                    drawFilledBeadInSocket(
                        cx = pos.x,
                        cy = pos.y,
                        radius = blueBeadRadius,
                        isCenterReceiving = isAtCenter,
                        pulse = if (isAtCenter) pulseGlow else 1f
                    )
                } else {
                    drawEmptyPlaceholderSocket(
                        cx = pos.x,
                        cy = pos.y,
                        radius = blueBeadRadius,
                        isCenterReceiving = isAtCenter,
                        isPearl = false,
                        pulse = if (isAtCenter) pulseGlow else 1f,
                        isHoldPraying = isHoldPraying && isAtCenter
                    )
                }
            }
        }

        // 6. Golden Sparkle Burst over the Center Receiving Slot on arrival
        if (justCompletedIndex != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                Box(
                    modifier = Modifier.offset {
                        // Position exactly over the center receiving slot (h * 0.535f)
                        val h = density.run { 800.dp.toPx() } // dynamic estimate
                        IntOffset(0, (density.run { 428.dp.toPx() }).roundToInt() - 27.dp.roundToPx())
                    }
                ) {
                    SparkleBurst()
                }
            }
        }
    }
}

/**
 * Draws an authentic empty medieval placeholder socket:
 * Antique gold wire ring, hollow dark recess showing background,
 * and 4 filigree claws ready to clasp the incoming bead.
 * If isCenterReceiving is true, radiates an inviting golden beacon glow.
 */
fun DrawScope.drawEmptyPlaceholderSocket(
    cx: Float,
    cy: Float,
    radius: Float,
    isCenterReceiving: Boolean,
    isPearl: Boolean = false,
    pulse: Float = 1f,
    isHoldPraying: Boolean = false
) {
    // 1. Soft depth shadow
    drawCircle(
        color = Color.Black.copy(alpha = 0.5f),
        radius = radius * 1.05f,
        center = Offset(cx + 1.5.dp.toPx(), cy + 2.5.dp.toPx())
    )

    // 2. Receiving Slot Target Glow (pulsing gold beacon)
    if (isCenterReceiving) {
        val auraRadius = radius * (if (isHoldPraying) 2.2f else 1.75f) * pulse
        val auraBrush = Brush.radialGradient(
            colors = listOf(
                GoldGlow.copy(alpha = if (isHoldPraying) 0.85f else 0.55f),
                MedievalGoldBright.copy(alpha = if (isHoldPraying) 0.5f else 0.25f),
                Color.Transparent
            ),
            center = Offset(cx, cy),
            radius = auraRadius
        )
        drawCircle(brush = auraBrush, radius = auraRadius, center = Offset(cx, cy))

        // Delicate target shimmer ring
        drawCircle(
            color = MedievalGoldBright.copy(alpha = ((pulse - 0.82f) / 0.36f).coerceIn(0.2f, 0.9f)),
            radius = radius + 3.dp.toPx(),
            center = Offset(cx, cy),
            style = Stroke(width = 1.2.dp.toPx())
        )
    }

    // 3. Hollow dark recess showing background through socket
    drawCircle(
        color = Color(0xFF0F0B08).copy(alpha = 0.75f),
        radius = radius - 1.5.dp.toPx(),
        center = Offset(cx, cy)
    )

    // 4. Antique gold wire bezel ring
    val bezelBrush = Brush.linearGradient(
        colors = if (isPearl) {
            listOf(PearlWhite, MedievalGoldBright, MedievalGoldDark)
        } else {
            listOf(MedievalGoldBright, MedievalGold, MedievalGoldDark)
        },
        start = Offset(cx - radius, cy - radius),
        end = Offset(cx + radius, cy + radius)
    )

    drawCircle(
        brush = bezelBrush,
        radius = radius,
        center = Offset(cx, cy),
        style = Stroke(width = 2.dp.toPx())
    )

    // Inner wire rim
    drawCircle(
        color = MedievalGoldDark.copy(alpha = 0.6f),
        radius = radius - 2.2.dp.toPx(),
        center = Offset(cx, cy),
        style = Stroke(width = 0.8.dp.toPx())
    )

    // 5. Four ornate filigree claws/prongs awaiting jewel
    val clawDist = radius - 1.dp.toPx()
    val clawRadius = 1.3.dp.toPx()
    val angles = listOf(0.0, Math.PI / 2, Math.PI, 3 * Math.PI / 2)
    for (a in angles) {
        val px = cx + (clawDist * cos(a)).toFloat()
        val py = cy + (clawDist * sin(a)).toFloat()
        drawCircle(
            color = MedievalGoldBright,
            radius = clawRadius,
            center = Offset(px, py)
        )
    }
}

/**
 * Draws a filled Lapis Lazuli bead settled firmly inside its golden socket,
 * clasped by filigree prongs and crowned with a golden halo.
 */
fun DrawScope.drawFilledBeadInSocket(
    cx: Float,
    cy: Float,
    radius: Float,
    isCenterReceiving: Boolean,
    pulse: Float = 1f
) {
    // 1. Draw the 3D Lapis Lazuli bead
    drawLapisLazuliBead(
        cx = cx,
        cy = cy,
        radius = radius,
        isCompleted = true,
        pulse = pulse
    )

    // 2. Bezel socket ring encircling the bead
    val bezelBrush = Brush.linearGradient(
        colors = listOf(MedievalGoldBright, MedievalGold, MedievalGoldDark),
        start = Offset(cx - radius, cy - radius),
        end = Offset(cx + radius, cy + radius)
    )
    drawCircle(
        brush = bezelBrush,
        radius = radius + 0.5.dp.toPx(),
        center = Offset(cx, cy),
        style = Stroke(width = 1.4.dp.toPx())
    )

    // 3. Four filigree prongs clasping the bead
    val clawDist = radius + 0.2.dp.toPx()
    val clawRadius = 1.4.dp.toPx()
    val angles = listOf(0.0, Math.PI / 2, Math.PI, 3 * Math.PI / 2)
    for (a in angles) {
        val px = cx + (clawDist * cos(a)).toFloat()
        val py = cy + (clawDist * sin(a)).toFloat()
        drawCircle(
            color = MedievalGoldBright,
            radius = clawRadius,
            center = Offset(px, py)
        )
    }
}

/**
 * Draws a segment of antique gold rosary chain links between two points.
 */
private fun DrawScope.drawGoldChainSegment(from: Offset, to: Offset) {
    val dx = to.x - from.x
    val dy = to.y - from.y
    val dist = kotlin.math.sqrt(dx * dx + dy * dy)
    val linkSpacing = 8.dp.toPx()
    val steps = (dist / linkSpacing).toInt().coerceAtLeast(1)

    for (step in 1 until steps) {
        val t = step.toFloat() / steps.toFloat()
        val lx = from.x + (dx * t)
        val ly = from.y + (dy * t)

        // Chain link shadow
        drawCircle(
            color = Color.Black.copy(alpha = 0.4f),
            radius = 2.4.dp.toPx(),
            center = Offset(lx + 0.5.dp.toPx(), ly + 0.5.dp.toPx())
        )

        // Golden oval chain ring
        drawCircle(
            color = MedievalGoldDark,
            radius = 2.4.dp.toPx(),
            center = Offset(lx, ly),
            style = Stroke(width = 1.2.dp.toPx())
        )
        drawCircle(
            color = MedievalGoldBright,
            radius = 1.4.dp.toPx(),
            center = Offset(lx, ly)
        )
    }
}

/**
 * Draws the glossy Lapis Lazuli Hail Mary bead with gold specular flecks and halo.
 */
fun DrawScope.drawLapisLazuliBead(
    cx: Float,
    cy: Float,
    radius: Float,
    isCompleted: Boolean,
    pulse: Float = 1f
) {
    // Drop shadow
    drawCircle(
        color = Color.Black.copy(alpha = 0.55f),
        radius = radius * 1.05f,
        center = Offset(cx + 2.dp.toPx(), cy + 3.dp.toPx())
    )

    // Completed Golden Halo Glow
    if (isCompleted) {
        val glowRadius = radius * 1.8f * pulse
        val haloBrush = Brush.radialGradient(
            colors = listOf(
                GoldGlow.copy(alpha = 0.85f),
                MedievalGoldBright.copy(alpha = 0.45f),
                Color.Transparent
            ),
            center = Offset(cx, cy),
            radius = glowRadius
        )
        drawCircle(brush = haloBrush, radius = glowRadius, center = Offset(cx, cy))

        // Illuminated golden perimeter ring
        drawCircle(
            color = MedievalGoldBright,
            radius = radius + 2.dp.toPx(),
            center = Offset(cx, cy),
            style = Stroke(width = 2.dp.toPx())
        )
    }

    // 3D Lapis Lazuli sphere with specular highlight
    val lapisBrush = Brush.radialGradient(
        colors = listOf(
            if (isCompleted) UltramarineSpecular else UltramarineLapis.copy(alpha = 0.95f),
            UltramarineLapis,
            UltramarineDark
        ),
        center = Offset(cx - radius * 0.35f, cy - radius * 0.35f),
        radius = radius * 1.2f
    )
    drawCircle(brush = lapisBrush, radius = radius, center = Offset(cx, cy))

    // Tiny specular gloss reflection at top-left
    drawCircle(
        color = Color.White.copy(alpha = if (isCompleted) 0.9f else 0.65f),
        radius = radius * 0.28f,
        center = Offset(cx - radius * 0.38f, cy - radius * 0.38f)
    )

    // Gold end ring caps (drilled hole mounts)
    drawCircle(
        color = MedievalGoldBright,
        radius = radius * 0.22f,
        center = Offset(cx, cy - radius * 0.88f)
    )
    drawCircle(
        color = MedievalGoldBright,
        radius = radius * 0.22f,
        center = Offset(cx, cy + radius * 0.88f)
    )
}

/**
 * Draws the Mother-of-Pearl Our Father bead with gold filigree caps.
 */
fun DrawScope.drawMotherOfPearlBead(
    cx: Float,
    cy: Float,
    radius: Float,
    isCompleted: Boolean,
    pulse: Float = 1f
) {
    // Drop shadow
    drawCircle(
        color = Color.Black.copy(alpha = 0.55f),
        radius = radius * 1.05f,
        center = Offset(cx + 2.dp.toPx(), cy + 3.dp.toPx())
    )

    // Halo if completed
    if (isCompleted) {
        val glowRadius = radius * 1.9f * pulse
        val haloBrush = Brush.radialGradient(
            colors = listOf(
                GoldGlow.copy(alpha = 0.85f),
                MedievalGoldBright.copy(alpha = 0.4f),
                Color.Transparent
            ),
            center = Offset(cx, cy),
            radius = glowRadius
        )
        drawCircle(brush = haloBrush, radius = glowRadius, center = Offset(cx, cy))
    }

    // Pearl sphere
    val pearlBrush = Brush.radialGradient(
        colors = listOf(
            Color.White,
            PearlWhite,
            PearlShadow,
            MedievalGoldDark.copy(alpha = 0.5f)
        ),
        center = Offset(cx - radius * 0.35f, cy - radius * 0.35f),
        radius = radius * 1.25f
    )
    drawCircle(brush = pearlBrush, radius = radius, center = Offset(cx, cy))

    // Specular highlight
    drawCircle(
        color = Color.White,
        radius = radius * 0.32f,
        center = Offset(cx - radius * 0.35f, cy - radius * 0.35f)
    )

    // Gold filigree caps on top and bottom
    val capBrush = Brush.linearGradient(
        colors = listOf(MedievalGoldBright, MedievalGold, MedievalGoldDark),
        start = Offset(cx - radius, cy),
        end = Offset(cx + radius, cy)
    )

    drawRoundRect(
        brush = capBrush,
        topLeft = Offset(cx - radius * 0.45f, cy - radius - 2.dp.toPx()),
        size = Size(radius * 0.9f, 4.dp.toPx()),
        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
    )
    drawRoundRect(
        brush = capBrush,
        topLeft = Offset(cx - radius * 0.45f, cy + radius - 2.dp.toPx()),
        size = Size(radius * 0.9f, 4.dp.toPx()),
        cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
    )
}

/**
 * Draws the ornate Gothic trefoil Latin crucifix pendant centerpiece.
 */
fun DrawScope.drawCrucifixPendant(cx: Float, cy: Float) {
    val crossW = 22.dp.toPx()
    val crossH = 34.dp.toPx()
    val barH = 5.dp.toPx()
    val barW = 5.dp.toPx()

    // Shadow
    drawRoundRect(
        color = Color.Black.copy(alpha = 0.45f),
        topLeft = Offset(cx - crossW / 2f + 1.dp.toPx(), cy - crossH * 0.4f + 2.dp.toPx()),
        size = Size(crossW, barH),
        cornerRadius = CornerRadius(1.dp.toPx(), 1.dp.toPx())
    )

    val goldBrush = Brush.linearGradient(
        colors = listOf(MedievalGoldBright, MedievalGold, MedievalGoldDark),
        start = Offset(cx - crossW / 2f, cy - crossH / 2f),
        end = Offset(cx + crossW / 2f, cy + crossH / 2f)
    )

    // Vertical shaft
    drawRoundRect(
        brush = goldBrush,
        topLeft = Offset(cx - barW / 2f, cy - crossH * 0.45f),
        size = Size(barW, crossH),
        cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx())
    )

    // Horizontal beam
    drawRoundRect(
        brush = goldBrush,
        topLeft = Offset(cx - crossW / 2f, cy - crossH * 0.2f),
        size = Size(crossW, barH),
        cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx())
    )

    // Central ruby / sacred heart accent
    drawCircle(
        color = OxbloodRed,
        radius = 2.dp.toPx(),
        center = Offset(cx, cy - crossH * 0.2f + barH / 2f)
    )
}
