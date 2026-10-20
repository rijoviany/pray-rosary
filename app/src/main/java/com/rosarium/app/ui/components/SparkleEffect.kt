package com.rosarium.app.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.unit.dp
import com.rosarium.app.ui.theme.GoldGlow
import com.rosarium.app.ui.theme.MedievalGoldBright
import kotlin.math.cos
import kotlin.math.sin

/**
 * Procedural or sprite-based golden sparkle burst effect that expands and fades
 * upon bead completion.
 */
@Composable
fun SparkleBurst(
    modifier: Modifier = Modifier,
    onAnimationEnd: () -> Unit = {}
) {
    val progress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 650, easing = FastOutSlowInEasing)
        )
        onAnimationEnd()
    }

    val t = progress.value
    val alpha = (1f - t).coerceIn(0f, 1f)
    val scale = 0.5f + (t * 0.9f)

    Canvas(modifier = modifier.size(54.dp)) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val maxRadius = (size.width / 2f) * scale

        // Central soft radial aura
        val auraBrush = Brush.radialGradient(
            colors = listOf(
                GoldGlow.copy(alpha = alpha * 0.9f),
                MedievalGoldBright.copy(alpha = alpha * 0.5f),
                Color.Transparent
            ),
            center = Offset(cx, cy),
            radius = maxRadius * 0.7f
        )
        drawCircle(brush = auraBrush, radius = maxRadius * 0.7f, center = Offset(cx, cy))

        // 8 radiating golden sparkle rays
        rotate(degrees = t * 45f, pivot = Offset(cx, cy)) {
            val rayCount = 8
            for (i in 0 until rayCount) {
                val angle = (i * (360f / rayCount)) * (Math.PI / 180f)
                val sparkDist = maxRadius * (0.4f + (t * 0.6f))
                val sx = cx + (cos(angle) * sparkDist).toFloat()
                val sy = cy + (sin(angle) * sparkDist).toFloat()

                val particleRadius = (2.2.dp.toPx() * (1f - t * 0.8f)).coerceAtLeast(0.5f)
                drawCircle(
                    color = MedievalGoldBright.copy(alpha = alpha),
                    radius = particleRadius,
                    center = Offset(sx, sy)
                )
            }
        }
    }
}
