package com.trivium.rosarium.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
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
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
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
    onNovenaOpened: (String, Int) -> Unit = { _, _ -> },
    onMarkDayComplete: (String, Int) -> Unit,
    onResetNovena: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sheetBg = if (isDark) Color(0xFF09121D) else ParchmentBg

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = sheetBg,
        scrimColor = Color.Black.copy(alpha = 0.75f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        NovenasScreenContent(
            novenas = novenas,
            novenaProgressMap = novenaProgressMap,
            initialNovenaId = initialNovenaId,
            isDark = isDark,
            onNovenaOpened = onNovenaOpened,
            onMarkDayComplete = onMarkDayComplete,
            onResetNovena = onResetNovena,
            onBack = onDismiss
        )
    }
}

@Composable
fun NovenasScreenContent(
    novenas: List<Novena>,
    novenaProgressMap: Map<String, NovenaProgress>,
    initialNovenaId: String? = null,
    isDark: Boolean = false,
    onNovenaOpened: (String, Int) -> Unit = { _, _ -> },
    onMarkDayComplete: (String, Int) -> Unit,
    onResetNovena: (String) -> Unit,
    onBack: () -> Unit
) {
    var selectedNovenaId by remember(initialNovenaId) {
        mutableStateOf(initialNovenaId)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (selectedNovenaId == null) {
            // Screen 5: 9-Day Novenas Menu List
            NovenasListView(
                novenas = novenas,
                novenaProgressMap = novenaProgressMap,
                isDark = isDark,
                onSelectNovena = { selectedNovenaId = it.id },
                onClose = onBack
            )
        } else {
            val currentNovena = novenas.find { it.id == selectedNovenaId } ?: novenas.first()
            NovenaDetailView(
                novena = currentNovena,
                progress = novenaProgressMap[currentNovena.id] ?: NovenaProgress(novenaId = currentNovena.id, currentDay = 1),
                isDark = isDark,
                onNovenaOpened = { day -> onNovenaOpened(currentNovena.id, day) },
                onMarkDayComplete = { day -> onMarkDayComplete(currentNovena.id, day) },
                onResetNovena = { onResetNovena(currentNovena.id) },
                onBack = { selectedNovenaId = null }
            )
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
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
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
    onNovenaOpened: (Int) -> Unit = {},
    onMarkDayComplete: (Int) -> Unit,
    onResetNovena: () -> Unit,
    onBack: () -> Unit
) {
    val iconRes = PrayerIcons.getNovenaIcon(novena.id)
    var inspectedDayNumber by remember(novena.id) {
        mutableIntStateOf(progress.currentDay.coerceIn(1, 9))
    }

    LaunchedEffect(novena.id, inspectedDayNumber) {
        onNovenaOpened(inspectedDayNumber)
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
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Summary Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(cardBg)
                    .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(NavyMedallion)
                            .border(1.5.dp, iconAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = novena.title,
                            modifier = Modifier.size(32.dp)
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

            // 1. 9-Votive Vigil Candle Altar
            NovenaCandleAltar(
                inspectedDay = inspectedDayNumber,
                completedDays = progress.completedDays,
                isDark = isDark,
                onSelectDay = { inspectedDayNumber = it }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Day Theme & Scripture
            Text(
                text = "Day $inspectedDayNumber: ${inspectedDay.theme}",
                color = if (isDark) Color(0xFFF7F3EA) else NavyHeader,
                fontSize = 18.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )

            if (inspectedDay.scriptureText.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "\"${inspectedDay.scriptureText}\" — ${inspectedDay.scriptureReference}",
                    color = subColor,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Serif,
                    fontStyle = FontStyle.Italic
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Intention & Meditation Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(1.dp, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) Color(0xFF101B2B) else Color(0xFFFCF9F3))
                    .border(1.dp, if (isDark) Color(0xFF233B57) else Color(0xFFE5DEC8), RoundedCornerShape(12.dp))
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
                        color = titleColor,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold
                    )
                    if (inspectedDay.meditation.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = inspectedDay.meditation,
                            color = if (isDark) Color(0xFFD3E0EA) else CharcoalText,
                            fontSize = 13.5.sp,
                            fontFamily = FontFamily.Serif,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 4. Perforated Devotional Tear Card (Tactile Paper Tear-Off Mechanic)
            PerforatedNovenaTearCard(
                dayNumber = inspectedDayNumber,
                prayerLines = inspectedDay.prayer,
                isCompleted = isInspectedDayCompleted,
                isDark = isDark,
                onTearComplete = {
                    onMarkDayComplete(inspectedDayNumber)
                    if (inspectedDayNumber < 9) {
                        inspectedDayNumber += 1
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

/**
 * 9-Votive Vigil Candle Altar sitting on an oak/mahogany communion rail.
 * Completed days burn with a living, gentle flickering golden flame.
 */
@Composable
private fun NovenaCandleAltar(
    inspectedDay: Int,
    completedDays: List<Int>,
    isDark: Boolean,
    onSelectDay: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val altarBg = if (isDark) Color(0xFF101B2B) else Color(0xFF281C15)
    val altarBorder = if (isDark) Color(0xFF233B57) else Color(0xFF4A3424)
    val shelfWood = if (isDark) Color(0xFF16253B) else Color(0xFF3B281B)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(altarBg)
            .border(1.dp, altarBorder, RoundedCornerShape(16.dp))
            .padding(top = 10.dp, bottom = 8.dp, start = 6.dp, end = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "✟ 9-DAY VIGIL CANDLE ALTAR ✟",
            color = Color(0xFFE5BE42),
            fontSize = 11.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 9 Candles
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            (1..9).forEach { dayNum ->
                val isLit = completedDays.contains(dayNum)
                val isInspected = dayNum == inspectedDay
                VigilCandleItem(
                    dayNumber = dayNum,
                    isLit = isLit,
                    isInspected = isInspected,
                    isDark = isDark,
                    onClick = { onSelectDay(dayNum) }
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Wooden Altar Shelf
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp))
                .background(shelfWood)
                .border(0.5.dp, Color(0xFFD4AF37).copy(alpha = 0.35f), RoundedCornerShape(3.dp))
        )
    }
}

@Composable
private fun VigilCandleItem(
    dayNumber: Int,
    isLit: Boolean,
    isInspected: Boolean,
    isDark: Boolean,
    onClick: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "CandleFlicker")
    val flameScaleY by infiniteTransition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.14f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 320, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FlameScaleY"
    )
    val flameAlpha by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 260, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "FlameAlpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 2.dp, vertical = 2.dp)
    ) {
        // Living Flame (visible only when lit)
        Box(
            modifier = Modifier
                .height(18.dp)
                .width(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            if (isLit) {
                Box(
                    modifier = Modifier
                        .size(13.dp, 16.dp)
                        .graphicsLayer(
                            scaleY = flameScaleY,
                            scaleX = 0.95f + (flameScaleY - 0.88f) * 0.4f,
                            alpha = flameAlpha
                        )
                        .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 4.dp, bottomEnd = 4.dp))
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFFFFF9C4), // White-hot core
                                    Color(0xFFFFB300), // Golden amber
                                    Color(0xFFFF6D00)  // Deep orange base
                                )
                            )
                        )
                )
            } else {
                // Black unlit wick
                Box(
                    modifier = Modifier
                        .width(1.5.dp)
                        .height(6.dp)
                        .background(Color(0xFF5A5A5A))
                )
            }
        }

        // Glass Votive Cup
        Box(
            modifier = Modifier
                .width(28.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(bottomStart = 5.dp, bottomEnd = 5.dp, topStart = 2.dp, topEnd = 2.dp))
                .background(
                    if (isLit) {
                        Brush.verticalGradient(
                            colors = listOf(Color(0x99FFD54F), Color(0xBBFFA000))
                        )
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(Color(0x33FFFFFF), Color(0x1AFFFFFF))
                        )
                    }
                )
                .border(
                    width = if (isInspected) 1.5.dp else 1.dp,
                    color = if (isInspected) Color(0xFFE5BE42) else Color(0xFF8D6E63).copy(alpha = 0.5f),
                    shape = RoundedCornerShape(bottomStart = 5.dp, bottomEnd = 5.dp, topStart = 2.dp, topEnd = 2.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "$dayNumber",
                color = if (isLit) Color(0xFF3E2723) else Color(0xFFD7CCC8),
                fontSize = 12.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        // Inspected indicator golden dot
        Box(
            modifier = Modifier
                .size(4.dp)
                .clip(CircleShape)
                .background(if (isInspected) Color(0xFFE5BE42) else Color.Transparent)
        )
    }
}

/**
 * The Perforated Devotional Tear Card:
 * Renders the daily novena prayer with a physical tear-away stub at the bottom.
 * Dragging downward rips the perforated paper with resistance, angle tilt, and micro-haptic ticks.
 */
@Composable
private fun PerforatedNovenaTearCard(
    dayNumber: Int,
    prayerLines: List<String>,
    isCompleted: Boolean,
    isDark: Boolean,
    onTearComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    val tearThresholdPx = with(density) { 110.dp.toPx() }
    val highlightThresholdPx = with(density) { 20.dp.toPx() }
    val tickStepPx = with(density) { 18.dp.toPx() }
    val dropTargetPx = with(density) { 240.dp.toPx() }

    val dragOffsetAnim = remember { Animatable(0f) }
    var isTearing by remember { mutableStateOf(false) }
    var lastHapticStep by remember { mutableIntStateOf(0) }

    val cardBg = if (isDark) Color(0xFF132235) else CardIvory
    val titleColor = if (isDark) Color(0xFFF7F3EA) else CharcoalText

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(cardBg)
            .border(1.dp, if (isDark) Color(0xFF233B57) else Color(0xFFE2D9CB), RoundedCornerShape(16.dp))
    ) {
        // Upper Prayer Text Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DAY $dayNumber NOVENA PRAYER",
                    color = GoldAccent,
                    fontSize = 11.5.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.6.sp
                )

                if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isDark) Color(0xFF1A3826) else Color(0xFFE8F5E9))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "SEALED",
                            color = Color(0xFF4CAF50),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            prayerLines.forEach { line ->
                Text(
                    text = line,
                    color = titleColor,
                    fontSize = 14.5.sp,
                    fontFamily = FontFamily.Serif,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }

        // Perforated Tear Line or Completed Wax Seal
        if (isCompleted) {
            // Stamped Wax Seal & Imprimatur
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDark) Color(0xFF0F1A28) else Color(0xFFFBF8F0))
                    .padding(vertical = 16.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Episcopal Wax Seal Medallion
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Color(0xFF8B1E1E), Color(0xFF5B0E0E))
                                )
                            )
                            .border(2.dp, Color(0xFFD4AF37), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✟",
                            color = Color(0xFFFFD54F),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "✦ DAY $dayNumber DEVOTION COMPLETED ✦",
                        color = if (isDark) Color(0xFF81C784) else Color(0xFF2E7D32),
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Votive candle burning on the holy altar • Deo Gratias",
                        color = if (isDark) Color(0xFF9FB2C8) else MutedText,
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Serif
                    )
                }
            }
        } else {
            // Perforated Dashed Line Divider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "- - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -",
                    color = GoldAccent.copy(alpha = 0.6f),
                    fontSize = 11.sp,
                    letterSpacing = 1.sp,
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "✂",
                    color = GoldAccent,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }

            // Tear Tab with Downward Drag Mechanics
            val currentOffset = dragOffsetAnim.value
            val tearRotation = (currentOffset / 20f).coerceIn(-4f, 4f)

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(0, currentOffset.roundToInt()) }
                    .rotate(tearRotation)
                    .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    .background(
                        if (isDark) {
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF1E334D), Color(0xFF142438))
                            )
                        } else {
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFFF3ECE0), Color(0xFFE9DEC9))
                            )
                        }
                    )
                    .border(
                        1.dp,
                        if (currentOffset > highlightThresholdPx) GoldBright else GoldAccent.copy(alpha = 0.5f),
                        RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
                    )
                    .pointerInput(dayNumber) {
                        detectVerticalDragGestures(
                            onDragStart = {
                                isTearing = true
                                lastHapticStep = 0
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                            onDragEnd = {
                                isTearing = false
                                if (dragOffsetAnim.value >= tearThresholdPx) {
                                    coroutineScope.launch {
                                        // Tear sheet flutter drop away
                                        dragOffsetAnim.animateTo(dropTargetPx, tween(200))
                                        onTearComplete()
                                        dragOffsetAnim.snapTo(0f)
                                    }
                                } else {
                                    coroutineScope.launch {
                                        dragOffsetAnim.animateTo(0f, spring(dampingRatio = 0.65f))
                                    }
                                }
                            },
                            onDragCancel = {
                                isTearing = false
                                coroutineScope.launch {
                                    dragOffsetAnim.animateTo(0f, spring(dampingRatio = 0.65f))
                                }
                            },
                            onVerticalDrag = { change, dragAmount ->
                                change.consume()
                                if (dragAmount > 0 || dragOffsetAnim.value > 0) {
                                    val newOffset = (dragOffsetAnim.value + dragAmount * 0.75f).coerceAtLeast(0f)
                                    coroutineScope.launch {
                                        dragOffsetAnim.snapTo(newOffset)
                                    }

                                    // Micro haptic ticks simulating tearing paper fibers
                                    val step = (newOffset / tickStepPx).toInt()
                                    if (step != lastHapticStep) {
                                        lastHapticStep = step
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                }
                            }
                        )
                    }
                    .padding(vertical = 14.dp, horizontal = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "✟",
                            color = GoldAccent,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "DRAG DOWN TO TEAR DAY $dayNumber",
                            color = if (isDark) Color.White else NavyHeader,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.KeyboardArrowDown,
                            contentDescription = "Pull Down",
                            tint = GoldAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "Rip page upon concluding devotion to light Day $dayNumber candle",
                        color = if (isDark) Color(0xFF9FB2C8) else MutedText,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Serif
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Accessible tap alternative
                    Text(
                        text = "or tap here to mark complete",
                        color = GoldAccent,
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.clickable { onTearComplete() }
                    )
                }
            }
        }
    }
}
