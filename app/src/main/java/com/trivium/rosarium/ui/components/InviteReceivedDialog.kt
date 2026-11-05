package com.trivium.rosarium.ui.components

import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trivium.rosarium.R
import com.trivium.rosarium.data.model.PendingInvite

private val NavyPrimary = Color(0xFF0D2036)
private val CardIvory = Color(0xFFFFFFFF)
private val GoldAccent = Color(0xFFC5A059)
private val CharcoalText = Color(0xFF1E2836)
private val MutedText = Color(0xFF6B7A8D)

@Composable
fun InviteReceivedDialog(
    pendingInvite: PendingInvite,
    onJoin: (memberName: String) -> Unit,
    onDecline: () -> Unit
) {
    var memberName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDecline,
        containerColor = CardIvory,
        shape = RoundedCornerShape(20.dp),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF3ECE0))
                        .border(2.dp, GoldAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_novena_saint_joseph),
                        contentDescription = "Family Circle",
                        modifier = Modifier.size(50.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Family Rosary Invitation",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = NavyPrimary,
                    textAlign = TextAlign.Center
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "${pendingInvite.inviterName} has invited you to join the \"${pendingInvite.familyName}\" prayer circle.",
                    fontFamily = FontFamily.Serif,
                    fontSize = 14.sp,
                    color = CharcoalText,
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp
                )

                Text(
                    text = "Unite with your family in daily prayer of the Holy Rosary and share your intentions together.",
                    fontSize = 13.sp,
                    color = MutedText,
                    textAlign = TextAlign.Center
                )

                OutlinedTextField(
                    value = memberName,
                    onValueChange = { memberName = it },
                    label = { Text("Your Name in the Circle") },
                    placeholder = { Text("e.g. Maria or Dad") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAccent,
                        focusedLabelColor = NavyPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onJoin(memberName.trim()) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.GroupAdd,
                    contentDescription = null,
                    tint = GoldAccent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Join \"${pendingInvite.familyName}\"",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent,
                    fontSize = 14.sp
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDecline,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Decline for now", color = MutedText, fontSize = 13.sp)
            }
        }
    )
}
