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
    isSnappingUp: Boolean = false,
    totalBeadsInDecade: Int = 10,
    primaryColor: Color = UltramarineLapis,
    specularColor: Color = UltramarineSpecular,
    darkColor: Color = UltramarineDark,
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

    // Smooth shift animation along the chain when a bead is pulled down or released:
    // The previous center bead glides smoothly left, clearing the center socket to show
    // the radiant empty slot that is about to get filled!
    val shiftAnim = remember { Animatable(0f) }
    LaunchedEffect(isHoldPraying, isSnappingUp) {
        if ((isHoldPraying || isSnappingUp) && isOurFatherCompleted) {
            shiftAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(320, easing = FastOutSlowInEasing)
            )
        } else if (!isHoldPraying && !isSnappingUp) {
            shiftAnim.snapTo(0f)
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

            val decadeBeadRadius = 9.2.dp.toPx()
            val whiteBeadRadius = 11.5.dp.toPx()

            val ourFatherY = 22.dp.toPx()
            val centerSlotPos = Offset(cx, ourFatherY)
            val crucifixY = ourFatherY + 22.dp.toPx()

            val spacingPx = with(density) { 27.dp.toPx() }
            val arcHeightPx = with(density) { 14.dp.toPx() }
            val spanWidthPx = cx - with(density) { 16.dp.toPx() }

            fun getLeftSlot(slotIdx: Int): Offset {
                val x = cx - (spacingPx * (slotIdx + 1))
                val normFrac = ((cx - x) / spanWidthPx).coerceAtLeast(0f)
                val y = ourFatherY - (arcHeightPx * kotlin.math.sqrt(normFrac))
                return Offset(x, y)
            }

            fun getRightSlot(slotIdx: Int): Offset {
                val x = cx + (spacingPx * (slotIdx + 1))
                val normFrac = ((x - cx) / spanWidthPx).coerceAtLeast(0f)
                val y = ourFatherY - (arcHeightPx * kotlin.math.sqrt(normFrac))
                return Offset(x, y)
            }

            val isShiftActive = (isHoldPraying || isSnappingUp) && isOurFatherCompleted
            val progress = if (isShiftActive) shiftAnim.value else 0f

            // Total upcoming beads at rest:
            // When Our Father is active: all 10 Hail Mary beads are upcoming on the right.
            // When Hail Marys are active: (totalBeadsInDecade - completedHailMarys) beads remain.
            val baseUpcoming = if (!isOurFatherCompleted) {
                totalBeadsInDecade
            } else {
                (totalBeadsInDecade - completedHailMarys).coerceIn(0, totalBeadsInDecade)
            }

            // When the empty slot is showing in the center, remove an empty slot from the right
            // side so the incoming bead has a natural flow and the total count is always exact.
            val countToRemove = if (isShiftActive && isOurFatherCompleted && baseUpcoming > 0) 1 else 0
            val remainingUpcoming = (baseUpcoming - countToRemove).coerceAtLeast(0)

            // 1. Draw Golden Chain Segments along the arc
            // Right chain connecting upcoming sockets
            if (remainingUpcoming > 0) {
                drawGoldChainSegment(centerSlotPos, getRightSlot(0))
                for (i in 0 until (remainingUpcoming - 1)) {
                    val p1 = getRightSlot(i)
                    val p2 = getRightSlot(i + 1)
                    if (p1.x < w + 30.dp.toPx() && p2.x < w + 30.dp.toPx()) {
                        drawGoldChainSegment(p1, p2)
                    }
                }
            }

            // Left chain connecting completed beads
            val totalLeftSlots = if (isOurFatherCompleted) {
                if (isShiftActive) completedHailMarys + 1 else completedHailMarys
            } else 0

            if (totalLeftSlots > 0) {
                drawGoldChainSegment(getLeftSlot(0), centerSlotPos)
                for (i in 0 until totalLeftSlots) {
                    val p1 = getLeftSlot(i)
                    val p2 = getLeftSlot(i + 1)
                    if (p1.x > -30.dp.toPx() && p2.x > -30.dp.toPx()) {
                        drawGoldChainSegment(p1, p2)
                    }
                }
            }

            // Chain to hanging Crucifix
            drawGoldChainSegment(
                Offset(cx, ourFatherY + 8.dp.toPx()),
                Offset(cx, crucifixY - 10.dp.toPx())
            )

            // 2. Draw Hanging Crucifix Pendant (compact, stays safely above medallion)
            drawCrucifixPendant(cx, crucifixY)

            // 3. Draw Right Side Upcoming Wire Sockets
            for (i in 0 until remainingUpcoming) {
                val rPos = getRightSlot(i)
                val rAlpha = ((w - rPos.x) / with(density) { 24.dp.toPx() }).coerceIn(0f, 1f)
                if (rAlpha > 0.04f) {
                    drawEmptyPlaceholderSocket(
                        cx = rPos.x,
                        cy = rPos.y,
                        radius = decadeBeadRadius * 0.95f,
                        isCenterReceiving = false,
                        isPearl = false,
                        pulse = 1f,
                        alpha = rAlpha
                    )
                }
            }

            // Smoothly dissolve the outermost socket that is transitioning out
            if (countToRemove > 0 && progress < 0.96f) {
                val fadeIdx = baseUpcoming - 1
                val rPos = getRightSlot(fadeIdx)
                val rAlpha = (((w - rPos.x) / with(density) { 24.dp.toPx() }).coerceIn(0f, 1f)) * (1f - progress)
                if (rAlpha > 0.04f) {
                    drawEmptyPlaceholderSocket(
                        cx = rPos.x,
                        cy = rPos.y,
                        radius = decadeBeadRadius * 0.95f,
                        isCenterReceiving = false,
                        isPearl = false,
                        pulse = 1f,
                        alpha = rAlpha
                    )
                }
            }

            // 4. Center Slot & Left Shifted Beads
            if (!isOurFatherCompleted) {
                // Decade not started: Center socket is empty, glowing gold awaiting Our Father
                drawEmptyPlaceholderSocket(
                    cx = centerSlotPos.x,
                    cy = centerSlotPos.y,
                    radius = whiteBeadRadius,
                    isCenterReceiving = true,
                    isPearl = true,
                    pulse = pulseGlow,
                    isHoldPraying = isHoldPraying
                )
            } else if (isShiftActive) {
                // A bead has just been released:
                // The bead that was occupying the center slot immediately glides to the left!
                // Center socket is now empty and open, awaiting the incoming bead from below.
                drawEmptyPlaceholderSocket(
                    cx = centerSlotPos.x,
                    cy = centerSlotPos.y,
                    radius = decadeBeadRadius,
                    isCenterReceiving = true,
                    isPearl = false,
                    pulse = pulseGlow,
                    isHoldPraying = true
                )

                if (completedHailMarys == 0) {
                    // White Bead is sliding left out of the center socket to Left Slot 0
                    val whitePos = lerp(centerSlotPos, getLeftSlot(0), progress)
                    val whiteAlpha = (whitePos.x / with(density) { 26.dp.toPx() }).coerceIn(0f, 1f)
                    if (whiteAlpha > 0.04f) {
                        drawMotherOfPearlBead(
                            cx = whitePos.x,
                            cy = whitePos.y,
                            radius = whiteBeadRadius,
                            isCompleted = true,
                            pulse = 1f,
                            alpha = whiteAlpha
                        )
                    }
                } else {
                    // 1. Most recent Hail Mary bead slides left out of center socket into Left Slot 0
                    val slidingHmPos = lerp(centerSlotPos, getLeftSlot(0), progress)
                    val hmAlpha = (slidingHmPos.x / with(density) { 24.dp.toPx() }).coerceIn(0f, 1f)
                    if (hmAlpha > 0.04f) {
                        drawRosaryBead(
                            cx = slidingHmPos.x,
                            cy = slidingHmPos.y,
                            radius = decadeBeadRadius,
                            primaryColor = primaryColor,
                            specularColor = specularColor,
                            darkColor = darkColor,
                            isCompleted = true,
                            pulse = 1f,
                            alpha = hmAlpha
                        )
                    }

                    // 2. Previous Hail Mary beads on the left each advance 1 slot to the left
                    for (h in 1 until completedHailMarys) {
                        val fromIdx = (completedHailMarys - 1) - h
                        val toIdx = completedHailMarys - h
                        val bPos = lerp(getLeftSlot(fromIdx), getLeftSlot(toIdx), progress)
                        val bAlpha = (bPos.x / with(density) { 24.dp.toPx() }).coerceIn(0f, 1f)
                        if (bAlpha > 0.04f) {
                            drawRosaryBead(
                                cx = bPos.x,
                                cy = bPos.y,
                                radius = decadeBeadRadius,
                                primaryColor = primaryColor,
                                specularColor = specularColor,
                                darkColor = darkColor,
                                isCompleted = true,
                                pulse = 1f,
                                alpha = bAlpha
                            )
                        }
                    }

                    // 3. White Bead (Our Father) slides to its next left slot
                    val whiteFromIdx = completedHailMarys - 1
                    val whiteToIdx = completedHailMarys
                    val whitePos = lerp(getLeftSlot(whiteFromIdx), getLeftSlot(whiteToIdx), progress)
                    val whiteAlpha = (whitePos.x / with(density) { 26.dp.toPx() }).coerceIn(0f, 1f)
                    if (whiteAlpha > 0.04f) {
                        drawMotherOfPearlBead(
                            cx = whitePos.x,
                            cy = whitePos.y,
                            radius = whiteBeadRadius,
                            isCompleted = true,
                            pulse = 1f,
                            alpha = whiteAlpha
                        )
                    }
                }
            } else {
                // Stationary resting state (between prayers or actively praying):
                if (completedHailMarys == 0) {
                    // White Bead is resting in center slot
                    drawMotherOfPearlBead(
                        cx = centerSlotPos.x,
                        cy = centerSlotPos.y,
                        radius = whiteBeadRadius,
                        isCompleted = true,
                        pulse = 1f
                    )
                } else {
                    // Most recently completed Hail Mary bead is resting in center slot
                    drawRosaryBead(
                        cx = centerSlotPos.x,
                        cy = centerSlotPos.y,
                        radius = decadeBeadRadius,
                        primaryColor = primaryColor,
                        specularColor = specularColor,
                        darkColor = darkColor,
                        isCompleted = true,
                        pulse = 1f
                    )

                    // Previous Hail Mary beads are resting on the left
                    for (h in 1 until completedHailMarys) {
                        val slotIdx = (completedHailMarys - 1) - h
                        val bPos = getLeftSlot(slotIdx)
                        val bAlpha = (bPos.x / with(density) { 24.dp.toPx() }).coerceIn(0f, 1f)
                        if (bAlpha > 0.04f) {
                            drawRosaryBead(
                                cx = bPos.x,
                                cy = bPos.y,
                                radius = decadeBeadRadius,
                                primaryColor = primaryColor,
                                specularColor = specularColor,
                                darkColor = darkColor,
                                isCompleted = true,
                                pulse = 1f,
                                alpha = bAlpha
                            )
                        }
                    }

                    // White Bead is resting on the left
                    val whitePos = getLeftSlot(completedHailMarys - 1)
                    val whiteAlpha = (whitePos.x / with(density) { 26.dp.toPx() }).coerceIn(0f, 1f)
                    if (whiteAlpha > 0.04f) {
                        drawMotherOfPearlBead(
                            cx = whitePos.x,
                            cy = whitePos.y,
                            radius = whiteBeadRadius,
                            isCompleted = true,
                            pulse = 1f,
                            alpha = whiteAlpha
                        )
                    }
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
 * Draws a segment of antique gold rosary chain links between two points.
 */
private fun DrawScope.drawGoldChainSegment(from: Offset, to: Offset) {
    val dx = to.x - from.x
    val dy = to.y - from.y
    val dist = kotlin.math.sqrt(dx * dx + dy * dy)
    val linkSpacing = 7.dp.toPx()
    val steps = (dist / linkSpacing).toInt().coerceAtLeast(1)

    for (step in 1 until steps) {
        val t = step.toFloat() / steps.toFloat()
        val lx = from.x + (dx * t)
        val ly = from.y + (dy * t)

        // Chain link shadow
        drawCircle(
            color = Color.Black.copy(alpha = 0.4f),
            radius = 2.2.dp.toPx(),
            center = Offset(lx + 0.5.dp.toPx(), ly + 0.5.dp.toPx())
        )

        // Golden oval chain ring
        drawCircle(
            color = MedievalGoldDark,
            radius = 2.2.dp.toPx(),
            center = Offset(lx, ly),
            style = Stroke(width = 1.2.dp.toPx())
        )
        drawCircle(
            color = MedievalGoldBright,
            radius = 1.3.dp.toPx(),
            center = Offset(lx, ly)
        )
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
    isHoldPraying: Boolean = false,
    alpha: Float = 1f
) {
    if (alpha <= 0.03f) return

    // 1. Soft depth shadow
    drawCircle(
        color = Color.Black.copy(alpha = 0.5f * alpha),
        radius = radius * 1.05f,
        center = Offset(cx + 1.5.dp.toPx(), cy + 2.5.dp.toPx())
    )

    // 2. Receiving Slot Target Glow (pulsing gold beacon)
    if (isCenterReceiving) {
        val auraRadius = radius * (if (isHoldPraying) 2.2f else 1.75f) * pulse
        val auraBrush = Brush.radialGradient(
            colors = listOf(
                GoldGlow.copy(alpha = (if (isHoldPraying) 0.85f else 0.55f) * alpha),
                MedievalGoldBright.copy(alpha = (if (isHoldPraying) 0.5f else 0.25f) * alpha),
                Color.Transparent
            ),
            center = Offset(cx, cy),
            radius = auraRadius
        )
        drawCircle(brush = auraBrush, radius = auraRadius, center = Offset(cx, cy))

        // Delicate target shimmer ring
        drawCircle(
            color = MedievalGoldBright.copy(alpha = (((pulse - 0.82f) / 0.36f).coerceIn(0.2f, 0.9f)) * alpha),
            radius = radius + 3.dp.toPx(),
            center = Offset(cx, cy),
            style = Stroke(width = 1.2.dp.toPx())
        )
    }

    // 3. Hollow dark recess showing background through socket
    drawCircle(
        color = Color(0xFF0F0B08).copy(alpha = 0.75f * alpha),
        radius = radius - 1.5.dp.toPx(),
        center = Offset(cx, cy)
    )

    // 4. Antique gold wire bezel ring
    val bezelBrush = Brush.linearGradient(
        colors = if (isPearl) {
            listOf(PearlWhite.copy(alpha = alpha), MedievalGoldBright.copy(alpha = alpha), MedievalGoldDark.copy(alpha = alpha))
        } else {
            listOf(MedievalGoldBright.copy(alpha = alpha), MedievalGold.copy(alpha = alpha), MedievalGoldDark.copy(alpha = alpha))
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
        color = MedievalGoldDark.copy(alpha = 0.6f * alpha),
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
            color = MedievalGoldBright.copy(alpha = alpha),
            radius = clawRadius,
            center = Offset(px, py)
        )
    }
}

/**
 * Draws an authentic 3D liturgical gemstone bead with specular highlight, gold end-caps, and halo.
 */
fun DrawScope.drawRosaryBead(
    cx: Float,
    cy: Float,
    radius: Float,
    primaryColor: Color,
    specularColor: Color,
    darkColor: Color,
    isCompleted: Boolean,
    pulse: Float = 1f,
    alpha: Float = 1f
) {
    if (alpha <= 0.03f) return

    // Drop shadow
    drawCircle(
        color = Color.Black.copy(alpha = 0.50f * alpha),
        radius = radius * 1.05f,
        center = Offset(cx + 1.5.dp.toPx(), cy + 2.dp.toPx())
    )

    // Completed Golden Halo Glow
    if (isCompleted) {
        val glowRadius = radius * 1.8f * pulse
        val haloBrush = Brush.radialGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.65f * alpha),
                GoldGlow.copy(alpha = 0.35f * alpha),
                Color.Transparent
            ),
            center = Offset(cx, cy),
            radius = glowRadius
        )
        drawCircle(brush = haloBrush, radius = glowRadius, center = Offset(cx, cy))

        // Illuminated golden perimeter ring
        drawCircle(
            color = MedievalGoldBright.copy(alpha = alpha),
            radius = radius + 2.dp.toPx(),
            center = Offset(cx, cy),
            style = Stroke(width = 1.8.dp.toPx())
        )
    }

    // 3D Gemstone sphere with specular highlight
    val gemBrush = Brush.radialGradient(
        colors = listOf(
            if (isCompleted) specularColor.copy(alpha = alpha) else primaryColor.copy(alpha = 0.95f * alpha),
            primaryColor.copy(alpha = alpha),
            darkColor.copy(alpha = alpha)
        ),
        center = Offset(cx - radius * 0.35f, cy - radius * 0.35f),
        radius = radius * 1.25f
    )
    drawCircle(brush = gemBrush, radius = radius, center = Offset(cx, cy))

    // Tiny specular gloss reflection at top-left
    drawCircle(
        color = Color.White.copy(alpha = (if (isCompleted) 0.85f else 0.60f) * alpha),
        radius = radius * 0.28f,
        center = Offset(cx - radius * 0.38f, cy - radius * 0.38f)
    )

    // Gold end ring caps (drilled hole mounts)
    drawCircle(
        color = MedievalGoldBright.copy(alpha = alpha),
        radius = radius * 0.22f,
        center = Offset(cx, cy - radius * 0.88f)
    )
    drawCircle(
        color = MedievalGoldBright.copy(alpha = alpha),
        radius = radius * 0.22f,
        center = Offset(cx, cy + radius * 0.88f)
    )
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
    drawRosaryBead(
        cx = cx,
        cy = cy,
        radius = radius,
        primaryColor = UltramarineLapis,
        specularColor = UltramarineSpecular,
        darkColor = UltramarineDark,
        isCompleted = isCompleted,
        pulse = pulse
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
    pulse: Float = 1f,
    alpha: Float = 1f
) {
    if (alpha <= 0.03f) return

    // Drop shadow
    drawCircle(
        color = Color.Black.copy(alpha = 0.50f * alpha),
        radius = radius * 1.05f,
        center = Offset(cx + 1.5.dp.toPx(), cy + 2.dp.toPx())
    )

    // Halo if completed
    if (isCompleted) {
        val glowRadius = radius * 1.9f * pulse
        val haloBrush = Brush.radialGradient(
            colors = listOf(
                GoldGlow.copy(alpha = 0.85f * alpha),
                MedievalGoldBright.copy(alpha = 0.4f * alpha),
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
            Color.White.copy(alpha = alpha),
            PearlWhite.copy(alpha = alpha),
            PearlShadow.copy(alpha = alpha),
            MedievalGoldDark.copy(alpha = 0.5f * alpha)
        ),
        center = Offset(cx - radius * 0.35f, cy - radius * 0.35f),
        radius = radius * 1.25f
    )
    drawCircle(brush = pearlBrush, radius = radius, center = Offset(cx, cy))

    // Specular highlight
    drawCircle(
        color = Color.White.copy(alpha = 0.95f * alpha),
        radius = radius * 0.32f,
        center = Offset(cx - radius * 0.35f, cy - radius * 0.35f)
    )

    // Gold filigree caps on top and bottom
    val capBrush = Brush.linearGradient(
        colors = listOf(MedievalGoldBright.copy(alpha = alpha), MedievalGold.copy(alpha = alpha), MedievalGoldDark.copy(alpha = alpha)),
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
    pulse: Float = 1f,
    primaryColor: Color = UltramarineLapis,
    specularColor: Color = UltramarineSpecular,
    darkColor: Color = UltramarineDark
) {
    when (step) {
        com.trivium.rosarium.data.model.DecadeStep.OUR_FATHER -> drawMotherOfPearlBead(cx, cy, radius * 1.15f, isCompleted, pulse)
        com.trivium.rosarium.data.model.DecadeStep.HAIL_MARY -> drawRosaryBead(cx, cy, radius, primaryColor, specularColor, darkColor, isCompleted, pulse)
        com.trivium.rosarium.data.model.DecadeStep.GLORY_BE -> drawAmberGoldBead(cx, cy, radius, isCompleted, pulse)
        com.trivium.rosarium.data.model.DecadeStep.FATIMA -> drawRubyCrimsonBead(cx, cy, radius, isCompleted, pulse)
        com.trivium.rosarium.data.model.DecadeStep.DECADE_FINISHED -> drawAmethystBead(cx, cy, radius * 1.1f, isCompleted, pulse)
    }
}

/**
 * Draws the ornate Gothic trefoil Latin crucifix pendant centerpiece.
 */
fun DrawScope.drawCrucifixPendant(cx: Float, cy: Float) {
    val crossW = 15.dp.toPx()
    val crossH = 22.dp.toPx()
    val barH = 3.6.dp.toPx()
    val barW = 3.6.dp.toPx()

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
        cornerRadius = CornerRadius(1.2.dp.toPx(), 1.2.dp.toPx())
    )

    // Horizontal beam
    drawRoundRect(
        brush = goldBrush,
        topLeft = Offset(cx - crossW / 2f, cy - crossH * 0.2f),
        size = Size(crossW, barH),
        cornerRadius = CornerRadius(1.2.dp.toPx(), 1.2.dp.toPx())
    )

    // Central ruby / sacred heart accent
    drawCircle(
        color = OxbloodRed,
        radius = 1.6.dp.toPx(),
        center = Offset(cx, cy - crossH * 0.2f + barH / 2f)
    )
}
