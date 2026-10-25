package com.trivium.rosarium.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import com.trivium.rosarium.ui.theme.GoldGlow
import com.trivium.rosarium.ui.theme.MedievalGold
import com.trivium.rosarium.ui.theme.MedievalGoldBright
import com.trivium.rosarium.ui.theme.MedievalGoldDark
import com.trivium.rosarium.ui.theme.OxbloodRed
import com.trivium.rosarium.ui.theme.ParchmentBase
import com.trivium.rosarium.ui.theme.PearlShadow
import com.trivium.rosarium.ui.theme.PearlWhite
import com.trivium.rosarium.ui.theme.UltramarineDark
import com.trivium.rosarium.ui.theme.UltramarineLapis
import com.trivium.rosarium.ui.theme.UltramarineSpecular
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Continuous Decade Rosary Chain Arc:
 * - Empty filigree socket in the center receives each active bead from below.
 * - When Our Father is completed, the Mother-of-pearl White Bead settles in the center.
 * - When the next Hail Mary bead is released, the White Bead moves to the left!
 * - Subsequent Hail Mary beads slide left along the chain, leaving the center slot
 *   open/empty awaiting the next bead.
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

    // Breathing pulse for active bead socket
    val infiniteTransition = rememberInfiniteTransition(label = "ReceivingPulse")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseGlow"
    )

    // Smooth shift animation along the chain when a new bead completes
    val shiftAnim = remember { Animatable(1f) }
    LaunchedEffect(completedHailMarys) {
        if (completedHailMarys > 0) {
            shiftAnim.snapTo(0f)
            shiftAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(420, easing = FastOutSlowInEasing)
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(98.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val cx = w / 2f

            val blueBeadRadius = 12.5.dp.toPx()
            val whiteBeadRadius = 15.5.dp.toPx()

            val ourFatherY = 26.dp.toPx()
            val centerSlotPos = Offset(cx, ourFatherY)
            val crucifixY = ourFatherY + 28.dp.toPx()

            // Arc geometry calculation for left and right slots
            val spanLeft = cx - 18.dp.toPx()
            val spanRight = (w - 18.dp.toPx()) - cx

            fun getLeftSlot(slotIdx: Int): Offset {
                val frac = ((slotIdx + 1f) / 10.5f).coerceIn(0.08f, 1.0f)
                val x = cx - (spanLeft * frac)
                val y = ourFatherY - (22.dp.toPx() * (frac * frac))
                return Offset(x, y)
            }

            fun getRightSlot(slotIdx: Int): Offset {
                val frac = ((slotIdx + 1f) / 10.5f).coerceIn(0.08f, 1.0f)
                val x = cx + (spanRight * frac)
                val y = ourFatherY - (22.dp.toPx() * (frac * frac))
                return Offset(x, y)
            }


            // 1. Draw Golden Chain Segments along the arc
            // Left chain
            for (i in 9 downTo 1) {
                drawGoldChainSegment(getLeftSlot(i), getLeftSlot(i - 1))
            }
            drawGoldChainSegment(getLeftSlot(0), centerSlotPos)

            // Right chain
            drawGoldChainSegment(centerSlotPos, getRightSlot(0))
            for (i in 0 until 9) {
                drawGoldChainSegment(getRightSlot(i), getRightSlot(i + 1))
            }

            // Chain to hanging Crucifix
            drawGoldChainSegment(
                Offset(cx, ourFatherY + 12.dp.toPx()),
                Offset(cx, crucifixY - 14.dp.toPx())
            )

            // 2. Draw Hanging Crucifix Pendant
            drawCrucifixPendant(cx, crucifixY)

            // 3. Draw Right Side Empty Wire Sockets
            for (i in 0..8) {
                val rPos = getRightSlot(i)
                drawEmptyPlaceholderSocket(
                    cx = rPos.x,
                    cy = rPos.y,
                    radius = blueBeadRadius * 0.95f,
                    isCenterReceiving = false,
                    isPearl = false,
                    pulse = 1f
                )
            }

            // 4. Draw Center Slot
            // The center slot is the dedicated receiving socket for incoming beads!
            // When Our Father is completed AND completedHailMarys == 0, Our Father rests in center.
            // As soon as completedHailMarys > 0, the center slot is left EMPTY awaiting the next bead!
            val isOurFatherInCenter = isOurFatherCompleted && completedHailMarys == 0
            if (isOurFatherInCenter) {
                drawMotherOfPearlBead(
                    cx = centerSlotPos.x,
                    cy = centerSlotPos.y,
                    radius = whiteBeadRadius,
                    isCompleted = true,
                    pulse = 1f
                )
            } else {
                val isCenterTarget = !isOurFatherCompleted || completedHailMarys < 10
                drawEmptyPlaceholderSocket(
                    cx = centerSlotPos.x,
                    cy = centerSlotPos.y,
                    radius = if (!isOurFatherCompleted) whiteBeadRadius else blueBeadRadius,
                    isCenterReceiving = isCenterTarget,
                    isPearl = !isOurFatherCompleted,
                    pulse = if (isCenterTarget) pulseGlow else 1f,
                    isHoldPraying = isHoldPraying
                )
            }

            // 5. Draw Left Slots and Shifting Completed Beads
            // Every time a bead is released, all completed beads advance left by one socket!
            if (isOurFatherCompleted && completedHailMarys > 0) {
                val progress = shiftAnim.value

                // A. The White Bead (Our Father)
                // Moves left on each Hail Mary release:
                // From center (if completedHailMarys == 1) or from (completedHailMarys - 1) to completedHailMarys!
                val whiteFromPos = if (completedHailMarys == 1) centerSlotPos else getLeftSlot(completedHailMarys - 1)
                val whiteToPos = getLeftSlot(completedHailMarys)
                val whitePos = lerp(whiteFromPos, whiteToPos, progress)

                drawMotherOfPearlBead(
                    cx = whitePos.x,
                    cy = whitePos.y,
                    radius = whiteBeadRadius,
                    isCompleted = true,
                    pulse = 1f
                )

                // B. Previous completed Hail Mary beads (all moving left by 1 position)
                for (h in 1 until completedHailMarys) {
                    val fromIdx = (completedHailMarys - 1) - h
                    val toIdx = completedHailMarys - h
                    val bPos = lerp(getLeftSlot(fromIdx), getLeftSlot(toIdx), progress)
                    drawLapisLazuliBead(
                        cx = bPos.x,
                        cy = bPos.y,
                        radius = blueBeadRadius,
                        isCompleted = true,
                        pulse = 1f
                    )
                }

                // C. The newly completed Hail Mary bead
                // Rolls from Center Socket into Left Slot 0, leaving center empty!
                val newBeadPos = lerp(centerSlotPos, getLeftSlot(0), progress)
                drawLapisLazuliBead(
                    cx = newBeadPos.x,
                    cy = newBeadPos.y,
                    radius = blueBeadRadius,
                    isCompleted = true,
                    pulse = 1f
                )

                // D. Draw empty placeholders for remaining left slots
                for (i in (completedHailMarys + 1)..9) {
                    val slotPos = getLeftSlot(i)
                    drawEmptyPlaceholderSocket(
                        cx = slotPos.x,
                        cy = slotPos.y,
                        radius = blueBeadRadius * 0.95f,
                        isCenterReceiving = false,
                        isPearl = false,
                        pulse = 1f
                    )
                }
            } else {
                // If no Hail Marys have completed yet, all left slots are empty wire sockets
                for (i in 0..9) {
                    val slotPos = getLeftSlot(i)
                    drawEmptyPlaceholderSocket(
                        cx = slotPos.x,
                        cy = slotPos.y,
                        radius = blueBeadRadius * 0.95f,
                        isCenterReceiving = false,
                        isPearl = false,
                        pulse = 1f
                    )
                }
            }
        }

        // Sparkle burst effect on newly settled bead
        if (justCompletedIndex != null && justCompletedIndex in -1 until 10) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                SparkleBurst()
            }
        }
    }
}

