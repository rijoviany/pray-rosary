package com.trivium.rosarium.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.trivium.rosarium.data.model.DecadeStep
import com.trivium.rosarium.data.model.Mystery
import com.trivium.rosarium.ui.theme.MedievalGold
import com.trivium.rosarium.ui.theme.MedievalGoldBright
import com.trivium.rosarium.ui.theme.MedievalGoldDark
import com.trivium.rosarium.ui.theme.PrayerThemes

/**
 * Atmospheric Cathedral Sanctuary Background:
 * Deep monastic darkness, soaring gothic stone arches, and glowing altar candles.
 */
@Composable
fun MysteryBackground(
    currentMystery: Mystery?,
    currentStep: DecadeStep = DecadeStep.HAIL_MARY,
    isHoldPraying: Boolean,
    modifier: Modifier = Modifier
) {
    val currentTheme = PrayerThemes.forStep(currentStep)

    val animatedVignette by animateColorAsState(
        targetValue = currentTheme.vignetteBase,
        animationSpec = tween(750),
        label = "vignetteColor"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // 1. Deep Sanctuary stone background
        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFF090705),
                    Color(0xFF140F0A),
                    Color(0xFF0A0705)
                )
            )
        )

        // 2. Gothic Cathedral Arch outlines in background
        val archColor = MedievalGoldDark.copy(alpha = 0.12f)
        fun drawGothicArch(cx: Float, baseY: Float, width: Float, height: Float) {
            val path = Path().apply {
                moveTo(cx - width / 2f, baseY)
                lineTo(cx - width / 2f, baseY - height * 0.65f)
                cubicTo(
                    cx - width / 2f, baseY - height * 0.95f,
                    cx, baseY - height,
                    cx, baseY - height
                )
                cubicTo(
                    cx, baseY - height,
                    cx + width / 2f, baseY - height * 0.95f,
                    cx + width / 2f, baseY - height * 0.65f
                )
                lineTo(cx + width / 2f, baseY)
            }
            drawPath(path, archColor, style = Stroke(width = 2.dp.toPx()))
        }

        // Center Nave Arch
        drawGothicArch(w / 2f, h * 0.85f, w * 0.78f, h * 0.72f)
        // Left Column Arch
        drawGothicArch(w * 0.10f, h * 0.95f, w * 0.40f, h * 0.60f)
        // Right Column Arch
        drawGothicArch(w * 0.90f, h * 0.95f, w * 0.40f, h * 0.60f)

        // 3. Warm candle glows on left and right columns
        fun drawCandleLight(x: Float, y: Float, radius: Float) {
            val candleBrush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFFFFD54F).copy(alpha = 0.40f),
                    Color(0xFFFF8F00).copy(alpha = 0.18f),
                    Color.Transparent
                ),
                center = Offset(x, y),
                radius = radius
            )
            drawCircle(brush = candleBrush, radius = radius, center = Offset(x, y))

            // Tiny candle flame center
            drawCircle(
                color = Color(0xFFFFF9C4),
                radius = 2.dp.toPx(),
                center = Offset(x, y)
            )
        }

        // Side altar candles
        drawCandleLight(w * 0.12f, h * 0.76f, 36.dp.toPx())
        drawCandleLight(w * 0.18f, h * 0.78f, 30.dp.toPx())
        drawCandleLight(w * 0.82f, h * 0.78f, 30.dp.toPx())
        drawCandleLight(w * 0.88f, h * 0.76f, 36.dp.toPx())

        // 4. Soft cinematic vignette
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color.Transparent,
                    animatedVignette.copy(alpha = 0.35f),
                    Color.Black.copy(alpha = 0.75f)
                ),
                center = Offset(w / 2f, h * 0.45f),
                radius = w * 0.95f
            )
        )
    }
}

/**
 * Centerpiece Mystery Medallion:
 * Displays the sacred mystery artwork framed by an ornate, multi-layered
 * baroque / gothic carved gold leaf bezel with specular highlights and a bottom trefoil cross.
 */
