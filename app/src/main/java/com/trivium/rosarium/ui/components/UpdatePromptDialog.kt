package com.trivium.rosarium.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.trivium.rosarium.ui.theme.MedievalGold
import com.trivium.rosarium.ui.theme.MedievalGoldBright

@Composable
fun UpdatePromptDialog(
    isDownloaded: Boolean,
    isDark: Boolean,
    onUpdate: () -> Unit,
    onDismiss: () -> Unit
) {
    val dialogBg = if (isDark) Color(0xFF0D1B2A) else Color(0xFFFBF8F2)
    val borderColor = if (isDark) MedievalGoldBright.copy(alpha = 0.5f) else Color(0xFFD4AF37)
    val primaryText = if (isDark) Color(0xFFF5EBE1) else Color(0xFF1E1711)
    val secondaryText = if (isDark) Color(0xFFB0A495) else Color(0xFF6B5E52)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = !isDownloaded,
            dismissOnClickOutside = !isDownloaded
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.5.dp, borderColor, RoundedCornerShape(20.dp))
                .background(dialogBg)
                .padding(24.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Sacred Glowing Icon Badge
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(
                            if (isDark) Color(0xFF162B40) else Color(0xFFF3ECE0)
                        )
                        .border(1.dp, MedievalGoldBright.copy(alpha = 0.4f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isDownloaded) Icons.Default.SystemUpdate else Icons.Default.CloudDownload,
                        contentDescription = "Update",
                        tint = MedievalGoldBright,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isDownloaded) "Update Ready to Install" else "Update Available",
                    fontSize = 19.sp,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = primaryText,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isDownloaded) {
                        "The latest version of Rosarium has been downloaded and is ready to install. Restart the app to apply the update."
                    } else {
                        "A new version of Rosarium is available with official Catholic prayers, improved devotions, audio enhancements, and performance updates."
                    },
                    fontSize = 13.5.sp,
                    fontFamily = FontFamily.Serif,
                    color = secondaryText,
                    textAlign = TextAlign.Center,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (!isDownloaded) {
                        // Dismiss / Later Button
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, borderColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .clickable { onDismiss() }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Later",
                                color = secondaryText,
                                fontSize = 14.sp,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Primary Update / Restart Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MedievalGold)
                            .clickable { onUpdate() }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isDownloaded) "Restart Now" else "Update Now",
                            color = Color(0xFF0F1A24),
                            fontSize = 14.sp,
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
