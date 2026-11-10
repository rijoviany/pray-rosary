package com.trivium.rosarium.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trivium.rosarium.data.model.Prayer
import com.trivium.rosarium.ui.components.PrayerIcons

private val NavyHeader = Color(0xFF0D2036)
private val NavyMedallion = Color(0xFF0F2642)
private val ParchmentBackground = Color(0xFFF7F3EA)
private val CardIvory = Color(0xFFFFFFFF)
private val GoldAccent = Color(0xFFC5A059)
private val GoldBright = Color(0xFFE5BE42)
private val CharcoalText = Color(0xFF1E2836)
private val MutedText = Color(0xFF6B7A8D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrayersSheet(
    prayers: List<Prayer>,
    initialPrayerId: String? = null,
    isDark: Boolean = false,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val bgColor = if (isDark) Color(0xFF09121D) else ParchmentBackground

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = bgColor,
        scrimColor = Color.Black.copy(alpha = 0.75f),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        PrayersScreenContent(
            prayers = prayers,
            initialPrayerId = initialPrayerId,
            isDark = isDark,
            onBack = onDismiss
        )
    }
}

@Composable
fun PrayersScreenContent(
    prayers: List<Prayer>,
    initialPrayerId: String? = null,
    isDark: Boolean = false,
    onBack: () -> Unit
) {
    var selectedPrayer by remember(initialPrayerId) {
        mutableStateOf(prayers.find { it.id == initialPrayerId })
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (selectedPrayer == null) {
            // Screen 2: Prayers & Invocations List
            PrayersListView(
                prayers = prayers,
                isDark = isDark,
                onSelectPrayer = { selectedPrayer = it },
                onClose = onBack
            )
        } else {
            // Screen 3: Prayer Detail View
            PrayerDetailView(
                prayer = selectedPrayer!!,
                isDark = isDark,
                onBack = { selectedPrayer = null }
            )
        }
    }
}