private fun lerp(start: Offset, stop: Offset, fraction: Float): Offset {
    return Offset(
        start.x + (stop.x - start.x) * fraction,
        start.y + (stop.y - start.y) * fraction
    )
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
 * Draws the Radiant Amber-Gold Trinity bead for the Glory Be.
 */
fun DrawScope.drawAmberGoldBead(
    cx: Float,
    cy: Float,
    radius: Float,
    isCompleted: Boolean,
    pulse: Float = 1f
) {
    drawCircle(
        color = Color.Black.copy(alpha = 0.55f),
        radius = radius * 1.05f,
        center = Offset(cx + 2.dp.toPx(), cy + 3.dp.toPx())
    )

    if (isCompleted) {
        val glowRadius = radius * 1.9f * pulse
        val haloBrush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFFD54F).copy(alpha = 0.85f),
                Color(0xFFFF8F00).copy(alpha = 0.45f),
                Color.Transparent
            ),
            center = Offset(cx, cy),
            radius = glowRadius
        )
        drawCircle(brush = haloBrush, radius = glowRadius, center = Offset(cx, cy))
    }

    val amberBrush = Brush.radialGradient(
        colors = listOf(
            Color(0xFFFFF9C4),
            Color(0xFFFFB300),
            Color(0xFFE65100)
        ),
        center = Offset(cx - radius * 0.35f, cy - radius * 0.35f),
        radius = radius * 1.25f
    )
    drawCircle(brush = amberBrush, radius = radius, center = Offset(cx, cy))

    drawCircle(
        color = Color.White.copy(alpha = 0.9f),
        radius = radius * 0.30f,
        center = Offset(cx - radius * 0.38f, cy - radius * 0.38f)
    )

    drawCircle(color = MedievalGoldBright, radius = radius * 0.22f, center = Offset(cx, cy - radius * 0.88f))
    drawCircle(color = MedievalGoldBright, radius = radius * 0.22f, center = Offset(cx, cy + radius * 0.88f))
}