@Composable
fun MysteryMedallion(
    currentMystery: Mystery?,
    currentStep: DecadeStep = DecadeStep.HAIL_MARY,
    isHoldPraying: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageName = currentMystery?.imageAsset ?: "mystery_joyful_1"
    val resId = context.resources.getIdentifier(imageName, "drawable", context.packageName)

    Box(
        modifier = modifier.size(195.dp),
        contentAlignment = Alignment.Center
    ) {
        // 1. Artwork inside circular crop with drop shadow
        Box(
            modifier = Modifier
                .size(172.dp)
                .shadow(12.dp, CircleShape)
                .clip(CircleShape)
        ) {
            Crossfade(
                targetState = Pair(resId, currentMystery?.imageAsset),
                animationSpec = tween(durationMillis = 500),
                label = "MedallionCrossfade"
            ) { (resolvedResId, _) ->
                if (resolvedResId != 0) {
                    Image(
                        painter = painterResource(id = resolvedResId),
                        contentDescription = currentMystery?.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    ProceduralSacredFresco(
                        step = currentStep,
                        isHoldPraying = isHoldPraying
                    )
                }
            }

            // Subtle inner vignette over artwork
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.35f)
                            )
                        )
                    )
            )
        }

        // 2. Ornate Baroque / Gothic Carved Gold Leaf Frame
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            val outerRadius = 88.dp.toPx()
            val innerRadius = 85.dp.toPx()

            // Outer drop shadow
            drawCircle(
                color = Color.Black.copy(alpha = 0.5f),
                radius = outerRadius + 2.dp.toPx(),
                center = Offset(cx + 1.dp.toPx(), cy + 3.dp.toPx()),
                style = Stroke(width = 3.dp.toPx())
            )

            // Carved gold ring bevel gradient
            val goldBrush = Brush.linearGradient(
                colors = listOf(
                    MedievalGoldBright,
                    MedievalGoldDark,
                    MedievalGold,
                    MedievalGoldBright,
                    MedievalGoldDark
                ),
                start = Offset(cx - outerRadius, cy - outerRadius),
                end = Offset(cx + outerRadius, cy + outerRadius)
            )

            drawCircle(
                brush = goldBrush,
                radius = outerRadius,
                center = Offset(cx, cy),
                style = Stroke(width = 4.5.dp.toPx())
            )

            // Inner specular rim
            drawCircle(
                color = MedievalGoldBright.copy(alpha = 0.85f),
                radius = innerRadius,
                center = Offset(cx, cy),
                style = Stroke(width = 1.2.dp.toPx())
            )

            // Outer beaded rim (filigree pearls along perimeter)
            val numBeads = 36
            val beadRingRadius = outerRadius + 3.5.dp.toPx()
            for (i in 0 until numBeads) {
                val angle = (i * 2.0 * Math.PI / numBeads).toFloat()
                val bx = cx + (beadRingRadius * kotlin.math.cos(angle))
                val by = cy + (beadRingRadius * kotlin.math.sin(angle))
                drawCircle(
                    color = MedievalGoldDark,
                    radius = 1.6.dp.toPx(),
                    center = Offset(bx + 0.5f, by + 0.5f)
                )
                drawCircle(
                    color = MedievalGoldBright,
                    radius = 1.4.dp.toPx(),
                    center = Offset(bx, by)
                )
            }

            // Bottom Ornate Trefoil Cross / Fleur-de-lis
            val bottomY = cy + outerRadius + 1.dp.toPx()
            val crossSize = 10.dp.toPx()

            val crossPath = Path().apply {
                moveTo(cx, bottomY - 2.dp.toPx())
                lineTo(cx, bottomY + crossSize)
                moveTo(cx - crossSize * 0.5f, bottomY + crossSize * 0.35f)
                lineTo(cx + crossSize * 0.5f, bottomY + crossSize * 0.35f)
            }
            drawPath(
                path = crossPath,
                color = MedievalGoldBright,
                style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            )
            // Ruby gem center on bottom cross
            drawCircle(
                color = Color(0xFFC62828),
                radius = 2.2.dp.toPx(),
                center = Offset(cx, bottomY + crossSize * 0.35f)
            )

            // Top Hanging Loop connecting to crucifix above
            val topY = cy - outerRadius
            drawOval(
                brush = goldBrush,
                topLeft = Offset(cx - 3.dp.toPx(), topY - 7.dp.toPx()),
                size = Size(6.dp.toPx(), 8.dp.toPx()),
                style = Stroke(width = 1.5.dp.toPx())
            )
        }
    }
}

/**
 * Procedural Sacred Fresco used when no local image asset exists.
 */
@Composable
private fun ProceduralSacredFresco(
    step: DecadeStep,
    isHoldPraying: Boolean
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF140F0B))
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val radius = size.width * 0.48f

        // Celestial light rays bursting from the divine center
        val sunburstBrush = Brush.radialGradient(
            colors = listOf(
                MedievalGoldBright.copy(alpha = if (isHoldPraying) 0.85f else 0.55f),
                Color(0xFFFFB300).copy(alpha = 0.35f),
                Color(0xFF1A120B).copy(alpha = 0.90f)
            ),
            center = Offset(cx, cy * 0.85f),
            radius = radius
        )
        drawCircle(brush = sunburstBrush, radius = radius, center = Offset(cx, cy))

        // Sacred Cross silhouette in the center
        val crossH = radius * 0.9f
        val crossW = radius * 0.5f
        val top = cy - crossH * 0.5f

        // Vertical bar
        drawRoundRect(
            color = MedievalGoldDark.copy(alpha = 0.75f),
            topLeft = Offset(cx - 3.dp.toPx(), top),
            size = Size(6.dp.toPx(), crossH),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )
        // Horizontal bar
        drawRoundRect(
            color = MedievalGoldDark.copy(alpha = 0.75f),
            topLeft = Offset(cx - crossW / 2f, top + crossH * 0.28f),
            size = Size(crossW, 5.5.dp.toPx()),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx(), 2.dp.toPx())
        )

        // Divine Nimbus Aura Halo
        drawCircle(
            color = MedievalGoldBright.copy(alpha = 0.45f),
            radius = crossH * 0.25f,
            center = Offset(cx, top + crossH * 0.28f),
            style = Stroke(width = 1.5.dp.toPx())
        )
    }
}
