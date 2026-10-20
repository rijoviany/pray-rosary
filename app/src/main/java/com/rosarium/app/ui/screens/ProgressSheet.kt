package com.rosarium.app.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rosarium.app.data.model.RosaryState
import com.rosarium.app.ui.theme.BackgroundSurface
import com.rosarium.app.ui.theme.MedievalGold
import com.rosarium.app.ui.theme.MedievalGoldBright
import com.rosarium.app.ui.theme.MedievalGoldDark
import com.rosarium.app.ui.theme.OxbloodRed
import com.rosarium.app.ui.theme.ParchmentBase
import com.rosarium.app.ui.theme.ParchmentDark
import com.rosarium.app.ui.theme.ParchmentLight
import com.rosarium.app.ui.theme.UltramarineLapis

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressSheet(
    state: RosaryState,
    onResetRosary: () -> Unit,
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
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Text(
                text = "Rosary Progress",
                color = ParchmentLight,
                fontSize = 22.sp,
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Current Decade Card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, MedievalGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .background(Color(0xFF201913))
                    .padding(18.dp)
            ) {
                Text(
                    text = "Decade ${state.decadeIndex + 1} of 5",
                    color = MedievalGoldBright,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = currentMystery?.title ?: "",
                    color = ParchmentLight,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold
                )

                if (currentMystery != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "\"${currentMystery.scriptureText}\"",
                        color = ParchmentBase,
                        fontSize = 13.5.sp,
                        fontFamily = FontFamily.Serif,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                    Text(
                        text = "— ${currentMystery.scriptureReference}",
                        color = MedievalGoldDark,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Serif,
                        modifier = Modifier.align(Alignment.End)
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Meditation: ${currentMystery.meditation}",
                        color = ParchmentDark,
                        fontSize = 13.sp,
                        fontFamily = FontFamily.Serif
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Decade Progress Bar
                val decadeFraction = state.completedHailMarysInDecade / 10f
                LinearProgressIndicator(
                    progress = { decadeFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MedievalGoldBright,
                    trackColor = Color(0xFF140E0A)
                )

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Hail Marys: ${state.completedHailMarysInDecade}/10",
                        color = ParchmentDark,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "Our Father: ${if (state.isOurFatherCompleted) "Prayed" else "Pending"}",
                        color = if (state.isOurFatherCompleted) MedievalGoldBright else ParchmentDark,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Serif
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Total 5 Decades Indicator Dots
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (i in 0 until 5) {
                    val isDone = i < state.decadeIndex
                    val isCurrent = i == state.decadeIndex

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .border(
                                    width = if (isCurrent) 1.5.dp else 1.dp,
                                    color = if (isCurrent) MedievalGoldBright else MedievalGoldDark,
                                    shape = CircleShape
                                )
                                .background(
                                    when {
                                        isDone -> MedievalGoldBright
                                        isCurrent -> UltramarineLapis
                                        else -> Color(0xFF18120E)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (i + 1).toString(),
                                color = if (isDone) Color.Black else ParchmentLight,
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Dec ${i + 1}",
                            color = ParchmentDark,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Serif
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Reset Rosary Button
            Button(
                onClick = {
                    onResetRosary()
                    onDismiss()
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = OxbloodRed.copy(alpha = 0.8f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = "Begin Rosary Anew",
                    color = ParchmentLight,
                    fontFamily = FontFamily.Serif,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
