package com.trivium.rosarium.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trivium.rosarium.data.model.MysteryCatalog
import com.trivium.rosarium.data.model.MysterySet
import com.trivium.rosarium.ui.components.PrayerIcons
import com.trivium.rosarium.ui.theme.MedievalGold
import com.trivium.rosarium.ui.theme.MedievalGoldBright
import com.trivium.rosarium.ui.theme.MedievalGoldDark
import com.trivium.rosarium.ui.theme.OxbloodRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MysterySelectorSheet(
    catalog: MysteryCatalog,
    currentSetId: String,
    todaySetId: String,
    onSelectMysterySet: (MysterySet) -> Unit,
    onSelectDecade: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var expandedSetId by remember { mutableStateOf<String?>(currentSetId) }

    val marianRosaries = catalog.mysterySets.filter { !it.isChaplet }
    val catholicChaplets = catalog.mysterySets.filter { it.isChaplet }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF09121D),
        scrimColor = Color.Black.copy(alpha = 0.75f),
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Text(
                text = "Rosaries & Approved Chaplets",
                color = MedievalGoldBright,
                fontSize = 20.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Select a devotion to pray upon the sacred beads",
                color = Color(0xFF9FB2C8),
                fontSize = 12.sp,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Section 1: The Holy Rosary of the Blessed Virgin Mary
                item {
                    Text(
                        text = "THE HOLY ROSARY OF THE BLESSED VIRGIN MARY",
                        color = MedievalGold,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }

                items(marianRosaries) { set ->
                    CollapsibleMysterySetCard(
                        set = set,
                        isSelected = set.id == currentSetId,
                        isToday = set.id == todaySetId,
                        isExpanded = expandedSetId == set.id,
                        onToggleExpand = {
                            expandedSetId = if (expandedSetId == set.id) null else set.id
                        },
                        onSelectMysterySet = {
                            onSelectMysterySet(set)
                            onDismiss()
                        },
                        onSelectDecade = { decadeIndex ->
                            onSelectMysterySet(set)
                            onSelectDecade(decadeIndex)
                            onDismiss()
                        }
                    )
                }

                // Section 2: Approved Catholic Chaplets & Devotions
                if (catholicChaplets.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "APPROVED CATHOLIC CHAPLETS & DEVOTIONS",
                            color = MedievalGold,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                        )
                    }

                    items(catholicChaplets) { set ->
                        CollapsibleMysterySetCard(
                            set = set,
                            isSelected = set.id == currentSetId,
                            isToday = false,
                            isExpanded = expandedSetId == set.id,
                            onToggleExpand = {
                                expandedSetId = if (expandedSetId == set.id) null else set.id
                            },
                            onSelectMysterySet = {
                                onSelectMysterySet(set)
                                onDismiss()
                            },
                            onSelectDecade = { decadeIndex ->
                                onSelectMysterySet(set)
                                onSelectDecade(decadeIndex)
                                onDismiss()
                            }
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(28.dp))
                }
            }
        }
    }
}

@Composable
private fun CollapsibleMysterySetCard(
    set: MysterySet,
    isSelected: Boolean,
    isToday: Boolean,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onSelectMysterySet: () -> Unit,
    onSelectDecade: (Int) -> Unit
) {
    val cardBg = if (isSelected) Color(0xFF16283D) else Color(0xFF132235)
    val cardBorder = if (isSelected) MedievalGoldBright else Color(0xFF233B57)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = cardBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .background(cardBg)
    ) {
        // Collapsed Header Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggleExpand)
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            // Icon Medallion
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF0F2642))
                    .border(1.2.dp, if (isSelected) MedievalGoldBright else Color(0xFF2B4769), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = PrayerIcons.getMysterySetIcon(set.id)),
                    contentDescription = set.name,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = set.name,
                        color = if (isSelected) MedievalGoldBright else Color(0xFFF7F3EA),
                        fontSize = 15.5.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (isToday) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(OxbloodRed)
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "Today",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
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

                Spacer(modifier = Modifier.height(2.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${set.mysteries.size} Decades • ${set.effectiveBeadsPerDecade} Beads",
                        color = Color(0xFF9FB2C8),
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Serif
                    )

                    if (set.recommendedDays.isNotEmpty()) {
                        Text(
                            text = " • ${set.recommendedDays.joinToString(", ")}",
                            color = MedievalGold.copy(alpha = 0.85f),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Serif
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Expand/Collapse Chevron Indicator
            Icon(
                imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                contentDescription = if (isExpanded) "Collapse" else "Expand",
                tint = if (isSelected) MedievalGoldBright else Color(0xFF9FB2C8),
                modifier = Modifier.size(22.dp)
            )
        }

        // Expanded Accordion Content: Shows Mysteries / Decades list & Start button
        AnimatedVisibility(
            visible = isExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F1E30))
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                // Quick "Pray from Beginning" Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1B334F))
                        .border(1.dp, MedievalGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .clickable(onClick = onSelectMysterySet)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Pray All Decades (From 1st)",
                        color = Color(0xFFF7F3EA),
                        fontSize = 12.5.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Start",
                            color = MedievalGoldBright,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start",
                            tint = MedievalGoldBright,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // List of Individual Mysteries/Decades
                set.mysteries.forEach { mystery ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(6.dp))
                            .clickable {
                                onSelectDecade(mystery.number - 1)
                            }
                            .padding(vertical = 5.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0A1624))
                                .border(1.dp, MedievalGoldDark.copy(alpha = 0.6f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = mystery.number.toString(),
                                color = MedievalGoldBright,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = mystery.title,
                                color = Color(0xFFF7F3EA),
                                fontSize = 13.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Medium
                            )
                            if (mystery.fruitOfMystery.isNotBlank()) {
                                Text(
                                    text = "Fruit: ${mystery.fruitOfMystery}",
                                    color = Color(0xFF9FB2C8),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Serif
                                )
                            }
                        }

                        Text(
                            text = "Jump →",
                            color = MedievalGold.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Serif
                        )
                    }
                }
            }
        }
    }
}
