package com.trivium.rosarium.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
    visible: Boolean,
    currentStep: DecadeStep = DecadeStep.HAIL_MARY,
    fontScale: Float = 1.0f,
    modifier: Modifier = Modifier
) {
    val theme = PrayerThemes.forStep(currentStep)
    AnimatedVisibility(
        visible = visible && prayer != null,
        enter = fadeIn() + scaleIn(initialScale = 0.94f),
        exit = fadeOut() + scaleOut(targetScale = 0.94f),
        modifier = modifier
    ) {
        if (prayer == null) return@AnimatedVisibility

        val context = LocalContext.current
        val bannerResId = context.resources.getIdentifier("banner_scroll", "drawable", context.packageName)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .widthIn(max = 480.dp),
            contentAlignment = Alignment.Center
        ) {
            if (bannerResId != 0) {
                Image(
                    painter = painterResource(id = bannerResId),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize(),
                    contentScale = ContentScale.FillBounds
                )
            } else {
                ProceduralParchmentScroll(modifier = Modifier.matchParentSize())
            }

            // Prayer Text Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 28.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header (e.g. "Hail Mary," or "Our Father,")
                Text(
                    text = prayer.title,
                    fontSize = (22 * fontScale).sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = InkBrownDark,
                    letterSpacing = 0.4.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Karaoke Phrase List
                val displayPhrases = prayer.phrases
                displayPhrases.forEachIndexed { index, phrase ->
                    val isCurrent = index == currentPhraseIndex
                    val isPast = index < currentPhraseIndex

                    val phraseColor = when {
                        isCurrent -> InkBrownDark
                        isPast -> InkBrown.copy(alpha = 0.95f)
                        else -> InkBrownMuted.copy(alpha = 0.45f)
                    }

                    val phraseWeight = if (isCurrent) FontWeight.SemiBold else FontWeight.Normal

                    Text(
                        text = phrase,
                        fontSize = (15.5f * fontScale).sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = phraseWeight,
                        color = phraseColor,
                        lineHeight = (22 * fontScale).sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(vertical = 1.5.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Ornamental Flourish Divider
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth(0.55f)
                        .height(14.dp)
                ) {
                    val w = size.width
                    val cy = size.height / 2f

                    // Center diamond / cross
                    val diamondPath = Path().apply {
                        moveTo(w / 2f, cy - 4.dp.toPx())
                        lineTo(w / 2f + 4.dp.toPx(), cy)
                        lineTo(w / 2f, cy + 4.dp.toPx())
                        lineTo(w / 2f - 4.dp.toPx(), cy)
                        close()
                    }
                    drawPath(diamondPath, MedievalGold)

                    // Left & Right delicate wings
                    drawLine(
                        color = MedievalGoldDark.copy(alpha = 0.7f),
                        start = Offset(0f, cy),
                        end = Offset(w / 2f - 8.dp.toPx(), cy),
                        strokeWidth = 1.dp.toPx()
                    )
                    drawLine(
                        color = MedievalGoldDark.copy(alpha = 0.7f),
                        start = Offset(w / 2f + 8.dp.toPx(), cy),
                        end = Offset(w, cy),
                        strokeWidth = 1.dp.toPx()
                    )
                }
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

        // Scroll corner curled ear accents
        fun drawEar(ox: Float, oy: Float, dx: Float, dy: Float) {
            val earPath = Path().apply {
                moveTo(ox, oy + dy * 14.dp.toPx())
                quadraticBezierTo(ox + dx * 6.dp.toPx(), oy + dy * 6.dp.toPx(), ox + dx * 14.dp.toPx(), oy)
            }
            drawPath(
                path = earPath,
                color = MedievalGoldDark,
                style = Stroke(width = 1.5.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        drawEar(4.dp.toPx(), 4.dp.toPx(), 1f, 1f)
        drawEar(w - 4.dp.toPx(), 4.dp.toPx(), -1f, 1f)
        drawEar(4.dp.toPx(), h - 4.dp.toPx(), 1f, -1f)
        drawEar(w - 4.dp.toPx(), h - 4.dp.toPx(), -1f, -1f)
    }
}
