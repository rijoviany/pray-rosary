package com.trivium.rosarium.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trivium.rosarium.data.model.Novena
import com.trivium.rosarium.data.model.NovenaProgress
import com.trivium.rosarium.ui.theme.BackgroundSurface
import com.trivium.rosarium.ui.theme.MedievalGold
import com.trivium.rosarium.ui.theme.MedievalGoldBright
import com.trivium.rosarium.ui.theme.MedievalGoldDark
import com.trivium.rosarium.ui.theme.OxbloodRed
import com.trivium.rosarium.ui.theme.ParchmentBase
import com.trivium.rosarium.ui.theme.ParchmentDark
import com.trivium.rosarium.ui.theme.ParchmentLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovenasSheet(
    novenas: List<Novena>,
    novenaProgressMap: Map<String, NovenaProgress>,
    onMarkDayComplete: (String, Int) -> Unit,
    onResetNovena: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedNovenaId by remember { mutableStateOf(novenas.firstOrNull()?.id ?: "st_jude") }
    val currentNovena = novenas.find { it.id == selectedNovenaId } ?: novenas.first()
    val progress = novenaProgressMap[currentNovena.id] ?: NovenaProgress(novenaId = currentNovena.id, currentDay = 1)

    // Inspected day on timeline
    var inspectedDayNumber by remember(currentNovena.id) {
        mutableIntStateOf(progress.currentDay.coerceIn(1, 9))
    }
    val inspectedDay = currentNovena.days.getOrNull(inspectedDayNumber - 1) ?: currentNovena.days.first()
    val isInspectedDayCompleted = progress.isDayCompleted(inspectedDayNumber)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BackgroundSurface,
        scrimColor = Color.Black.copy(alpha = 0.75f),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 14.dp)
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Approved Catholic Novenas",
                        color = MedievalGoldBright,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Nine days of persistent prayer, meditation & petition",
                        color = ParchmentDark,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Serif
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Novena Selector Carousel (Scrollable Pills)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                novenas.forEach { novena ->
                    val isSelected = novena.id == currentNovena.id
                    val novenaProgress = novenaProgressMap[novena.id]
                    val isNovenaDone = novenaProgress?.isCompleted == true
                    val activeDay = novenaProgress?.currentDay ?: 1

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) MedievalGoldBright else ParchmentDark.copy(alpha = 0.35f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .background(
                                if (isSelected) Color(0xFF2B2017) else Color(0xFF16110D)
                            )
                            .clickable {
                                selectedNovenaId = novena.id
                                val targetDay = (novenaProgressMap[novena.id]?.currentDay ?: 1).coerceIn(1, 9)
                                inspectedDayNumber = targetDay
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(novena.themeColor)
                            )
                            Spacer(modifier = Modifier.width(7.dp))
                            Text(
                                text = novena.title.removePrefix("Novena to ").removePrefix("Novena of the "),
                                color = if (isSelected) MedievalGoldBright else ParchmentLight,
                                fontSize = 12.5.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (isNovenaDone) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Completed",
                                    tint = MedievalGoldBright,
                                    modifier = Modifier.size(14.dp)
                                )
                            } else {
                                Text(
                                    text = "D$activeDay",
                                    color = if (isSelected) MedievalGold else ParchmentDark,
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Active Novena Summary Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, MedievalGold.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .background(Color(0xFF1C1510))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentNovena.title,
                            color = MedievalGoldBright,
                            fontSize = 17.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currentNovena.subtitle,
                            color = ParchmentDark,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Serif,
                            fontStyle = FontStyle.Italic
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(OxbloodRed.copy(alpha = 0.85f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = currentNovena.feastDay,
                            color = ParchmentLight,
                            fontSize = 10.5.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = currentNovena.description,
                    color = ParchmentBase.copy(alpha = 0.9f),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Serif,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Progress Bar & Percentage
                val completedCount = progress.completedDays.size
                val percentText = "${((completedCount / 9f) * 100).toInt()}%"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (progress.isCompleted) "Novena Completed • 9 of 9 Days" else "Day ${progress.currentDay} of 9 • $completedCount completed",
                        color = MedievalGoldBright,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = percentText,
                        color = MedievalGoldBright,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                LinearProgressIndicator(
                    progress = { progress.percentComplete },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MedievalGoldBright,
                    trackColor = Color(0xFF2E2218)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // 9-Day Interactive Timeline Stepper
            Text(
                text = "Nine-Day Journey",
                color = ParchmentLight,
                fontSize = 14.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                (1..9).forEach { dayNum ->
                    val isDone = progress.isDayCompleted(dayNum)
                    val isActiveToday = progress.currentDay == dayNum && !progress.isCompleted
                    val isInspected = inspectedDayNumber == dayNum

                    val bgColor by animateColorAsState(
                        targetValue = when {
                            isInspected -> MedievalGoldBright
                            isDone -> MedievalGold.copy(alpha = 0.85f)
                            isActiveToday -> Color(0xFF3B2513)
                            else -> Color(0xFF16110D)
                        },
                        animationSpec = tween(200),
                        label = "timelineBg"
                    )

                    val textColor = when {
                        isInspected -> Color(0xFF120C06)
                        isDone -> Color(0xFF120C06)
                        isActiveToday -> MedievalGoldBright
                        else -> ParchmentDark
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clickable { inspectedDayNumber = dayNum }
                            .padding(horizontal = 2.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .border(
                                    width = if (isInspected || isActiveToday) 1.8.dp else 1.dp,
                                    color = if (isInspected || isActiveToday) MedievalGoldBright else ParchmentDark.copy(alpha = 0.4f),
                                    shape = CircleShape
                                )
                                .background(bgColor),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isDone && !isInspected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Day $dayNum Completed",
                                    tint = Color(0xFF120C06),
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                Text(
                                    text = dayNum.toString(),
                                    color = textColor,
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "D$dayNum",
                            color = if (isInspected) MedievalGoldBright else ParchmentDark.copy(alpha = 0.7f),
                            fontSize = 9.5.sp,
                            fontFamily = FontFamily.Serif
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Inspected Day Parchment Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.2.dp, MedievalGoldDark, RoundedCornerShape(14.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF221A13), Color(0xFF1A130E))
                        )
                    )
                    .padding(18.dp)
            ) {
                // Day header and completion badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MedievalGold.copy(alpha = 0.2f))
                            .border(1.dp, MedievalGold.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "DAY $inspectedDayNumber OF 9",
                            color = MedievalGoldBright,
                            fontSize = 11.5.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.6.sp
                        )
                    }

                    if (isInspectedDayCompleted) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Completed",
                                tint = MedievalGoldBright,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Completed",
                                color = MedievalGoldBright,
                                fontSize = 11.5.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Theme
                Text(
                    text = inspectedDay.theme,
                    color = ParchmentLight,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Scripture
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF140F0B))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "\"${inspectedDay.scriptureText}\"",
                        color = ParchmentBase,
                        fontSize = 12.5.sp,
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 17.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "— ${inspectedDay.scriptureReference}",
                        color = MedievalGoldBright,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Intention
                Text(
                    text = "INTENTION FOR TODAY:",
                    color = MedievalGold,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = inspectedDay.intention,
                    color = ParchmentLight,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Serif,
                    lineHeight = 18.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Meditation
                Text(
                    text = "MEDITATION:",
                    color = MedievalGold,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = inspectedDay.meditation,
                    color = ParchmentBase.copy(alpha = 0.9f),
                    fontSize = 12.5.sp,
                    fontFamily = FontFamily.Serif,
                    lineHeight = 17.5.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Cross divider
                Text(
                    text = "☩  ☩  ☩",
                    color = MedievalGold.copy(alpha = 0.6f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Daily Prayer
                Text(
                    text = "DAILY NOVENA PRAYER:",
                    color = MedievalGold,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                inspectedDay.prayer.forEach { line ->
                    Text(
                        text = line,
                        color = ParchmentLight,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Serif,
                        lineHeight = 18.sp,
                        modifier = Modifier.padding(vertical = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Mark Complete / Advance Action Button
                ElevatedButton(
                    onClick = {
                        onMarkDayComplete(currentNovena.id, inspectedDayNumber)
                        if (inspectedDayNumber < 9) {
                            inspectedDayNumber += 1
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = if (isInspectedDayCompleted) Color(0xFF2C2016) else MedievalGoldBright,
                        contentColor = if (isInspectedDayCompleted) MedievalGoldBright else Color(0xFF140F0A)
                    )
                ) {
                    Icon(
                        imageVector = if (isInspectedDayCompleted) Icons.Default.Check else Icons.Default.CheckCircle,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isInspectedDayCompleted) "Mark Day $inspectedDayNumber Complete Again" else "Mark Day $inspectedDayNumber as Complete",
                        fontSize = 13.5.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Completion Celebration / Reset Option
            AnimatedVisibility(visible = progress.isCompleted) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF2E2014))
                        .border(1.dp, MedievalGoldBright.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Deo Gratias!",
                        color = MedievalGoldBright,
                        fontSize = 18.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "You have faithfully completed the 9 days of this novena. May the Lord grant your petitions in accordance with His divine Will.",
                        color = ParchmentLight,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = {
                            onResetNovena(currentNovena.id)
                            inspectedDayNumber = 1
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MedievalGoldBright)
                    ) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Pray This Novena Again", fontSize = 12.sp, fontFamily = FontFamily.Serif)
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
