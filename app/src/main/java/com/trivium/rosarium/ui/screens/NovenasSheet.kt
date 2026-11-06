package com.trivium.rosarium.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trivium.rosarium.data.model.Novena
import com.trivium.rosarium.data.model.NovenaProgress
import com.trivium.rosarium.ui.components.PrayerIcons

private val NavyHeader = Color(0xFF0D2036)
private val NavyMedallion = Color(0xFF0F2642)
private val ParchmentBg = Color(0xFFF7F3EA)
private val CardIvory = Color(0xFFFFFFFF)
private val GoldAccent = Color(0xFFC5A059)
private val GoldBright = Color(0xFFE5BE42)
private val CharcoalText = Color(0xFF1E2836)
private val MutedText = Color(0xFF6B7A8D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NovenasSheet(
    novenas: List<Novena>,
    novenaProgressMap: Map<String, NovenaProgress>,
    initialNovenaId: String? = null,
    isDark: Boolean = false,
    onMarkDayComplete: (String, Int) -> Unit,
    onResetNovena: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedNovenaId by remember(initialNovenaId) {
        mutableStateOf(initialNovenaId)
    }
    val sheetBg = if (isDark) Color(0xFF09121D) else ParchmentBg

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = sheetBg,
        scrimColor = Color.Black.copy(alpha = 0.75f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (selectedNovenaId == null) {
                // Screen 5: 9-Day Novenas Menu List
                NovenasListView(
                    novenas = novenas,
                    novenaProgressMap = novenaProgressMap,
                    isDark = isDark,
                    onSelectNovena = { selectedNovenaId = it.id },
                    onClose = onDismiss
                )
            } else {
                val currentNovena = novenas.find { it.id == selectedNovenaId } ?: novenas.first()
                NovenaDetailView(
                    novena = currentNovena,
                    progress = novenaProgressMap[currentNovena.id] ?: NovenaProgress(novenaId = currentNovena.id, currentDay = 1),
                    isDark = isDark,
                    onMarkDayComplete = { day -> onMarkDayComplete(currentNovena.id, day) },
                    onResetNovena = { onResetNovena(currentNovena.id) },
                    onBack = { selectedNovenaId = null }
                )
            }
        }
    }
}

