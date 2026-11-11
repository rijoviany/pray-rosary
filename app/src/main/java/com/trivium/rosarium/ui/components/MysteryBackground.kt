package com.trivium.rosarium.ui.components

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.trivium.rosarium.R
import com.trivium.rosarium.data.model.DecadeStep
import com.trivium.rosarium.data.model.Mystery
import com.trivium.rosarium.ui.theme.MedievalGold
import com.trivium.rosarium.ui.theme.MedievalGoldBright
import com.trivium.rosarium.ui.theme.MedievalGoldDark
import com.trivium.rosarium.ui.theme.PrayerThemes

/**
 * Atmospheric Cathedral Sanctuary Background:
 * Authentic Romanesque Cathedral Arches with glowing golden cross and altar clouds.
 * Seamlessly transitions between Light and Dark mode cathedral backgrounds.
 */
@Composable
fun MysteryBackground(
    currentMystery: Mystery?,
    currentStep: DecadeStep = DecadeStep.HAIL_MARY,
    isHoldPraying: Boolean,
    isDark: Boolean = true,
    modifier: Modifier = Modifier
) {
    Crossfade(
        targetState = isDark,
        animationSpec = tween(durationMillis = 400),
        label = "PrayerCathedralBackgroundCrossfade",
        modifier = modifier.fillMaxSize()
    ) { dark ->
        val bgResId = if (dark) R.drawable.prayer_bg_dark else R.drawable.prayer_bg_light

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = bgResId),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter
            )

            // Subtle atmospheric sacred glow overlay when holding bead to pray
            if (isHoldPraying) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    if (dark) Color(0xFF09121D).copy(alpha = 0.35f) else Color(0xFFC5A059).copy(alpha = 0.20f)
                                )
                            )
                        )
                )
            }
        }
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
    fontScale: Float = 1.0f,
    customWidth: Dp? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val imageName = currentMystery?.imageAsset ?: "mystery_joyful_1"
    val resId = context.resources.getIdentifier(imageName, "drawable", context.packageName)

    val archWidth = customWidth ?: when {
        fontScale >= 1.25f -> 118.dp
        fontScale >= 1.10f -> 130.dp
        else -> 142.dp
    }
    val archHeight = archWidth * 1.15f
    val topCurveRadius = archWidth / 2
    val archShape = RoundedCornerShape(
        topStart = topCurveRadius,
        topEnd = topCurveRadius,
        bottomStart = 14.dp,
        bottomEnd = 14.dp
    )

    Box(
        modifier = modifier
            .size(width = archWidth, height = archHeight)
            .shadow(10.dp, archShape)
            .clip(archShape)
            .background(Color(0xFF0F2642))
            .border(
                width = 2.dp,
                brush = Brush.verticalGradient(
                    listOf(MedievalGoldBright, MedievalGold, MedievalGoldDark)
                ),
                shape = archShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(
            targetState = Pair(resId, currentMystery?.imageAsset),
            animationSpec = tween(durationMillis = 400),
            label = "ArchCrossfade"
        ) { (resolvedResId, _) ->
            if (resolvedResId != 0) {
                Image(
                    painter = painterResource(id = resolvedResId),
                    contentDescription = currentMystery?.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.TopCenter
                )
            } else {
                ProceduralSacredFresco(
                    step = currentStep,
                    isHoldPraying = isHoldPraying
                )
            }
        }

        // Inner golden specular rim
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(3.dp)
                .border(
                    width = 1.dp,
                    color = MedievalGoldBright.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(
                        topStart = topCurveRadius - 3.dp,
                        topEnd = topCurveRadius - 3.dp,
                        bottomStart = 13.dp,
                        bottomEnd = 13.dp
                    )
                )
        )
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
            .background(Color(0xFF0A1624))
    ) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val radius = size.width * 0.48f

        // Celestial light rays bursting from the divine center
        val sunburstBrush = Brush.radialGradient(
            colors = listOf(
                MedievalGoldBright.copy(alpha = if (isHoldPraying) 0.85f else 0.55f),
                Color(0xFFFFB300).copy(alpha = 0.35f),
                Color(0xFF0F2238).copy(alpha = 0.90f)
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
