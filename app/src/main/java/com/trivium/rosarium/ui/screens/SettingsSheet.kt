package com.trivium.rosarium.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.graphics.Color
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
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BackgroundSurface,
        scrimColor = Color.Black.copy(alpha = 0.65f),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Settings & Preferences",
                color = ParchmentLight,
                fontSize = 22.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // 1. Language Selector
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

            // 3 items per row grid
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
                    // Fill remaining slots in last row if not divisible by 3
                    repeat(3 - rowLanguages.size) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Accessibility: Tap to Pray Toggle
            SettingToggleItem(
                title = "Tap to Pray (Accessibility)",
                subtitle = "Tap and hold bead instead of dragging downward",
                checked = settings.tapToPrayEnabled,
                onCheckedChange = { onUpdateSettings(settings.copy(tapToPrayEnabled = it)) }
            )

            // 3. Audio & Haptics
            SettingToggleItem(
                title = "Sound Effects",
                subtitle = "Bead clacks, string tension, completion chime",
                checked = settings.soundEffectsEnabled,
                onCheckedChange = { onUpdateSettings(settings.copy(soundEffectsEnabled = it)) }
            )

            SettingToggleItem(
                title = "Haptic Pulses",
                subtitle = "Tactile feedback during prayer and bounces",
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
                subtitle = "Recite 'O my Jesus' after Glory Be",
                checked = settings.includeFatimaPrayer,
                onCheckedChange = { onUpdateSettings(settings.copy(includeFatimaPrayer = it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Prayer Duration Slider
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Prayer Duration",
                        color = ParchmentLight,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "${settings.prayerDurationSeconds}s",
                        color = MedievalGoldBright,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                }
                Slider(
                    value = settings.prayerDurationSeconds.toFloat(),
                    onValueChange = { onUpdateSettings(settings.copy(prayerDurationSeconds = it.toInt())) },
                    valueRange = 10f..30f,
                    steps = 19,
                    colors = SliderDefaults.colors(
                        thumbColor = MedievalGoldBright,
                        activeTrackColor = MedievalGold,
                        inactiveTrackColor = Color(0xFF221A14)
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 5. Font Scale Slider
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
