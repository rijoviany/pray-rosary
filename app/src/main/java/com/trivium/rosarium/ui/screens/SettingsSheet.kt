package com.trivium.rosarium.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandMore
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trivium.rosarium.R
import com.trivium.rosarium.data.model.MysteryCatalog
import com.trivium.rosarium.data.model.MysterySet
import com.trivium.rosarium.data.model.Novena
import com.trivium.rosarium.data.model.NovenaProgress
import com.trivium.rosarium.data.model.SettingsState
import com.trivium.rosarium.ui.components.PrayerIcons
import com.trivium.rosarium.ui.theme.BackgroundSurface
import com.trivium.rosarium.ui.theme.MedievalGold
import com.trivium.rosarium.ui.theme.MedievalGoldBright
import com.trivium.rosarium.ui.theme.MedievalGoldDark
import com.trivium.rosarium.ui.theme.OxbloodRed
import com.trivium.rosarium.ui.theme.ParchmentBase
import com.trivium.rosarium.ui.theme.ParchmentDark
import com.trivium.rosarium.ui.theme.ParchmentLight
import com.trivium.rosarium.ui.theme.UltramarineLapis

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    settings: SettingsState,
    catalog: MysteryCatalog,
    currentMysterySetId: String,
    onSelectMysterySet: (MysterySet) -> Unit,
    allNovenas: List<Novena>,
    novenaProgressMap: Map<String, NovenaProgress>,
    onSelectNovena: (Novena) -> Unit,
    onOpenReadIntentions: () -> Unit,
    onOpenProgress: () -> Unit,
    onOpenPrayers: () -> Unit = {},
    currentBeadCount: Int = 0,
    totalBeadCount: Int = 53,
    onUpdateSettings: (SettingsState) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Menu list expansion states: Rosaries & Novenas open by default as a visible list
    var isRosariesExpanded by remember { mutableStateOf(true) }
    var isNovenasExpanded by remember { mutableStateOf(true) }
    var isSettingsExpanded by remember { mutableStateOf(false) }

    val activeMysterySet = catalog.mysterySets.find { it.id == currentMysterySetId }

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
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Rosarium Menu",
                color = MedievalGoldBright,
                fontSize = 22.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Select devotions, 9-day novenas, scripture & progress",
                color = ParchmentDark,
                fontSize = 12.sp,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // ==========================================
            // 0. PRAYERS & INVOCATIONS
            // ==========================================
            DirectNavigationRow(
                title = "Prayers & Invocations",
                subtitle = "Our Father, Hail Mary, Litany & Sacred Invocations",
                badgeText = "16 Prayers",
                icon = {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0F2642)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_our_father),
                            contentDescription = "Prayers",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                onClick = {
                    onOpenPrayers()
                    onDismiss()
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // ==========================================
            // 1. ROSARIES & CHAPLETS (LIST)
            // ==========================================
            val chaplets = catalog.mysterySets.filter { it.isChaplet }
            val marian = catalog.mysterySets.filter { !it.isChaplet }
            val chapletPriority = listOf(
                "divine_mercy", "saint_michael", "precious_blood", "holy_wounds",
                "saint_joseph", "seven_sorrows", "sacred_heart", "franciscan_crown",
                "holy_souls", "saint_anne"
            )
            val sortedChaplets = chaplets.sortedBy { set ->
                val idx = chapletPriority.indexOf(set.id)
                if (idx != -1) idx else 999
            }

            ExpandableMenuHeader(
                title = "Rosaries & Chaplets",
                subtitle = "Divine Mercy, St. Michael, Holy Rosary...",
                badgeText = activeMysterySet?.name?.take(16) ?: "${catalog.mysterySets.size} Devotions",
                isExpanded = isRosariesExpanded,
                onToggle = { isRosariesExpanded = !isRosariesExpanded },
                icon = {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .clip(CircleShape)
                            .background(activeMysterySet?.primaryBeadColor ?: MedievalGold)
                            .border(1.2.dp, activeMysterySet?.specularBeadColor ?: MedievalGoldBright, CircleShape)
                    )
                }
            )

            AnimatedVisibility(
                visible = isRosariesExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 2.dp, top = 6.dp, bottom = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "APPROVED CATHOLIC CHAPLETS",
                        color = MedievalGold,
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 2.dp)
                    )

                    sortedChaplets.forEach { set ->
                        SubMenuItem(
                            title = set.name,
                            subtitle = "${set.effectiveBeadsPerDecade} beads per group",
                            badgeText = "${set.effectiveBeadsPerDecade} Beads",
                            dotColor = set.primaryBeadColor,
                            dotHighlight = set.specularBeadColor,
                            iconRes = PrayerIcons.getMysterySetIcon(set.id),
                            isSelected = set.id == currentMysterySetId,
                            onClick = {
                                onSelectMysterySet(set)
                                onDismiss()
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "THE HOLY ROSARY (BLESSED VIRGIN MARY)",
                        color = MedievalGold,
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp, bottom = 2.dp)
                    )

                    marian.forEach { set ->
                        SubMenuItem(
                            title = set.name,
                            subtitle = set.recommendedDays.joinToString(", "),
                            badgeText = "10 Beads",
                            dotColor = set.primaryBeadColor,
                            dotHighlight = set.specularBeadColor,
                            iconRes = PrayerIcons.getMysterySetIcon(set.id),
                            isSelected = set.id == currentMysterySetId,
                            onClick = {
                                onSelectMysterySet(set)
                                onDismiss()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ==========================================
            // 2. NOVENAS (LIST)
            // ==========================================
            val activeNovenasCount = novenaProgressMap.count { it.value.completedDays.isNotEmpty() && !it.value.isCompleted }
            val novenasBadge = if (activeNovenasCount > 0) "$activeNovenasCount Active" else "${allNovenas.size} Novenas"

            ExpandableMenuHeader(
                title = "9-Day Catholic Novenas",
                subtitle = "Holy Spirit, St. Anthony, Divine Mercy...",
                badgeText = novenasBadge,
                isExpanded = isNovenasExpanded,
                onToggle = { isNovenasExpanded = !isNovenasExpanded },
                icon = {
                    Canvas(modifier = Modifier.size(18.dp)) {
                        val w = size.width
                        val h = size.height
                        // Radiant Cross
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
                }
            )

            AnimatedVisibility(
                visible = isNovenasExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 2.dp, top = 6.dp, bottom = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    allNovenas.forEach { novena ->
                        val progress = novenaProgressMap[novena.id]
                        val isDone = progress?.isCompleted == true
                        val badge = when {
                            isDone -> "Complete"
                            progress != null && progress.completedDays.isNotEmpty() -> "Day ${progress.currentDay}/9"
                            else -> "9 Days"
                        }

                        SubMenuItem(
                            title = novena.title.removePrefix("Novena to ").removePrefix("Novena of the "),
                            subtitle = novena.subtitle,
                            badgeText = badge,
                            dotColor = novena.themeColor,
                            dotHighlight = MedievalGoldBright,
                            iconRes = PrayerIcons.getNovenaIcon(novena.id),
                            isSelected = false,
                            isDone = isDone,
                            onClick = {
                                onSelectNovena(novena)
                                onDismiss()
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // ==========================================
            // 3. INTENTIONS >
            // ==========================================
            DirectNavigationRow(
                title = "Read Intentions & Scripture",
                subtitle = "Active mystery scripture passage & fruit",
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

            Spacer(modifier = Modifier.height(10.dp))

            // ==========================================
            // 4. PROGRESS >
            // ==========================================
            DirectNavigationRow(
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

            Spacer(modifier = Modifier.height(14.dp))

            // ==========================================
            // 5. SETTINGS & AUDIO PREFERENCES (EXPANDABLE)
            // ==========================================
            ExpandableMenuHeader(
                title = "Settings & Audio Preferences",
                subtitle = "Language, audio speed, chant, sound & haptics",
                badgeText = settings.language.uppercase(),
                isExpanded = isSettingsExpanded,
                onToggle = { isSettingsExpanded = !isSettingsExpanded },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = null,
                        tint = MedievalGoldBright,
                        modifier = Modifier.size(20.dp)
                    )
                }
            )

            AnimatedVisibility(
                visible = isSettingsExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 4.dp, end = 2.dp, top = 10.dp, bottom = 10.dp)
                ) {

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
                                textAlign = TextAlign.Center
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
        }
    }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

/**
 * Expandable Accordion Header Row (e.g. "rosaries >", "novenas >")
 */
@Composable
private fun ExpandableMenuHeader(
    title: String,
    subtitle: String,
    badgeText: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    icon: @Composable () -> Unit
) {
    val rotation by animateFloatAsState(targetValue = if (isExpanded) 90f else 0f, label = "arrowRotation")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isExpanded) 1.5.dp else 1.dp,
                color = if (isExpanded) MedievalGoldBright else MedievalGold.copy(alpha = 0.45f),
                shape = RoundedCornerShape(12.dp)
            )
            .background(if (isExpanded) Color(0xFF241B14) else Color(0xFF1C1510))
            .clickable(onClick = onToggle)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFF100B07))
                .border(1.dp, MedievalGold.copy(alpha = 0.7f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            icon()
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (isExpanded) MedievalGoldBright else ParchmentLight,
                fontSize = 15.sp,
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
                .background(Color(0xFF2C2016))
                .border(1.dp, MedievalGoldDark.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = badgeText,
                color = MedievalGoldBright,
                fontSize = 10.5.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = if (isExpanded) "Collapse" else "Expand",
            tint = MedievalGoldBright,
            modifier = Modifier
                .size(20.dp)
                .rotate(rotation)
        )
    }
}

/**
 * Direct Navigation Row (e.g. "intentions >", "progress >")
 */
@Composable
private fun DirectNavigationRow(
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
            .background(Color(0xFF1C1510))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(Color(0xFF100B07))
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
                fontSize = 15.sp,
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
                .background(Color(0xFF2C2016))
                .border(1.dp, MedievalGoldDark.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = badgeText,
                color = MedievalGoldBright,
                fontSize = 10.5.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = "Open",
            tint = MedievalGoldBright,
            modifier = Modifier.size(20.dp)
        )
    }
}

/**
 * Sub-Menu Item for inside expanded Rosaries or Novenas list
 */
@Composable
private fun SubMenuItem(
    title: String,
    subtitle: String,
    badgeText: String,
    dotColor: Color,
    dotHighlight: Color,
    iconRes: Int? = null,
    isSelected: Boolean = false,
    isDone: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 52.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 1.2.dp else 0.8.dp,
                color = if (isSelected) MedievalGoldBright else MedievalGoldDark.copy(alpha = 0.35f),
                shape = RoundedCornerShape(12.dp)
            )
            .background(if (isSelected) Color(0xFF2B2016) else Color(0xFF1B140F))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconRes != null) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F2642))
                    .border(1.2.dp, MedievalGoldBright.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = iconRes),
                    contentDescription = title,
                    modifier = Modifier.size(22.dp)
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(dotColor)
                    .border(1.2.dp, dotHighlight, CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = if (isSelected) MedievalGoldBright else ParchmentLight,
                fontSize = 14.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
            Text(
                text = subtitle,
                color = ParchmentDark,
                fontSize = 11.sp,
                fontFamily = FontFamily.Serif
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        if (isDone) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Completed",
                tint = MedievalGoldBright,
                modifier = Modifier.size(16.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(5.dp))
                    .background(Color(0xFF241A12))
                    .border(0.8.dp, MedievalGoldDark.copy(alpha = 0.4f), RoundedCornerShape(5.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badgeText,
                    color = MedievalGoldBright,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (isSelected) {
            Spacer(modifier = Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = MedievalGoldBright,
                modifier = Modifier.size(16.dp)
            )
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