@Composable
private fun PrayersListView(
    prayers: List<Prayer>,
    isDark: Boolean = false,
    onSelectPrayer: (Prayer) -> Unit,
    onClose: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var activeFilter by remember { mutableStateOf("Core Prayers") }

    val bgColor = if (isDark) Color(0xFF09121D) else ParchmentBackground
    val cardBg = if (isDark) Color(0xFF132235) else CardIvory
    val cardBorder = if (isDark) Color(0xFF233B57) else Color(0xFFE2D9CB)
    val titleColor = if (isDark) Color(0xFFF7F3EA) else CharcoalText
    val subColor = if (isDark) Color(0xFF9FB2C8) else MutedText
    val filterBorder = if (isDark) Color(0xFF233B57) else Color(0xFFDDD4C5)
    val iconAccent = if (isDark) GoldBright else GoldAccent

    val corePrayerKeys = setOf(
        "our_father", "hail_mary", "glory_be", "fatima", "hail_holy_queen",
        "eternal_father", "sorrowful_passion", "holy_god"
    )

    val filteredPrayers = prayers.filter { prayer ->
        val matchesSearch = searchQuery.isBlank() ||
                prayer.title.contains(searchQuery, ignoreCase = true) ||
                prayer.fullText.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (activeFilter) {
            "Core Prayers" -> corePrayerKeys.contains(prayer.id)
            "Popular" -> corePrayerKeys.contains(prayer.id) || prayer.id.contains("heart") || prayer.id.contains("blood")
            else -> true
        }

        matchesSearch && matchesFilter
    }

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
            IconButton(onClick = onClose) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }

            Text(
                text = "Prayers & Invocations",
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

        // Search Bar (if active)
        if (isSearchActive) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search prayers, phrases...", color = MutedText, fontSize = 14.sp) },
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

        // 2. Filter Tabs / Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("Core Prayers", "Popular", "All").forEach { filter ->
                val isSelected = activeFilter == filter
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
                        .clickable { activeFilter = filter }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter,
                        color = if (isSelected) Color.White else titleColor,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        fontFamily = FontFamily.Serif
                    )
                }
            }
        }

        // 3. List of Prayer Cards
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredPrayers) { prayer ->
                val iconRes = PrayerIcons.getPrayerIcon(prayer.id)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 2.dp, shape = RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                        .background(cardBg)
                        .border(1.dp, cardBorder, RoundedCornerShape(16.dp))
                        .clickable { onSelectPrayer(prayer) }
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
                            contentDescription = prayer.title,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Prayer Title
                    Text(
                        text = prayer.title,
                        color = titleColor,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )

                    // Right Chevron
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
private fun PrayerDetailView(
    prayer: Prayer,
    isDark: Boolean = false,
    onBack: () -> Unit
) {
    var fontScale by remember { mutableFloatStateOf(1.0f) }
    val iconRes = PrayerIcons.getPrayerIcon(prayer.id)

    val vaultColors = if (isDark) {
        listOf(Color(0xFF132235), Color(0xFF09121D))
    } else {
        listOf(Color(0xFFFFFDF9), Color(0xFFF7F3EA))
    }
    val vaultBorder = if (isDark) Color(0xFF233B57) else Color(0xFFE2D9CB)
    val titleColor = if (isDark) Color(0xFFF7F3EA) else CharcoalText
    val phraseColor = if (isDark) Color(0xFFE5DEC8) else Color(0xFF1E2836)
    val controlsPillBg = if (isDark) Color(0xFF15263A) else Color(0xFFFDFBF7)
    val controlsPillBorder = if (isDark) Color(0xFF284160) else GoldAccent.copy(alpha = 0.5f)
    val iconAccent = if (isDark) GoldBright else GoldAccent

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NavyHeader)
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
                text = prayer.title,
                color = Color.White,
                fontSize = 19.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            )

            IconButton(onClick = {}) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = "Bookmark",
                    tint = Color.White
                )
            }
        }

        // Cathedral Vaulted Body
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .background(
                    Brush.verticalGradient(colors = vaultColors)
                )
                .border(
                    width = 1.dp,
                    color = vaultBorder,
                    shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 22.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Golden Sunburst Medallion
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(Color(0xFFFFFAEB), Color(0xFFFFDE82), Color(0xFFD4AF37), Color(0xFF9E7719))
                            )
                        )
                        .border(2.dp, GoldBright, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = iconRes),
                        contentDescription = prayer.title,
                        modifier = Modifier.size(64.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Prayer Title
                Text(
                    text = prayer.title,
                    color = titleColor,
                    fontSize = (24 * fontScale).sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Prayer Verses / Phrases
                prayer.phrases.forEach { phrase ->
                    Text(
                        text = phrase,
                        color = phraseColor,
                        fontSize = (16.5f * fontScale).sp,
                        fontFamily = FontFamily.Serif,
                        lineHeight = (26 * fontScale).sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                    )
                }

                Spacer(modifier = Modifier.height(84.dp))
            }

            // Floating Bottom Controls Pill (Speaker, Large Play Button, Text Size)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 20.dp)
                    .shadow(8.dp, RoundedCornerShape(32.dp))
                    .clip(RoundedCornerShape(32.dp))
                    .background(controlsPillBg)
                    .border(1.2.dp, controlsPillBorder, RoundedCornerShape(32.dp))
                    .padding(horizontal = 18.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Speaker Button
                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Audio Recitation",
                        tint = CharcoalText,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Centered Golden Play Button
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(GoldBright, GoldAccent, Color(0xFF9E7719))
                            )
                        )
                        .clickable {},
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                // Font Size Button
                Box(
                    modifier = Modifier
                        .clickable {
                            fontScale = when (fontScale) {
                                1.0f -> 1.15f
                                1.15f -> 1.30f
                                else -> 1.0f
                            }
                        }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Aᴬ",
                        color = CharcoalText,
                        fontSize = 17.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
