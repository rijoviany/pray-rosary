package com.trivium.rosarium.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trivium.rosarium.data.model.DecadeStep
import com.trivium.rosarium.data.model.Prayer
import com.trivium.rosarium.ui.theme.InkBrown
import com.trivium.rosarium.ui.theme.InkBrownDark
import com.trivium.rosarium.ui.theme.InkBrownMuted
import com.trivium.rosarium.ui.theme.MedievalGold
import com.trivium.rosarium.ui.theme.MedievalGoldBright
import com.trivium.rosarium.ui.theme.MedievalGoldDark
import com.trivium.rosarium.ui.theme.OxbloodRed
import com.trivium.rosarium.ui.theme.ParchmentBase
import com.trivium.rosarium.ui.theme.ParchmentBorder
import com.trivium.rosarium.ui.theme.ParchmentDark
import com.trivium.rosarium.ui.theme.ParchmentLight
import com.trivium.rosarium.ui.theme.PrayerThemes

/**
 * Renders the illuminated parchment scroll banner displaying the active prayer text
 * with phrase-by-phrase karaoke illumination and prayer-specific theme trim.
 */
@Composable
fun PrayerBanner(
    prayer: Prayer?,
    currentPhraseIndex: Int,
    prayerProgress: Float,
    isHoldPraying: Boolean,
    currentStep: DecadeStep = DecadeStep.HAIL_MARY,
    fontScale: Float = 1.0f,
    modifier: Modifier = Modifier
) {
    if (prayer == null) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .widthIn(max = 420.dp),
        contentAlignment = Alignment.Center
    ) {
        ProceduralParchmentScroll(modifier = Modifier.matchParentSize())

        // Prayer Text Container
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Caption "— PRAYING —"
            Text(
                text = "— PRAYING —",
                fontSize = (10.5f * fontScale).sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                color = MedievalGoldDark,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(3.dp))

            // Header (e.g. "Hail Mary" or "Our Father")
            Text(
                text = prayer.title,
                fontSize = (20 * fontScale).sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = InkBrownDark,
                letterSpacing = 0.4.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Prayer Phrases (line by line karaoke)
            val displayPhrases = prayer.phrases
            displayPhrases.forEachIndexed { index, phrase ->
                val isCurrent = isHoldPraying && (index == currentPhraseIndex)
                val isPast = isHoldPraying && (index < currentPhraseIndex)

                val phraseColor = when {
                    !isHoldPraying -> InkBrownDark.copy(alpha = 0.88f)
                    isCurrent -> InkBrownDark
                    isPast -> InkBrownDark.copy(alpha = 0.75f)
                    else -> InkBrownMuted.copy(alpha = 0.42f)
                }

                val phraseWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal

                Text(
                    text = phrase,
                    fontSize = (13.5f * fontScale).sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = phraseWeight,
                    color = phraseColor,
                    lineHeight = (18.5f * fontScale).sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 0.8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Ornamental Cross Divider (— ✙ —)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(MedievalGoldDark.copy(alpha = 0.6f))
                )
                Canvas(modifier = Modifier.padding(horizontal = 6.dp).size(12.dp)) {
                    val w = size.width
                    val h = size.height
                    val cx = w / 2f
                    val cy = h / 2f
                    // Draw central cross
                    drawLine(
                        color = MedievalGold,
                        start = Offset(cx, 1f),
                        end = Offset(cx, h - 1f),
                        strokeWidth = 1.5.dp.toPx()
                    )
                    drawLine(
                        color = MedievalGold,
                        start = Offset(1f, cy),
                        end = Offset(w - 1f, cy),
                        strokeWidth = 1.5.dp.toPx()
                    )
                    drawCircle(MedievalGoldBright, radius = 1.2.dp.toPx(), center = Offset(cx, cy))
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(MedievalGoldDark.copy(alpha = 0.6f))
                )
            }
        }
    }
}

/**
 * Procedurally draws an aged medieval parchment scroll with torn deckle edges,
 * curled scroll finials, and gold-leaf border.
 */
@Composable
private fun ProceduralParchmentScroll(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val corner = 8.dp.toPx()

        // Soft parchment drop shadow
        drawRoundRect(
            color = Color.Black.copy(alpha = 0.35f),
            topLeft = Offset(2.dp.toPx(), 4.dp.toPx()),
            size = Size(w, h),
            cornerRadius = CornerRadius(corner, corner)
        )

        // Parchment base gradient (aged gradient from warm ivory to antique tan)
        val parchmentBrush = Brush.verticalGradient(
            colors = listOf(ParchmentLight, ParchmentBase, ParchmentDark),
            startY = 0f,
            endY = h
        )

        drawRoundRect(
            brush = parchmentBrush,
            topLeft = Offset.Zero,
            size = Size(w, h),
            cornerRadius = CornerRadius(corner, corner)
        )

        // Gold-leaf perimeter line with deckle indentations
        val goldStroke = Brush.linearGradient(
            colors = listOf(MedievalGoldDark, MedievalGoldBright, MedievalGold),
            start = Offset(0f, 0f),
            end = Offset(w, h)
        )

        drawRoundRect(
            brush = goldStroke,
            topLeft = Offset(4.dp.toPx(), 4.dp.toPx()),
            size = Size(w - 8.dp.toPx(), h - 8.dp.toPx()),
            cornerRadius = CornerRadius(corner - 2.dp.toPx(), corner - 2.dp.toPx()),
            style = Stroke(width = 1.5.dp.toPx())
        )

        // Scroll corner ornate baroque bracket accents
        fun drawEar(ox: Float, oy: Float, dx: Float, dy: Float) {
            val earPath = Path().apply {
                moveTo(ox, oy + dy * 16.dp.toPx())
                lineTo(ox, oy)
                lineTo(ox + dx * 16.dp.toPx(), oy)
                moveTo(ox, oy + dy * 12.dp.toPx())
                quadraticTo(ox + dx * 6.dp.toPx(), oy + dy * 6.dp.toPx(), ox + dx * 12.dp.toPx(), oy)
            }
            drawPath(
                path = earPath,
                color = MedievalGoldBright,
                style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
            )
            // Tiny corner gold dot
            drawCircle(
                color = MedievalGoldDark,
                radius = 1.5.dp.toPx(),
                center = Offset(ox + dx * 4.dp.toPx(), oy + dy * 4.dp.toPx())
            )
        }

        drawEar(4.dp.toPx(), 4.dp.toPx(), 1f, 1f)
        drawEar(w - 4.dp.toPx(), 4.dp.toPx(), -1f, 1f)
        drawEar(4.dp.toPx(), h - 4.dp.toPx(), 1f, -1f)
        drawEar(w - 4.dp.toPx(), h - 4.dp.toPx(), -1f, -1f)
    }
}