@Composable
private fun NovenasListView(
    novenas: List<Novena>,
    novenaProgressMap: Map<String, NovenaProgress>,
    isDark: Boolean = false,
    onSelectNovena: (Novena) -> Unit,
    onClose: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val bgColor = if (isDark) Color(0xFF09121D) else ParchmentBg
    val cardBg = if (isDark) Color(0xFF132235) else CardIvory
    val cardBorder = if (isDark) Color(0xFF233B57) else Color(0xFFE2D9CB)
    val titleColor = if (isDark) Color(0xFFF7F3EA) else CharcoalText
    val subColor = if (isDark) Color(0xFF9FB2C8) else MutedText
    val iconAccent = if (isDark) GoldBright else GoldAccent

    val filteredNovenas = novenas.filter { novena ->
        searchQuery.isBlank() ||
                novena.title.contains(searchQuery, ignoreCase = true) ||
                novena.subtitle.contains(searchQuery, ignoreCase = true) ||
                novena.description.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ParchmentBg)
    ) {
        // Deep Navy Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavyHeader)
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "9-Day Novenas",
                color = Color.White,
                fontSize = 19.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )

            IconButton(onClick = { isSearchActive = !isSearchActive }) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.White
                )
            }
        }

        if (isSearchActive) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search novenas...", color = MutedText, fontSize = 14.sp) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = CardIvory,
                    unfocusedContainerColor = CardIvory,
                    focusedBorderColor = GoldAccent,
                    unfocusedBorderColor = Color(0xFFDDD4C5)
                )
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // List of Novenas Cards with Circular Navy Medallions
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredNovenas) { novena ->
                val iconRes = PrayerIcons.getNovenaIcon(novena.id)
                val progress = novenaProgressMap[novena.id]
                val isDone = progress?.isCompleted == true
                val badgeText = when {
                    isDone -> "Complete"
                    progress != null && progress.completedDays.isNotEmpty() -> "Day ${progress.currentDay}/9"
                    else -> "9 Days"
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(cardBg)
                        .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
                        .clickable { onSelectNovena(novena) }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Deep Navy Circular Medallion with Custom Icon
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(NavyMedallion)
                            .border(1.2.dp, iconAccent.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = novena.title,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = novena.title,
                            color = titleColor,
                            fontSize = 15.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = novena.subtitle,
                            color = subColor,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Serif
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isDone) (if (isDark) Color(0xFF1B382B) else Color(0xFFE8F5E9)) else (if (isDark) Color(0xFF142438) else Color(0xFFF9F5EC)))
                            .border(
                                1.dp,
                                if (isDone) Color(0xFF81C784) else iconAccent.copy(alpha = 0.4f),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = badgeText,
                            color = if (isDone) Color(0xFF81C784) else iconAccent,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open",
                        tint = iconAccent,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun NovenaDetailView(
    novena: Novena,
    progress: NovenaProgress,
    isDark: Boolean = false,
    onMarkDayComplete: (Int) -> Unit,
    onResetNovena: () -> Unit,
    onBack: () -> Unit
) {
    val iconRes = PrayerIcons.getNovenaIcon(novena.id)
    var inspectedDayNumber by remember(novena.id) {
        mutableIntStateOf(progress.currentDay.coerceIn(1, 9))
    }
    val inspectedDay = novena.days.getOrNull(inspectedDayNumber - 1) ?: novena.days.first()
    val isInspectedDayCompleted = progress.isDayCompleted(inspectedDayNumber)

    val bodyBg = if (isDark) Color(0xFF09121D) else ParchmentBg
    val cardBg = if (isDark) Color(0xFF132235) else CardIvory
    val cardBorder = if (isDark) Color(0xFF233B57) else Color(0xFFE2D9CB)
    val titleColor = if (isDark) Color(0xFFF7F3EA) else CharcoalText
    val subColor = if (isDark) Color(0xFF9FB2C8) else MutedText
    val iconAccent = if (isDark) GoldBright else GoldAccent

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bodyBg)
    ) {
        // Deep Navy Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavyHeader)
                .padding(horizontal = 8.dp, vertical = 12.dp),
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
                text = novena.title,
                color = Color.White,
                fontSize = 18.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )

            IconButton(onClick = onResetNovena) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset Novena",
                    tint = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 14.dp)
        ) {
            // Summary Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(cardBg)
                    .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(NavyMedallion)
                            .border(1.5.dp, iconAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = novena.title,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = novena.subtitle,
                            color = titleColor,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = novena.feastDay,
                            color = GoldAccent,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Serif
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 9 Days Timeline Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                (1..9).forEach { dayNum ->
                    val isDone = progress.isDayCompleted(dayNum)
                    val isInspected = dayNum == inspectedDayNumber

                    val boxColor = when {
                        isInspected -> NavyHeader
                        isDone -> Color(0xFF2E7D32)
                        else -> CardIvory
                    }

                    val textColor = when {
                        isInspected -> Color.White
                        isDone -> Color.White
                        else -> CharcoalText
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(boxColor)
                            .border(
                                1.dp,
                                if (isInspected) GoldAccent else Color(0xFFE2D9CB),
                                CircleShape
                            )
                            .clickable { inspectedDayNumber = dayNum },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isDone && !isInspected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        } else {
                            Text(
                                text = "$dayNum",
                                color = textColor,
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Day Title & Theme
            Text(
                text = "Day $inspectedDayNumber: ${inspectedDay.theme}",
                color = NavyHeader,
                fontSize = 18.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )

            if (inspectedDay.scriptureText.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "\"${inspectedDay.scriptureText}\" — ${inspectedDay.scriptureReference}",
                    color = MutedText,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Intention & Meditation Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFFCF9F3))
                    .border(1.dp, Color(0xFFE5DEC8), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Text(
                        text = "INTENTION & MEDITATION",
                        color = GoldAccent,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = inspectedDay.intention,
                        color = CharcoalText,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (inspectedDay.meditation.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = inspectedDay.meditation,
                            color = CharcoalText,
                            fontSize = 13.5.sp,
                            fontFamily = FontFamily.Serif,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Daily Novena Prayer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(14.dp))
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardIvory)
                    .border(1.dp, Color(0xFFE2D9CB), RoundedCornerShape(14.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Text(
                        text = "DAY $inspectedDayNumber NOVENA PRAYER",
                        color = GoldAccent,
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.6.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    inspectedDay.prayer.forEach { line ->
                        Text(
                            text = line,
                            color = CharcoalText,
                            fontSize = 14.5.sp,
                            fontFamily = FontFamily.Serif,
                            lineHeight = 22.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Mark Day Complete Button
            ElevatedButton(
                onClick = { onMarkDayComplete(inspectedDayNumber) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.elevatedButtonColors(
                    containerColor = if (isInspectedDayCompleted) Color(0xFF2E7D32) else NavyHeader,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    imageVector = if (isInspectedDayCompleted) Icons.Default.CheckCircle else Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isInspectedDayCompleted) "Day $inspectedDayNumber Completed" else "Mark Day $inspectedDayNumber Complete",
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
