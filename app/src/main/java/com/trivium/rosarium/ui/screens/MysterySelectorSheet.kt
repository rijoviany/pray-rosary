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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
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
import com.trivium.rosarium.data.model.MysteryCatalog
import com.trivium.rosarium.data.model.MysterySet
import com.trivium.rosarium.ui.theme.BackgroundSurface
import com.trivium.rosarium.ui.theme.MedievalGold
import com.trivium.rosarium.ui.theme.MedievalGoldBright
import com.trivium.rosarium.ui.theme.MedievalGoldDark
import com.trivium.rosarium.ui.theme.OxbloodRed
import com.trivium.rosarium.ui.theme.ParchmentBase
import com.trivium.rosarium.ui.theme.ParchmentDark
import com.trivium.rosarium.ui.theme.ParchmentLight

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

    val marianRosaries = catalog.mysterySets.filter { !it.isChaplet }
    val catholicChaplets = catalog.mysterySets.filter { it.isChaplet }

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
                color = ParchmentDark,
                fontSize = 12.sp,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(bottom = 14.dp)
            )

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Section 1: The Holy Rosary of the Blessed Virgin Mary
                item {
                    Text(
                        text = "THE HOLY ROSARY OF THE BLESSED VIRGIN MARY",
                        color = MedievalGold,
                        fontSize = 11.5.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp,
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }

                items(marianRosaries) { set ->
                    MysterySetCard(
                        set = set,
                        isSelected = set.id == currentSetId,
                        isToday = set.id == todaySetId,
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
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "APPROVED CATHOLIC CHAPLETS & DEVOTIONS",
                            color = MedievalGold,
                            fontSize = 11.5.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                        )
                    }

                    items(catholicChaplets) { set ->
                        MysterySetCard(
                            set = set,
                            isSelected = set.id == currentSetId,
                            isToday = false,
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
private fun MysterySetCard(
    set: MysterySet,
    isSelected: Boolean,
    isToday: Boolean,
    onSelectMysterySet: () -> Unit,
    onSelectDecade: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = if (isSelected) MedievalGoldBright else ParchmentDark.copy(alpha = 0.35f),
                shape = RoundedCornerShape(12.dp)
            )
            .background(if (isSelected) Color(0xFF251C15) else Color(0xFF18130E))
            .clickable(onClick = onSelectMysterySet)
            .padding(14.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Gemstone bead color indicator dot
            Box(
                modifier = Modifier
                    .size(15.dp)
                    .clip(CircleShape)
                    .background(set.primaryBeadColor)
                    .border(1.2.dp, set.specularBeadColor, CircleShape)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = set.name,
                color = if (isSelected) MedievalGoldBright else ParchmentLight,
                fontSize = 16.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            // Bead count badge (e.g. 10 Beads or 7 Beads)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFF2E2218))
                    .border(1.dp, MedievalGoldDark.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 7.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${set.effectiveBeadsPerDecade} Beads",
                    color = MedievalGoldBright,
                    fontSize = 10.5.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Medium
                )
            }

            if (isToday) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(OxbloodRed.copy(alpha = 0.85f))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "Today",
                        color = ParchmentLight,
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (isSelected) {
                Spacer(modifier = Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = MedievalGoldBright,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (set.recommendedDays.isNotEmpty()) "Recommended: ${set.recommendedDays.joinToString(", ")}" else "Approved Catholic Devotion",
            color = ParchmentDark,
            fontSize = 11.5.sp,
            fontFamily = FontFamily.Serif
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Decades/Mysteries list
        set.mysteries.forEach { mystery ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 3.dp)
                    .clickable {
                        onSelectDecade(mystery.number - 1)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(19.dp)
                        .clip(CircleShape)
                        .background(MedievalGoldDark.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = mystery.number.toString(),
                        color = ParchmentLight,
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Serif
                    )
                }
                Spacer(modifier = Modifier.width(9.dp))
                Column {
                    Text(
                        text = mystery.title,
                        color = ParchmentBase,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "Fruit: ${mystery.fruitOfMystery}",
                        color = ParchmentDark.copy(alpha = 0.8f),
                        fontSize = 10.5.sp,
                        fontFamily = FontFamily.Serif
                    )
                }
            }
        }
    }
}
