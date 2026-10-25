package com.trivium.rosarium.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trivium.rosarium.data.model.RosaryState
import com.trivium.rosarium.ui.theme.BackgroundSurface
import com.trivium.rosarium.ui.theme.MedievalGold
import com.trivium.rosarium.ui.theme.MedievalGoldBright
import com.trivium.rosarium.ui.theme.MedievalGoldDark
import com.trivium.rosarium.ui.theme.ParchmentBase
import com.trivium.rosarium.ui.theme.ParchmentDark
import com.trivium.rosarium.ui.theme.ParchmentLight
import com.trivium.rosarium.ui.theme.UltramarineLapis

/**
 * Dedicated Intentions Bottom Sheet:
 * Displays the Fruit of the Mystery (Spiritual Intention), Sacred Scripture,
 * Devotional Meditation, and Altar Candle for personal prayer intentions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IntentionsSheet(
    state: RosaryState,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val currentMystery = state.currentMystery

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
                .padding(horizontal = 24.dp, vertical = 14.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with Candle Icon
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .border(1.2.dp, MedievalGoldBright.copy(alpha = 0.85f), CircleShape)
                        .background(Color(0xFF140F0A)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(20.dp)) {
                        val w = size.width
                        val h = size.height
                        // Candle flame
                        val flamePath = Path().apply {
                            moveTo(w * 0.5f, h * 0.12f)
                            quadraticTo(w * 0.72f, h * 0.38f, w * 0.5f, h * 0.48f)
                            quadraticTo(w * 0.28f, h * 0.38f, w * 0.5f, h * 0.12f)
                        }
                        drawPath(flamePath, MedievalGoldBright)
                        // Candle body
                        drawRoundRect(
                            color = MedievalGold.copy(alpha = 0.9f),
                            topLeft = Offset(w * 0.36f, h * 0.48f),
                            size = Size(w * 0.28f, h * 0.42f),
                            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx())
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Spiritual Intentions",
                        color = ParchmentLight,
                        fontSize = 20.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${state.currentMysterySet.name} • Decade ${state.decadeIndex + 1} of 5",
                        color = MedievalGoldBright,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Serif
                    )
                }
            }

            if (currentMystery != null) {
                // 1. Primary Spiritual Fruit / Mystery Intention Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF261A0E), Color(0xFF181008))
                            )
                        )
                        .border(1.2.dp, MedievalGoldBright.copy(alpha = 0.75f), RoundedCornerShape(12.dp))
                        .padding(16.dp)
                ) {
                    Text(
                        text = "— FRUIT OF THE MYSTERY —",
                        color = MedievalGoldBright,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = currentMystery.fruitOfMystery,
                        color = ParchmentLight,
                        fontSize = 19.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Offered during this decade of ${currentMystery.title}: praying for the grace of ${currentMystery.fruitOfMystery.lowercase()}.",
                        color = ParchmentBase.copy(alpha = 0.85f),
                        fontSize = 12.5.sp,
                        fontFamily = FontFamily.Serif,
                        lineHeight = 17.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Scripture Reading Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF140F0A))
                        .border(1.dp, MedievalGold.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Sacred Scripture",
                            color = MedievalGoldBright,
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = currentMystery.scriptureReference,
                            color = MedievalGold,
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "“${currentMystery.scriptureText}”",
                        color = ParchmentLight,
                        fontSize = 13.5.sp,
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 19.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Sacred Meditation Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF140F0A))
                        .border(1.dp, MedievalGold.copy(alpha = 0.45f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
                ) {
                    Text(
                        text = "Meditation",
                        color = MedievalGoldBright,
                        fontSize = 14.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = currentMystery.meditation,
                        color = ParchmentDark,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Serif,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 4. Personal Intentions Devotional Card
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1A130D))
                        .border(1.dp, MedievalGoldDark.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Personal Intentions & Petitions",
                        color = MedievalGoldBright,
                        fontSize = 13.5.sp,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Hold in your heart your family, loved ones, departed souls, and personal petitions as you draw each bead of this sacred decade.",
                        color = ParchmentBase.copy(alpha = 0.8f),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Serif,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.5.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
