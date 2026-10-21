package com.rosarium.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rosarium.app.data.model.PrayerStats
import com.rosarium.app.ui.theme.MedievalGold
import com.rosarium.app.ui.theme.MedievalGoldBright
import com.rosarium.app.ui.theme.MedievalGoldDark
import com.rosarium.app.ui.theme.ParchmentBase
import com.rosarium.app.ui.theme.ParchmentDark
import com.rosarium.app.ui.theme.ParchmentLight
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * GitHub-style prayer history heatmap styled with an illuminated medieval manuscript aesthetic:
 * - 7 rows representing Sunday through Saturday
 * - 16 weeks of prayer devotion leading up to today
 * - Illuminated gold-leaf intensity tiers (empty stone -> antique bronze -> brass -> illuminated gold -> radiant gold-leaf)
 * - Interactive tap to inspect historical devotion for any specific day
 */
@Composable
fun MedievalPrayerHeatmap(
    stats: PrayerStats,
    modifier: Modifier = Modifier
) {
    val today = remember { LocalDate.now() }
    var selectedDate by remember { mutableStateOf(today) }

    // Number of past weeks to display (16 weeks = 112 days)
    val totalWeeks = 16

    // Calculate grid start date: Sunday of (totalWeeks - 1) weeks ago
    val startDay = remember(today) {
        val currentSunday = today.minusDays(today.dayOfWeek.value % 7L)
        currentSunday.minusWeeks((totalWeeks - 1).toLong())
    }

    val scrollState = rememberScrollState()

    // Auto-scroll to the latest week on launch
    LaunchedEffect(Unit) {
        scrollState.scrollTo(scrollState.maxValue)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MedievalGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .background(Color(0xFF1B140F))
            .padding(16.dp)
    ) {
        // 1. Header with Title & Active Selected Day Tooltip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Devotion Heatmap",
                color = MedievalGoldBright,
                fontSize = 15.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )

            val selectedCount = stats.getCountForDate(selectedDate)
            val countLabel = when {
                selectedCount >= 53 -> "$selectedCount prayers (Rosary prayed)"
                selectedCount >= 10 -> "$selectedCount prayers (${selectedCount / 10} decades)"
                selectedCount > 0 -> "$selectedCount prayers"
                else -> "No prayers recorded"
            }

            Text(
                text = "${selectedDate.format(DateTimeFormatter.ofPattern("d MMM"))}: $countLabel",
                color = if (selectedCount > 0) MedievalGoldBright else ParchmentDark,
                fontSize = 11.5.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 2. The 7x16 Contribution Grid
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(scrollState),
            verticalAlignment = Alignment.Top
        ) {
            // Day-of-week labels on the left (Sun, Mon, Tue, Wed, Thu, Fri, Sat)
            Column(
                modifier = Modifier.padding(end = 6.dp),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                val dayLabels = listOf("S", "M", "T", "W", "T", "F", "S")
                dayLabels.forEach { label ->
                    Box(
                        modifier = Modifier.size(13.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = MedievalGoldDark,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Columns representing weeks
            for (w in 0 until totalWeeks) {
                val weekStart = startDay.plusWeeks(w.toLong())

                Column(
                    modifier = Modifier.padding(horizontal = 1.5.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    for (d in 0 until 7) {
                        val date = weekStart.plusDays(d.toLong())
                        val isFuture = date.isAfter(today)
                        val isSelected = date == selectedDate
                        val count = if (isFuture) 0 else stats.getCountForDate(date)
                        val level = stats.getIntensityLevel(count)

                        HeatmapCell(
                            level = level,
                            isFuture = isFuture,
                            isSelected = isSelected,
                            onClick = {
                                if (!isFuture) {
                                    selectedDate = date
                                }
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3. Legend at bottom (Less -> More in medieval gold tiers)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Less",
                color = ParchmentDark,
                fontSize = 10.sp,
                fontFamily = FontFamily.Serif
            )
            Spacer(modifier = Modifier.width(6.dp))

            for (lvl in 0..4) {
                HeatmapCell(
                    level = lvl,
                    isFuture = false,
                    isSelected = false,
                    onClick = {}
                )
                Spacer(modifier = Modifier.width(3.dp))
            }

            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "More",
                color = MedievalGoldBright,
                fontSize = 10.sp,
                fontFamily = FontFamily.Serif
            )
        }
    }
}

/**
 * Individual illuminated medieval tile in the heatmap.
 */
@Composable
private fun HeatmapCell(
    level: Int,
    isFuture: Boolean,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val cellColor = when {
        isFuture -> Color(0xFF140E0A).copy(alpha = 0.35f)
        level == 0 -> Color(0xFF221A13)
        level == 1 -> Color(0xFF5A4222) // Soft warm bronze
        level == 2 -> Color(0xFF9E7B26) // Antique brass-gold
        level == 3 -> Color(0xFFD4AF37) // Rich illuminated gold
        else -> Color(0xFFFFD54F)       // Radiant gold-leaf shimmer
    }

    val borderColor = when {
        isSelected -> MedievalGoldBright
        level == 4 -> Color(0xFFFFF9C4)
        level == 3 -> Color(0xFFFFE082)
        level > 0 -> MedievalGoldDark
        else -> Color(0xFF33251A)
    }

    Box(
        modifier = Modifier
            .size(13.dp)
            .clip(RoundedCornerShape(2.5.dp))
            .border(
                width = if (isSelected) 1.5.dp else 0.75.dp,
                color = borderColor,
                shape = RoundedCornerShape(2.5.dp)
            )
            .background(cellColor)
            .clickable(enabled = !isFuture, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // High devotion micro-accent (tiny gold cross center for level 4)
        if (level == 4) {
            Box(
                modifier = Modifier
                    .size(3.5.dp)
                    .clip(RoundedCornerShape(0.5.dp))
                    .background(Color.White.copy(alpha = 0.9f))
            )
        }
    }
}
