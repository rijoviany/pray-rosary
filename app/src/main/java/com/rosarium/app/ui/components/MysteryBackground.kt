package com.rosarium.app.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
import com.rosarium.app.data.model.Mystery
import com.rosarium.app.ui.theme.BackgroundVignette
import com.rosarium.app.ui.theme.MedievalGold
import com.rosarium.app.ui.theme.MedievalGoldBright
import com.rosarium.app.ui.theme.MedievalGoldDark
import com.rosarium.app.ui.theme.OxbloodRed
import com.rosarium.app.ui.theme.UltramarineDark

/**
 * Renders the illuminated background for the current mystery with cross-fading,
 * soft atmospheric vignette, and illuminated sacred rays.
 */
@Composable
fun MysteryBackground(
    currentMystery: Mystery?,
    isHoldPraying: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageName = currentMystery?.imageAsset ?: "mystery_joyful_1"
    val resId = context.resources.getIdentifier(imageName, "drawable", context.packageName)

    Box(modifier = modifier.fillMaxSize()) {
        Crossfade(
            targetState = currentMystery?.imageAsset to resId,
            animationSpec = tween(durationMillis = 650),
            label = "MysteryBackgroundCrossfade"
        ) { (_, resolvedResId) ->
            if (resolvedResId != 0) {
                Image(
                    painter = painterResource(id = resolvedResId),
                    contentDescription = currentMystery?.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                // Procedural Illuminated Sacred Fresco
                ProceduralSacredFresco(
                    mysteryIndex = currentMystery?.number ?: 1,
                    isHoldPraying = isHoldPraying
                )
            }
        }

        // Vignette Layer: Keeps top banner and bottom beads exceptionally crisp and legible
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Top gradient for banner readability
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        BackgroundVignette.copy(alpha = 0.88f),
                        BackgroundVignette.copy(alpha = 0.45f),
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
                        BackgroundVignette.copy(alpha = 0.65f),
                        BackgroundVignette.copy(alpha = 0.95f)
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
                        BackgroundVignette.copy(alpha = 0.70f)
                    ),
                    center = Offset(w / 2f, h * 0.42f),
                    radius = w * 0.9f
                )
            )
        }
    }
}

@Composable
private fun ProceduralSacredFresco(
    mysteryIndex: Int,
    isHoldPraying: Boolean
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF140E0A))
    ) {
        val w = size.width
        val h = size.height
        val cx = w / 2f
        val cy = h * 0.36f

        // Celestial light rays bursting from high center
        val rayAlpha = if (isHoldPraying) 0.22f else 0.12f
        val rayBrush = Brush.radialGradient(
            colors = listOf(
                MedievalGoldBright.copy(alpha = rayAlpha * 1.5f),
                MedievalGold.copy(alpha = rayAlpha),
                Color.Transparent
            ),
            center = Offset(cx, cy * 0.7f),
            radius = w * 0.75f
        )
        drawCircle(brush = rayBrush, radius = w * 0.75f, center = Offset(cx, cy * 0.7f))

        // Architectural Gothic Arches in fresco silhouette
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

        // Soft heavenly clouds at bottom of fresco
        val cloudBrush = Brush.radialGradient(
            colors = listOf(Color(0xFF5A4432).copy(alpha = 0.35f), Color.Transparent),
            center = Offset(cx, h * 0.75f),
            radius = w * 0.65f
        )
        drawCircle(brush = cloudBrush, radius = w * 0.65f, center = Offset(cx, h * 0.75f))
    }
}
