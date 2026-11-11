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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.Dp
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
    customMaxHeight: Dp? = null,
    modifier: Modifier = Modifier
) {
    if (prayer == null) return

    val maxBannerHeight = customMaxHeight ?: when {
        fontScale >= 1.25f -> 132.dp
        fontScale >= 1.10f -> 142.dp
        else -> 155.dp
    }

    val stanzas = remember(prayer.id, prayer.phrases) {
        formatLiturgicalStanzas(prayer.phrases)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp)
            .widthIn(max = 420.dp)
            .heightIn(max = maxBannerHeight),
        contentAlignment = Alignment.Center
    ) {
        ProceduralParchmentScroll(modifier = Modifier.matchParentSize())

        // Prayer Text Container
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 5.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Caption "— PRAYING —"
            Text(
                text = "— PRAYING —",
                fontSize = (9f * fontScale).coerceIn(8.5f, 10.5f).sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                color = MedievalGoldDark,
                letterSpacing = 1.4.sp,
                textAlign = TextAlign.Center
            )

            // Header (e.g. "Hail Mary" or "Our Father")
            Text(
                text = prayer.title,
                fontSize = (15f * fontScale).coerceIn(13.5f, 17.5f).sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = InkBrownDark,
                letterSpacing = 0.2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Prayer text formatted in elegant flowing liturgical stanzas (prevents excessive height)
            stanzas.forEachIndexed { index, stanza ->
                if (index > 0) {
                    Spacer(modifier = Modifier.height(2.dp))
                }
                Text(
                    text = stanza,
                    fontSize = (11.5f * fontScale).coerceIn(11f, 15f).sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Normal,
                    color = InkBrownDark.copy(alpha = 0.92f),
                    lineHeight = (15f * fontScale).coerceIn(14.5f, 19.5f).sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(3.dp))

            // Bottom Ornamental Cross Divider (— ✙ —)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(0.50f)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(MedievalGoldDark.copy(alpha = 0.6f))
                )
                Canvas(modifier = Modifier.padding(horizontal = 4.dp).size(9.dp)) {
                    val w = size.width
                    val h = size.height
                    val cx = w / 2f
                    val cy = h / 2f
                    // Draw central cross
                    drawLine(
                        color = MedievalGold,
                        start = Offset(cx, 1f),
                        end = Offset(cx, h - 1f),
                        strokeWidth = 1.2.dp.toPx()
                    )
                    drawLine(
                        color = MedievalGold,
                        start = Offset(1f, cy),
                        end = Offset(w - 1f, cy),
                        strokeWidth = 1.2.dp.toPx()
                    )
                    drawCircle(MedievalGoldBright, radius = 1.dp.toPx(), center = Offset(cx, cy))
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
 * Formats comma-separated phrase lists into natural, balanced liturgical stanzas
 * so text wraps cleanly and avoids excessive vertical elongation on larger fonts.
 */
private fun formatLiturgicalStanzas(phrases: List<String>): List<String> {
    if (phrases.isEmpty()) return emptyList()
    val fullText = phrases.joinToString(" ") { it.trim() }
        .replace(Regex("\\s+"), " ")

    val sentences = fullText.split(Regex("(?<=[.?!])\\s+")).filter { it.isNotBlank() }
    if (sentences.size <= 2) {
        return listOf(fullText)
    }
    val mid = (sentences.size + 1) / 2
    val stanza1 = sentences.subList(0, mid).joinToString(" ")
    val stanza2 = sentences.subList(mid, sentences.size).joinToString(" ")
    return listOf(stanza1, stanza2)
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
