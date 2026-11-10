package com.trivium.rosarium.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trivium.rosarium.R
import com.trivium.rosarium.data.model.PrayerStats
import com.trivium.rosarium.data.model.RosaryState
import com.trivium.rosarium.ui.components.MedievalPrayerHeatmap

private val NavyHeader = Color(0xFF0D2036)
private val ParchmentBg = Color(0xFFF7F3EA)
private val CardIvory = Color(0xFFFFFFFF)
private val GoldAccent = Color(0xFFC5A059)
private val CharcoalText = Color(0xFF1E2836)
private val MutedText = Color(0xFF6B7A8D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressSheet(
    state: RosaryState,
    stats: PrayerStats,
    isDark: Boolean = false,
    onResetRosary: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val bgColor = if (isDark) Color(0xFF09121D) else ParchmentBg

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = bgColor,
        scrimColor = Color.Black.copy(alpha = 0.75f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        ProgressScreenContent(
            state = state,
            stats = stats,
            isDark = isDark,
            onResetRosary = onResetRosary,
            onBack = onDismiss
        )
    }
}

@Composable
fun ProgressScreenContent(
    state: RosaryState,
    stats: PrayerStats,
    isDark: Boolean = false,
    onResetRosary: () -> Unit,
    onBack: () -> Unit
) {
    var activeTab by remember { mutableStateOf("Overview") }
    val streak = stats.calculateStreak().coerceAtLeast(1)

    val bgColor = if (isDark) Color(0xFF09121D) else ParchmentBg
    val cardBg = if (isDark) Color(0xFF132235) else CardIvory
    val cardBorder = if (isDark) Color(0xFF233B57) else Color(0xFFE2D9CB)
    val titleColor = if (isDark) Color(0xFFF7F3EA) else CharcoalText
    val subColor = if (isDark) Color(0xFF9FB2C8) else MutedText
    val filterBorder = if (isDark) Color(0xFF233B57) else Color(0xFFDDD4C5)
    val accentNavy = if (isDark) Color(0xFF1E3A5F) else NavyHeader
    val iconAccent = if (isDark) GoldAccent else GoldAccent

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        // 1. Deep Navy Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavyHeader)
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "Prayer Progress",
                color = Color.White,
                fontSize = 19.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 18.dp, vertical = 14.dp)
            ) {
                // 2. Filter Pills: Overview / History
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf("Overview", "History").forEach { tab ->
                        val isSelected = activeTab == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isSelected) NavyHeader else cardBg)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) NavyHeader else filterBorder,
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable { activeTab = tab }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = tab,
                                color = if (isSelected) Color.White else titleColor,
                                fontSize = 13.5.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. Stats Row (Day Streak, Rosaries, Novenas)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCardItem(
                        icon = Icons.Default.LocalFireDepartment,
                        iconTint = Color(0xFFE65100),
                        value = "$streak",
                        label = "Day Streak",
                        isDark = isDark,
                        modifier = Modifier.weight(1f)
                    )

                    StatCardDrawableItem(
                        drawableRes = R.drawable.ic_rosary_loop,
                        value = "${stats.totalRosaries.coerceAtLeast(1)}",
                        label = "Rosaries",
                        isDark = isDark,
                        modifier = Modifier.weight(1f)
                    )

                    StatCardDrawableItem(
                        drawableRes = R.drawable.ic_novena_calendar,
                        value = "7",
                        label = "Novenas",
                        isDark = isDark,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 4. Monthly Prayer Activity Heatmap Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(cardBg)
                        .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Monthly Prayer Activity",
                                color = titleColor,
                                fontSize = 15.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            )

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ChevronLeft,
                                    contentDescription = "Previous Month",
                                    tint = subColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "Nov 2026",
                                    color = titleColor,
                                    fontSize = 12.5.sp,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 4.dp)
                                )
                                Icon(
                                    imageVector = Icons.Default.ChevronRight,
                                    contentDescription = "Next Month",
                                    tint = subColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        MedievalPrayerHeatmap(
                            stats = stats
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // 5. "My Goal" Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(cardBg)
                        .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(if (isDark) Color(0xFF0D1926) else Color(0xFFF9F5EC))
                                    .border(1.dp, if (isDark) Color(0xFF233B57) else GoldAccent.copy(alpha = 0.5f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.TrackChanges,
                                    contentDescription = "Goal",
                                    tint = GoldAccent,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "My Goal",
                                    color = subColor,
                                    fontSize = 11.5.sp,
                                    fontFamily = FontFamily.Serif
                                )
                                Text(
                                    text = "Pray the Rosary daily",
                                    color = titleColor,
                                    fontSize = 14.5.sp,
                                    fontFamily = FontFamily.Serif,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            Text(
                                text = "24 / 30 days",
                                color = if (isDark) GoldAccent else NavyHeader,
                                fontSize = 13.5.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { 24f / 30f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = if (isDark) GoldAccent else NavyHeader,
                            trackColor = if (isDark) Color(0xFF1B2C40) else Color(0xFFE2D9CB)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

@Composable
private fun StatCardItem(
    icon: ImageVector,
    iconTint: Color,
    value: String,
    label: String,
    isDark: Boolean = false,
    modifier: Modifier = Modifier
) {
    val cardBg = if (isDark) Color(0xFF132235) else CardIvory
    val cardBorder = if (isDark) Color(0xFF233B57) else Color(0xFFE2D9CB)
    val titleColor = if (isDark) Color(0xFFF7F3EA) else CharcoalText
    val subColor = if (isDark) Color(0xFF9FB2C8) else MutedText

    Box(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(cardBg)
            .border(1.dp, cardBorder, RoundedCornerShape(14.dp))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = iconTint,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = titleColor,
                fontSize = 18.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = subColor,
                fontSize = 11.sp,
                fontFamily = FontFamily.Serif
            )
        }
    }
}

@Composable
private fun StatCardDrawableItem(
    drawableRes: Int,
    value: String,
    label: String,
    isDark: Boolean = false,
    modifier: Modifier = Modifier
) {
    val cardBg = if (isDark) Color(0xFF132235) else CardIvory
    val cardBorder = if (isDark) Color(0xFF233B57) else Color(0xFFE2D9CB)
    val titleColor = if (isDark) Color(0xFFF7F3EA) else CharcoalText
    val subColor = if (isDark) Color(0xFF9FB2C8) else MutedText

    Box(
        modifier = modifier
            .shadow(2.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(cardBg)
            .border(1.dp, cardBorder, RoundedCornerShape(14.dp))
            .padding(vertical = 12.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            androidx.compose.foundation.Image(
                painter = painterResource(id = drawableRes),
                contentDescription = label,
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = titleColor,
                fontSize = 18.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = subColor,
                fontSize = 11.sp,
                fontFamily = FontFamily.Serif
            )
        }
    }
}