/**
 * Draws the Sacred Heart Ruby-Crimson bead for the Fatima Prayer.
 */
fun DrawScope.drawRubyCrimsonBead(
    cx: Float,
    cy: Float,
    radius: Float,
    isCompleted: Boolean,
    pulse: Float = 1f
) {
    drawCircle(
        color = Color.Black.copy(alpha = 0.55f),
        radius = radius * 1.05f,
        center = Offset(cx + 2.dp.toPx(), cy + 3.dp.toPx())
    )

    if (isCompleted) {
        val glowRadius = radius * 1.9f * pulse
        val haloBrush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFFF8A80).copy(alpha = 0.85f),
                OxbloodRed.copy(alpha = 0.45f),
                Color.Transparent
            ),
            center = Offset(cx, cy),
            radius = glowRadius
        )
        drawCircle(brush = haloBrush, radius = glowRadius, center = Offset(cx, cy))
    }

    val rubyBrush = Brush.radialGradient(
        colors = listOf(
            Color(0xFFFFCDD2),
            Color(0xFFC62828),
            Color(0xFF4A0000)
        ),
        center = Offset(cx - radius * 0.35f, cy - radius * 0.35f),
        radius = radius * 1.25f
    )
    drawCircle(brush = rubyBrush, radius = radius, center = Offset(cx, cy))

    drawCircle(
        color = Color.White.copy(alpha = 0.85f),
        radius = radius * 0.28f,
        center = Offset(cx - radius * 0.38f, cy - radius * 0.38f)
    )

    drawCircle(color = MedievalGoldBright, radius = radius * 0.22f, center = Offset(cx, cy - radius * 0.88f))
    drawCircle(color = MedievalGoldBright, radius = radius * 0.22f, center = Offset(cx, cy + radius * 0.88f))
}

/**
 * Draws the Queen of Heaven Imperial Amethyst bead for the Hail Holy Queen.
 */
fun DrawScope.drawAmethystBead(
    cx: Float,
    cy: Float,
    radius: Float,
    isCompleted: Boolean,
    pulse: Float = 1f
) {
    drawCircle(
        color = Color.Black.copy(alpha = 0.55f),
        radius = radius * 1.05f,
        center = Offset(cx + 2.dp.toPx(), cy + 3.dp.toPx())
    )

    if (isCompleted) {
        val glowRadius = radius * 1.9f * pulse
        val haloBrush = Brush.radialGradient(
            colors = listOf(
                Color(0xFFE1BEE7).copy(alpha = 0.85f),
                Color(0xFF7B1FA2).copy(alpha = 0.45f),
                Color.Transparent
            ),
            center = Offset(cx, cy),
            radius = glowRadius
        )
        drawCircle(brush = haloBrush, radius = glowRadius, center = Offset(cx, cy))
    }

    val amethystBrush = Brush.radialGradient(
        colors = listOf(
            Color(0xFFF3E5F5),
            Color(0xFF6A1B9A),
            Color(0xFF1A0033)
        ),
        center = Offset(cx - radius * 0.35f, cy - radius * 0.35f),
        radius = radius * 1.25f
    )
    drawCircle(brush = amethystBrush, radius = radius, center = Offset(cx, cy))

    drawCircle(
        color = Color.White.copy(alpha = 0.85f),
        radius = radius * 0.28f,
        center = Offset(cx - radius * 0.38f, cy - radius * 0.38f)
    )

    drawCircle(color = MedievalGoldBright, radius = radius * 0.22f, center = Offset(cx, cy - radius * 0.88f))
    drawCircle(color = MedievalGoldBright, radius = radius * 0.22f, center = Offset(cx, cy + radius * 0.88f))
}

/**
 * Draws the appropriate bead according to the active liturgical prayer step.
 */
fun DrawScope.drawPrayerBead(
    step: com.trivium.rosarium.data.model.DecadeStep,
    cx: Float,
    cy: Float,
    radius: Float,
    isCompleted: Boolean,
    pulse: Float = 1f
) {
    when (step) {
        com.trivium.rosarium.data.model.DecadeStep.OUR_FATHER -> drawMotherOfPearlBead(cx, cy, radius * 1.15f, isCompleted, pulse)
        com.trivium.rosarium.data.model.DecadeStep.HAIL_MARY -> drawLapisLazuliBead(cx, cy, radius, isCompleted, pulse)
        com.trivium.rosarium.data.model.DecadeStep.GLORY_BE -> drawAmberGoldBead(cx, cy, radius, isCompleted, pulse)
        com.trivium.rosarium.data.model.DecadeStep.FATIMA -> drawRubyCrimsonBead(cx, cy, radius, isCompleted, pulse)
        com.trivium.rosarium.data.model.DecadeStep.DECADE_FINISHED -> drawAmethystBead(cx, cy, radius * 1.1f, isCompleted, pulse)
    }
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
