package com.trivium.rosarium.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.trivium.rosarium.data.model.DecadeStep
import com.trivium.rosarium.data.model.Mystery
import com.trivium.rosarium.ui.theme.BackgroundVignette
import com.trivium.rosarium.ui.theme.MedievalGold
import com.trivium.rosarium.ui.theme.MedievalGoldBright
import com.trivium.rosarium.ui.theme.MedievalGoldDark
import com.trivium.rosarium.ui.theme.PrayerThemes

/**
 * Dynamic Sacred Atmosphere:
 * Crossfades backgrounds, vignette tones, and sacred illumination
 * individually for each prayer (Our Father, Hail Mary, Glory Be, Fatima, Hail Holy Queen).
 */
@Composable
fun MysteryBackground(
    currentMystery: Mystery?,
    currentStep: DecadeStep = DecadeStep.HAIL_MARY,
    isHoldPraying: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageName = currentMystery?.imageAsset ?: "mystery_joyful_1"
    val resId = context.resources.getIdentifier(imageName, "drawable", context.packageName)

    val currentTheme = PrayerThemes.forStep(currentStep)

    val animatedVignette by animateColorAsState(
        targetValue = currentTheme.vignetteBase,
        animationSpec = tween(750),
        label = "vignetteColor"
    )

    val animatedAura by animateColorAsState(
        targetValue = currentTheme.auraGlow,
        animationSpec = tween(750),
        label = "auraColor"
    )

    val animatedFrescoTone by animateColorAsState(
        targetValue = currentTheme.frescoTone,
        animationSpec = tween(750),
        label = "frescoTone"
    )

    Box(modifier = modifier.fillMaxSize()) {
        Crossfade(
            targetState = Triple(currentMystery?.imageAsset, resId, currentStep),
            animationSpec = tween(durationMillis = 650),
            label = "MysteryBackgroundCrossfade"
        ) { target ->
            val (_, resolvedResId, step) = target
            if (resolvedResId != 0 && step == DecadeStep.HAIL_MARY) {
                Image(
                    painter = painterResource(id = resolvedResId),
                    contentDescription = currentMystery?.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Procedural Sacred Fresco calibrated to current prayer
                ProceduralSacredFresco(
                    step = step,
                    auraColor = animatedAura,
                    frescoTone = animatedFrescoTone,
                    isHoldPraying = isHoldPraying
                )
            }
        }

        // Vignette Layer: Dynamically tuned to the active prayer's liturgical palette
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Top gradient for banner readability
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        animatedVignette.copy(alpha = 0.90f),
                        animatedVignette.copy(alpha = 0.50f),
                        Color.Transparent
                    ),
                    startY = 0f,
                    endY = h * 0.38f
                )
            )

            // Bottom gradient for bead clarity
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        animatedVignette.copy(alpha = 0.65f),
                        animatedVignette.copy(alpha = 0.95f)
                    ),
                    startY = h * 0.55f,
                    endY = h
                )
            )

            // Radial edge darkening (soft cinematic vignette)
            drawRect(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Transparent,
                        animatedVignette.copy(alpha = 0.75f)
                    ),
                    center = Offset(w / 2f, h * 0.44f),
                    radius = w * 0.9f
                )
            )
        }
    }
}

@Composable
private fun ProceduralSacredFresco(
    step: DecadeStep,
    auraColor: Color,
    frescoTone: Color,
    isHoldPraying: Boolean
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF120E0A))
    ) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h * 0.38f

        // Celestial light rays bursting according to prayer theme
        val rayAlpha = if (isHoldPraying) 0.30f else 0.16f
        val rayBrush = Brush.radialGradient(
            colors = listOf(
                auraColor.copy(alpha = rayAlpha * 1.6f),
                frescoTone.copy(alpha = rayAlpha),
                Color.Transparent
            ),
            center = Offset(cx, cy * 0.68f),
            radius = w * 0.85f
        )
        drawCircle(brush = rayBrush, radius = w * 0.85f, center = Offset(cx, cy * 0.68f))

        when (step) {
            DecadeStep.OUR_FATHER -> {
                // Heavenly Father celestial sunburst
                val sunburstBrush = Brush.radialGradient(
                    colors = listOf(
                        MedievalGoldBright.copy(alpha = 0.35f),
                        Color(0xFFFFF9C4).copy(alpha = 0.15f),
                        Color.Transparent
                    ),
                    center = Offset(cx, h * 0.22f),
                    radius = w * 0.65f
                )
                drawCircle(brush = sunburstBrush, radius = w * 0.65f, center = Offset(cx, h * 0.22f))
            }
            DecadeStep.GLORY_BE -> {
                // Holy Trinity tri-radiant aureole
                val trinityBrush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFFFD54F).copy(alpha = 0.40f),
                        Color(0xFFFF8F00).copy(alpha = 0.20f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = w * 0.70f
                )
                drawCircle(brush = trinityBrush, radius = w * 0.70f, center = Offset(cx, cy))
            }
            DecadeStep.FATIMA -> {
                // Sacred Heart deep crimson contemplation
                val fatimaBrush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFE53935).copy(alpha = 0.32f),
                        Color(0xFF7F0000).copy(alpha = 0.20f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy),
                    radius = w * 0.75f
                )
                drawCircle(brush = fatimaBrush, radius = w * 0.75f, center = Offset(cx, cy))
            }
            DecadeStep.DECADE_FINISHED -> {
                // Queen of Heaven starfield & starlight
                val queenBrush = Brush.radialGradient(
                    colors = listOf(
                        Color(0xFFBA68C8).copy(alpha = 0.30f),
                        Color(0xFF4A148C).copy(alpha = 0.20f),
                        Color.Transparent
                    ),
                    center = Offset(cx, cy * 0.7f),
                    radius = w * 0.8f
                )
                drawCircle(brush = queenBrush, radius = w * 0.8f, center = Offset(cx, cy * 0.7f))
            }
            else -> {
                // Gothic Arch silhouette for Hail Mary
                val archPath = Path().apply {
                    moveTo(w * 0.12f, h)
                    lineTo(w * 0.12f, h * 0.28f)
                    cubicTo(w * 0.12f, h * 0.12f, cx, h * 0.08f, cx, h * 0.08f)
                    cubicTo(cx, h * 0.08f, w * 0.88f, h * 0.12f, w * 0.88f, h * 0.28f)
                    lineTo(w * 0.88f, h)
                }

                drawPath(
                    path = archPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(MedievalGoldDark.copy(alpha = 0.25f), Color.Transparent)
                    ),
                    style = Stroke(width = 3.dp.toPx())
                )
            }
        }
    }
}
