package com.trivium.rosarium.ui.components

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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trivium.rosarium.data.model.Prayer

private val NavyHeader = Color(0xFF0D2036)
private val ParchmentBg = Color(0xFFF7F3EA)
private val CardIvory = Color(0xFFFFFFFF)
private val GoldAccent = Color(0xFFC5A059)
private val GoldBright = Color(0xFFE5BE42)
private val CharcoalText = Color(0xFF1E2836)
private val MutedText = Color(0xFF6B7A8D)

@Composable
fun QuickAccessDrawerContent(
    prayers: List<Prayer>,
    onSelectPrayer: (Prayer) -> Unit,
    onClose: () -> Unit,
    isDark: Boolean = false,
    onToggleDarkMode: ((Boolean) -> Unit)? = null,
    onOpenRosary: (() -> Unit)? = null,
    onOpenNovenas: (() -> Unit)? = null,
    onOpenProgress: (() -> Unit)? = null,
    onOpenSettings: (() -> Unit)? = null
) {
    ModalDrawerSheet(
        drawerContainerColor = if (isDark) Color(0xFF0D1926) else ParchmentBg,
        drawerShape = RoundedCornerShape(topEnd = 20.dp, bottomEnd = 20.dp),
        modifier = Modifier.width(320.dp)
    ) {
        QuickAccessPrayersContent(
            prayers = prayers,
            onSelectPrayer = onSelectPrayer,
            onClose = onClose,
            isDark = isDark,
            onToggleDarkMode = onToggleDarkMode,
            onOpenRosary = onOpenRosary,
            onOpenNovenas = onOpenNovenas,
            onOpenProgress = onOpenProgress,
            onOpenSettings = onOpenSettings
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickAccessPrayersSheet(
    prayers: List<Prayer>,
    onSelectPrayer: (Prayer) -> Unit,
    onDismiss: () -> Unit,
    isDark: Boolean = false,
    onToggleDarkMode: ((Boolean) -> Unit)? = null
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val bgColor = if (isDark) Color(0xFF0D1926) else ParchmentBg

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = bgColor,
        dragHandle = null,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        QuickAccessPrayersContent(
            prayers = prayers,
            onSelectPrayer = onSelectPrayer,
            onClose = onDismiss,
            isDark = isDark,
            onToggleDarkMode = onToggleDarkMode
        )
    }
}

@Composable
fun QuickAccessPrayersContent(
    prayers: List<Prayer>,
    onSelectPrayer: (Prayer) -> Unit,
    onClose: () -> Unit,
    isDark: Boolean = false,
    onToggleDarkMode: ((Boolean) -> Unit)? = null,
    onOpenRosary: (() -> Unit)? = null,
    onOpenNovenas: (() -> Unit)? = null,
    onOpenProgress: (() -> Unit)? = null,
    onOpenSettings: (() -> Unit)? = null
) {
    var searchQuery by remember { mutableStateOf("") }
    var activeCategory by remember { mutableStateOf("All") }

    val bgColor = if (isDark) Color(0xFF0D1926) else ParchmentBg
    val cardColor = if (isDark) Color(0xFF142438) else CardIvory
    val cardBorder = if (isDark) Color(0xFF1F354D) else Color(0xFFE2D9CB)
    val textColor = if (isDark) Color(0xFFF7F3EA) else CharcoalText
    val subTextColor = if (isDark) Color(0xFF9EB0C4) else MutedText
    val iconAccent = if (isDark) GoldBright else GoldAccent

    val coreIds = setOf("our_father", "hail_mary", "glory_be", "fatima", "hail_holy_queen", "apostles_creed")
    val marianIds = setOf("hail_mary", "hail_holy_queen", "fatima", "memorare", "angelus", "regina_caeli", "magnificat")
    val devotionIds = setOf("eternal_father", "holy_god", "sorrowful_passion", "michael", "contrition", "sacred_heart")

    val filtered = prayers.filter { p ->
        val matchesSearch = searchQuery.isBlank() ||
                p.title.contains(searchQuery, ignoreCase = true) ||
                p.fullText.contains(searchQuery, ignoreCase = true)

        val matchesCategory = when (activeCategory) {
            "Core" -> coreIds.any { p.id.contains(it) }
            "Marian" -> marianIds.any { p.id.contains(it) }
            "Devotions" -> devotionIds.any { p.id.contains(it) }
            else -> true
        }

        matchesSearch && matchesCategory
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(NavyHeader)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "✟",
                    color = GoldBright,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Quick Access Prayers",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Daily Catholic prayers and devotions",
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Serif
                    )
                }
            }

            // Dark Mode Quick Toggle (if provided)
            if (onToggleDarkMode != null) {
                IconButton(
                    onClick = { onToggleDarkMode(!isDark) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = "Toggle Light / Dark Mode",
                        tint = GoldBright,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text("Search prayers, phrases...", color = subTextColor, fontSize = 13.sp)
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = iconAccent,
                    modifier = Modifier.size(19.dp)
                )
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = cardColor,
                unfocusedContainerColor = cardColor,
                focusedBorderColor = iconAccent,
                unfocusedBorderColor = cardBorder,
                focusedTextColor = textColor,
                unfocusedTextColor = textColor
            )
        )

        // Category Filter Pills
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            listOf("All", "Core", "Marian", "Devotions").forEach { cat ->
                val isSelected = activeCategory == cat
                val pillBg = if (isSelected) NavyHeader else cardColor
                val pillText = if (isSelected) Color.White else textColor
                val pillBorder = if (isSelected) iconAccent else cardBorder

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(pillBg)
                        .border(1.dp, pillBorder, RoundedCornerShape(20.dp))
                        .clickable { activeCategory = cat }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cat,
                        color = pillText,
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Prayers List
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filtered) { prayer ->
                val iconRes = PrayerIcons.getPrayerIcon(prayer.id)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(elevation = 1.dp, shape = RoundedCornerShape(14.dp))
                        .clip(RoundedCornerShape(14.dp))
                        .background(cardColor)
                        .border(1.dp, cardBorder, RoundedCornerShape(14.dp))
                        .clickable {
                            onSelectPrayer(prayer)
                            onClose()
                        }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Medallion icon
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0F2642))
                            .border(1.2.dp, iconAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = iconRes),
                            contentDescription = prayer.title,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = prayer.title,
                            color = textColor,
                            fontSize = 14.5.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (prayer.phrases.isNotEmpty()) {
                            Text(
                                text = prayer.phrases.first(),
                                color = subTextColor,
                                fontSize = 11.5.sp,
                                fontFamily = FontFamily.Serif,
                                maxLines = 1
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Open",
                        tint = iconAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
