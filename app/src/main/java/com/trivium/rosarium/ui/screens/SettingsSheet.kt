package com.trivium.rosarium.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trivium.rosarium.data.model.SettingsState
import com.trivium.rosarium.ui.theme.BackgroundSurface
import com.trivium.rosarium.ui.theme.MedievalGold
import com.trivium.rosarium.ui.theme.MedievalGoldBright
import com.trivium.rosarium.ui.theme.MedievalGoldDark
import com.trivium.rosarium.ui.theme.ParchmentBase
import com.trivium.rosarium.ui.theme.ParchmentDark
import com.trivium.rosarium.ui.theme.ParchmentLight
import com.trivium.rosarium.ui.theme.UltramarineLapis

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    settings: SettingsState,
    onUpdateSettings: (SettingsState) -> Unit,
    onOpenNovenas: () -> Unit,
    onOpenProgress: () -> Unit,
    onOpenReadIntentions: () -> Unit,
    currentBeadCount: Int = 0,
    totalBeadCount: Int = 53,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                .padding(horizontal = 22.dp, vertical = 14.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Rosarium Sanctuary",
                color = MedievalGoldBright,
                fontSize = 22.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Devotions, scripture readings, progress & preferences",
                color = ParchmentDark,
                fontSize = 12.sp,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // --- 1. Top Devotions & Reading Section ---
            Text(
                text = "DEVOTIONS & MEDITATIONS",
                color = MedievalGold,
                fontSize = 11.5.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Novenas Navigation Card
            MenuNavigationCard(
                title = "9-Day Catholic Novenas",
                subtitle = "St. Jude, Divine Mercy, Undoer of Knots & more",
                badgeText = "9 Days",
                icon = {
                    Canvas(modifier = Modifier.size(20.dp)) {
                        val w = size.width
                        val h = size.height
                        // Radiance circle
                        drawCircle(
                            color = MedievalGold.copy(alpha = 0.35f),
                            radius = 4.dp.toPx(),
                            center = Offset(w * 0.5f, h * 0.38f)
                        )
                        // Latin Cross
                        drawLine(
                            color = MedievalGoldBright,
                            start = Offset(w * 0.5f, h * 0.12f),
                            end = Offset(w * 0.5f, h * 0.88f),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                        drawLine(
                            color = MedievalGoldBright,
                            start = Offset(w * 0.22f, h * 0.38f),
                            end = Offset(w * 0.78f, h * 0.38f),
                            strokeWidth = 2.dp.toPx(),
                            cap = StrokeCap.Round
                        )
                    }
                },
                onClick = {
                    onDismiss()
                    onOpenNovenas()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Prayer Progress & History Navigation Card
            MenuNavigationCard(
                title = "Prayer Progress & History",
                subtitle = "Completed rosaries, streaks & liturgical heatmap",
                badgeText = if (totalBeadCount > 0) "$currentBeadCount / $totalBeadCount" else "History",
                icon = {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = null,
                        tint = MedievalGoldBright,
                        modifier = Modifier.size(20.dp)
                    )
                },
                onClick = {
                    onDismiss()
                    onOpenProgress()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Read Scripture & Intentions Navigation Card
            MenuNavigationCard(
                title = "Read Scripture & Intentions",
                subtitle = "Active mystery scripture passage & spiritual fruit",
                badgeText = "Read",
                icon = {
                    Icon(
                        imageVector = Icons.Default.AutoStories,
                        contentDescription = null,
                        tint = MedievalGoldBright,
                        modifier = Modifier.size(20.dp)
                    )
                },
                onClick = {
                    onDismiss()
                    onOpenReadIntentions()
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // --- 2. Golden Section Divider ---
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(MedievalGold.copy(alpha = 0.3f))
                )
                Text(
                    text = "  ☩  SETTINGS & PREFERENCES  ☩  ",
                    color = MedievalGold.copy(alpha = 0.75f),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.6.sp
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(1.dp)
                        .background(MedievalGold.copy(alpha = 0.3f))
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Language Selector
            Text(
                text = "Language / மொழி / भाषा",
                color = MedievalGoldBright,
                fontSize = 14.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(10.dp))

            val languages = listOf(
                "en" to "English",
                "ml" to "മലയാളം",
                "ta" to "தமிழ்",
                "hi" to "हिन्दी",
                "kok" to "कोंकणी",
                "mr" to "मराठी",
                "te" to "తెలుగు",
                "kn" to "ಕನ್ನಡ",
                "gu" to "ગુજરાતી"
            )

            languages.chunked(3).forEach { rowLanguages ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowLanguages.forEach { (code, label) ->
                        val isSelected = settings.language == code
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) MedievalGoldBright else ParchmentDark.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(if (isSelected) UltramarineLapis else Color(0xFF1E1711))
                                .clickable { onUpdateSettings(settings.copy(language = code)) }
                                .padding(vertical = 10.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) ParchmentLight else ParchmentBase,
                                fontSize = 13.5.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                    repeat(3 - rowLanguages.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Accessibility: Tap to Pray
            SettingToggleItem(
                title = "Tap to Pray (Accessibility)",
                subtitle = "Tap and hold bead instead of dragging downward",
                checked = settings.tapToPrayEnabled,
                onCheckedChange = { onUpdateSettings(settings.copy(tapToPrayEnabled = it)) }
            )

            // Audio & Haptics
            SettingToggleItem(
                title = "Sound Effects",
                subtitle = "Bead clacks, string tension, completion chime",
                checked = settings.soundEffectsEnabled,
                onCheckedChange = { onUpdateSettings(settings.copy(soundEffectsEnabled = it)) }
            )

            SettingToggleItem(
                title = "Haptic Pulses",
                subtitle = "Tactile feedback on release and bounces",
                checked = settings.hapticFeedbackEnabled,
                onCheckedChange = { onUpdateSettings(settings.copy(hapticFeedbackEnabled = it)) }
            )

            SettingToggleItem(
                title = "Ambient Gregorian Chant",
                subtitle = "Subtle background sacred drone (optional)",
                checked = settings.ambientChantEnabled,
                onCheckedChange = { onUpdateSettings(settings.copy(ambientChantEnabled = it)) }
            )

            SettingToggleItem(
                title = "Include Fatima Prayer",
                subtitle = "Recite 'O my Jesus' after Glory Be (Holy Rosary)",
                checked = settings.includeFatimaPrayer,
                onCheckedChange = { onUpdateSettings(settings.copy(includeFatimaPrayer = it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Prayer Duration Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Prayer Duration (Hail Mary / Our Father)",
                        color = ParchmentLight,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "${settings.prayerDurationSeconds}s / ${maxOf(10, settings.prayerDurationSeconds * 2)}s",
                        color = MedievalGoldBright,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = settings.prayerDurationSeconds.toFloat(),
                    onValueChange = { onUpdateSettings(settings.copy(prayerDurationSeconds = it.toInt())) },
                    valueRange = 5f..30f,
                    steps = 24,
                    colors = SliderDefaults.colors(
                        thumbColor = MedievalGoldBright,
                        activeTrackColor = MedievalGold,
                        inactiveTrackColor = Color(0xFF221A14)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Font Scale Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Prayer Text Size",
                        color = ParchmentLight,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "${(settings.fontScale * 100).toInt()}%",
                        color = MedievalGoldBright,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = settings.fontScale,
                    onValueChange = { onUpdateSettings(settings.copy(fontScale = it)) },
                    valueRange = 0.85f..1.35f,
                    colors = SliderDefaults.colors(
                        thumbColor = MedievalGoldBright,
                        activeTrackColor = MedievalGold,
                        inactiveTrackColor = Color(0xFF221A14)
                    )
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun MenuNavigationCard(
    title: String,
    subtitle: String,
    badgeText: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, MedievalGold.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
            .background(Color(0xFF1E1711))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFF120C08))
                .border(1.dp, MedievalGold.copy(alpha = 0.7f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = ParchmentLight,
                fontSize = 14.5.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                color = ParchmentDark,
                fontSize = 11.sp,
                fontFamily = FontFamily.Serif
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(Color(0xFF2E2218))
                .border(1.dp, MedievalGoldDark.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = badgeText,
                color = MedievalGoldBright,
                fontSize = 11.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MedievalGoldBright.copy(alpha = 0.7f),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun SettingToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = ParchmentLight,
                fontSize = 14.5.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = ParchmentDark,
                fontSize = 11.5.sp,
                fontFamily = FontFamily.Serif
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = ParchmentLight,
                checkedTrackColor = MedievalGold,
                uncheckedThumbColor = ParchmentDark,
                uncheckedTrackColor = Color(0xFF221A14)
            )
        )
    }
}
