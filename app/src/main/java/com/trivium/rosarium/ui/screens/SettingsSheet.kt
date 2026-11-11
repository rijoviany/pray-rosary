package com.trivium.rosarium.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trivium.rosarium.data.model.SettingsState
import com.trivium.rosarium.ui.theme.MedievalGold
import com.trivium.rosarium.ui.theme.MedievalGoldBright
import com.trivium.rosarium.ui.theme.MedievalGoldDark

/**
 * Dedicated Settings & Preferences Sheet:
 * Audio, Haptics, Display, Pacing, Accessibility, and Language configuration.
 * Distinct from Prayers and Devotions catalogs.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    settings: SettingsState,
    onUpdateSettings: (SettingsState) -> Unit,
    onCheckForUpdate: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val isDark = settings.isDarkMode
    val sheetBg = if (isDark) Color(0xFF09121D) else Color(0xFFF7F3EA)
    val cardBg = if (isDark) Color(0xFF132235) else Color(0xFFFFFFFF)
    val cardBorder = if (isDark) Color(0xFF233B57) else Color(0xFFE2D9CB)
    val primaryText = if (isDark) Color(0xFFF7F3EA) else Color(0xFF1E2836)
    val mutedText = if (isDark) Color(0xFF8A9FB5) else Color(0xFF6B7A8D)
    val sectionHeaderColor = if (isDark) MedievalGoldBright else Color(0xFF8C6D37)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = sheetBg,
        scrimColor = Color.Black.copy(alpha = 0.75f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0xFF162B40) else Color(0xFF0D2036)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MedievalGoldBright,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Settings & Preferences",
                        color = primaryText,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Audio, display, pacing & prayer controls",
                        color = mutedText,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Serif
                    )
                }
            }

            // ==========================================
            // 1. APPEARANCE & DISPLAY
            // ==========================================
            SectionHeader(title = "APPEARANCE & DISPLAY", color = sectionHeaderColor)

            SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
                SettingToggleItem(
                    title = "Dark Mode",
                    subtitle = "Cathedral Nocturne dark aesthetic",
                    checked = settings.isDarkMode,
                    primaryColor = primaryText,
                    mutedColor = mutedText,
                    onCheckedChange = { onUpdateSettings(settings.copy(isDarkMode = it)) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Font Scale Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Prayer Text Size",
                            color = primaryText,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Medium
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
                            inactiveTrackColor = if (isDark) Color(0xFF233B57) else Color(0xFFE2D9CB)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 2. AUDIO, HAPTICS & PACING
            // ==========================================
            SectionHeader(title = "AUDIO, HAPTICS & PACING", color = sectionHeaderColor)

            SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
                SettingToggleItem(
                    title = "Sound Effects",
                    subtitle = "Bead clacks, string tension, completion chime",
                    checked = settings.soundEffectsEnabled,
                    primaryColor = primaryText,
                    mutedColor = mutedText,
                    onCheckedChange = { onUpdateSettings(settings.copy(soundEffectsEnabled = it)) }
                )

                SettingToggleItem(
                    title = "Bead Release Vibration",
                    subtitle = "Tactile haptic pulse when bead is ready to release",
                    checked = settings.hapticFeedbackEnabled,
                    primaryColor = primaryText,
                    mutedColor = mutedText,
                    onCheckedChange = { onUpdateSettings(settings.copy(hapticFeedbackEnabled = it)) }
                )

                SettingToggleItem(
                    title = "Ambient Gregorian Chant",
                    subtitle = "Subtle background sacred drone",
                    checked = settings.ambientChantEnabled,
                    primaryColor = primaryText,
                    mutedColor = mutedText,
                    onCheckedChange = { onUpdateSettings(settings.copy(ambientChantEnabled = it)) }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Prayer Duration Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Prayer Pacing (Hail Mary / Our Father)",
                            color = primaryText,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Medium
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
                            inactiveTrackColor = if (isDark) Color(0xFF233B57) else Color(0xFFE2D9CB)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 3. PRAYER RULES & ACCESSIBILITY
            // ==========================================
            SectionHeader(title = "PRAYER RULES & ACCESSIBILITY", color = sectionHeaderColor)

            SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
                SettingToggleItem(
                    title = "Tap to Pray (Accessibility)",
                    subtitle = "Tap and hold bead instead of dragging downward",
                    checked = settings.tapToPrayEnabled,
                    primaryColor = primaryText,
                    mutedColor = mutedText,
                    onCheckedChange = { onUpdateSettings(settings.copy(tapToPrayEnabled = it)) }
                )

                SettingToggleItem(
                    title = "Include Fatima Prayer",
                    subtitle = "Recite 'O my Jesus' after Glory Be (Holy Rosary)",
                    checked = settings.includeFatimaPrayer,
                    primaryColor = primaryText,
                    mutedColor = mutedText,
                    onCheckedChange = { onUpdateSettings(settings.copy(includeFatimaPrayer = it)) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ==========================================
            // 4. LANGUAGE / LINGUA / भाषा
            // ==========================================
            SectionHeader(title = "LANGUAGE / LINGUA / भाषा", color = sectionHeaderColor)

            SettingsCard(cardBg = cardBg, cardBorder = cardBorder) {
                Text(
                    text = "GLOBAL & VATICAN",
                    color = MedievalGoldBright,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                val globalLanguages = listOf(
                    "la" to "Latina",
                    "en" to "English",
                    "es" to "Español",
                    "pt" to "Português",
                    "fr" to "Français",
                    "it" to "Italiano",
                    "de" to "Deutsch",
                    "pl" to "Polski",
                    "tl" to "Tagalog"
                )

                LanguageGrid(
                    languages = globalLanguages,
                    selectedLanguage = settings.language,
                    isDark = isDark,
                    cardBorder = cardBorder,
                    primaryText = primaryText,
                    onSelectLanguage = { code -> onUpdateSettings(settings.copy(language = code)) }
                )

                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(0.5.dp)
                        .background(cardBorder.copy(alpha = 0.6f))
                )
                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "INDIAN REGIONAL",
                    color = MedievalGoldBright,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                val indianLanguages = listOf(
                    "ml" to "മലയാളം",
                    "ta" to "தமிழ்",
                    "hi" to "हिन्दी",
                    "kok" to "कोंकणी",
                    "mr" to "मराठी",
                    "te" to "తెలుగు",
                    "kn" to "ಕನ್ನಡ",
                    "gu" to "ગુજરાતી"
                )

                LanguageGrid(
                    languages = indianLanguages,
                    selectedLanguage = settings.language,
                    isDark = isDark,
                    cardBorder = cardBorder,
                    primaryText = primaryText,
                    onSelectLanguage = { code -> onUpdateSettings(settings.copy(language = code)) }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // App Version Footer & Check for Updates
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Rosarium • v${com.trivium.rosarium.BuildConfig.VERSION_NAME} (Build ${com.trivium.rosarium.BuildConfig.VERSION_CODE})",
                    color = mutedText.copy(alpha = 0.75f),
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Serif,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, MedievalGold.copy(alpha = 0.45f), RoundedCornerShape(20.dp))
                        .background(if (isDark) Color(0xFF132338) else Color(0xFFEFE8DB))
                        .clickable { onCheckForUpdate() }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Check for updates",
                        tint = MedievalGoldBright,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = "Check for Updates",
                        color = primaryText,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun SectionHeader(title: String, color: Color) {
    Text(
        text = title,
        color = color,
        fontSize = 11.sp,
        fontFamily = FontFamily.Serif,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.8.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
    )
}

@Composable
private fun SettingsCard(
    cardBg: Color,
    cardBorder: Color,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, cardBorder, RoundedCornerShape(14.dp))
            .background(cardBg)
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        content()
    }
}

@Composable
private fun SettingToggleItem(
    title: String,
    subtitle: String,
    checked: Boolean,
    primaryColor: Color,
    mutedColor: Color,
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
                color = primaryColor,
                fontSize = 14.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = mutedColor,
                fontSize = 11.5.sp,
                fontFamily = FontFamily.Serif
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = MedievalGold,
                uncheckedThumbColor = mutedColor,
                uncheckedTrackColor = primaryColor.copy(alpha = 0.15f)
            )
        )
    }
}

@Composable
private fun LanguageGrid(
    languages: List<Pair<String, String>>,
    selectedLanguage: String,
    isDark: Boolean,
    cardBorder: Color,
    primaryText: Color,
    onSelectLanguage: (String) -> Unit
) {
    languages.chunked(3).forEach { rowLanguages ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            rowLanguages.forEach { (code, label) ->
                val isSelected = selectedLanguage == code
                val itemBg = if (isSelected) {
                    if (isDark) Color(0xFF162B40) else Color(0xFF0D2036)
                } else {
                    if (isDark) Color(0xFF0D1825) else Color(0xFFF7F3EA)
                }
                val itemBorder = if (isSelected) MedievalGoldBright else cardBorder
                val itemTextColor = if (isSelected) MedievalGoldBright else primaryText

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = itemBorder,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(itemBg)
                        .clickable { onSelectLanguage(code) }
                        .padding(vertical = 9.dp, horizontal = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = itemTextColor,
                        fontSize = 12.5.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        textAlign = TextAlign.Center
                    )
                }
            }
            repeat(3 - rowLanguages.size) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}
