package com.trivium.rosarium.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trivium.rosarium.R
import com.trivium.rosarium.data.model.FamilyGroup
import com.trivium.rosarium.data.model.FamilyMember
import com.trivium.rosarium.data.model.InviteCodeGenerator

private val NavyPrimary = Color(0xFF0D2036)
private val ParchmentBg = Color(0xFFF7F3EA)
private val CardIvory = Color(0xFFFFFFFF)
private val GoldAccent = Color(0xFFC5A059)
private val CharcoalText = Color(0xFF1E2836)
private val MutedText = Color(0xFF6B7A8D)
private val SubtleBorder = Color(0xFFE2D9CB)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilySheet(
    family: FamilyGroup?,
    onCreateFamily: (familyName: String, creatorName: String) -> Unit,
    onJoinFamily: (inviteCode: String, memberName: String) -> Unit,
    onLeaveFamily: () -> Unit,
    onUpdateIntention: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    var showCreateDialog by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }
    var showEditIntentionDialog by remember { mutableStateOf(false) }
    var showLeaveConfirmDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = ParchmentBg,
        dragHandle = null,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
        ) {
            // Top Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Close",
                        tint = NavyPrimary
                    )
                }

                Text(
                    text = "Family Prayer Circle",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = NavyPrimary
                )

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MutedText
                    )
                }
            }

            if (family == null) {
                // Not in a Family yet: Onboarding view
                NoFamilyView(
                    onCreateClick = { showCreateDialog = true },
                    onJoinClick = { showJoinDialog = true }
                )
            } else {
                // In a Family: Circle details & members
                ActiveFamilyView(
                    family = family,
                    onShareInvite = {
                        shareFamilyInvite(context, family)
                    },
                    onCopyInviteCode = {
                        copyToClipboard(context, family.inviteCode)
                    },
                    onEditIntentionClick = { showEditIntentionDialog = true },
                    onLeaveFamilyClick = { showLeaveConfirmDialog = true }
                )
            }
        }
    }

    // Dialog: Create Family
    if (showCreateDialog) {
        CreateFamilyDialog(
            onDismiss = { showCreateDialog = false },
            onConfirm = { name, creator ->
                showCreateDialog = false
                onCreateFamily(name, creator)
            }
        )
    }

    // Dialog: Join with Code
    if (showJoinDialog) {
        JoinFamilyDialog(
            onDismiss = { showJoinDialog = false },
            onConfirm = { code, name ->
                showJoinDialog = false
                onJoinFamily(code, name)
            }
        )
    }

    // Dialog: Edit Intention
    if (showEditIntentionDialog && family != null) {
        EditIntentionDialog(
            currentIntention = family.familyIntention,
            onDismiss = { showEditIntentionDialog = false },
            onConfirm = { newIntention ->
                showEditIntentionDialog = false
                onUpdateIntention(newIntention)
            }
        )
    }

    // Dialog: Confirm Leave
    if (showLeaveConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLeaveConfirmDialog = false },
            title = {
                Text(
                    text = "Leave Family Circle?",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = NavyPrimary
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to leave this prayer circle? You can re-join at any time with the invite code.",
                    color = CharcoalText,
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLeaveConfirmDialog = false
                        onLeaveFamily()
                    }
                ) {
                    Text("Leave", color = Color(0xFFC62828), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLeaveConfirmDialog = false }) {
                    Text("Cancel", color = MutedText)
                }
            },
            containerColor = CardIvory
        )
    }
}

@Composable
private fun NoFamilyView(
    onCreateClick: () -> Unit,
    onJoinClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(110.dp)
                .clip(CircleShape)
                .background(Color(0xFFF0EAE1))
                .border(2.dp, GoldAccent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_novena_saint_joseph),
                contentDescription = "Holy Family",
                modifier = Modifier.size(80.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Unite Your Family in Prayer",
            fontFamily = FontFamily.Serif,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            color = NavyPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "“The family that prays together stays together.”\n— Venerable Father Patrick Peyton\n\nInvite your family members via WhatsApp, Email, or SMS to pray the Rosary together and share intentions.",
            fontFamily = FontFamily.Serif,
            fontSize = 14.sp,
            color = MutedText,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )

        Spacer(modifier = Modifier.height(36.dp))

        // Create Button
        Button(
            onClick = onCreateClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = GoldAccent,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Create a Family Circle",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Join Button
        OutlinedButton(
            onClick = onJoinClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = NavyPrimary),
            border = BorderStroke(1.dp, GoldAccent),
            shape = RoundedCornerShape(14.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Login,
                contentDescription = null,
                tint = NavyPrimary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Join with Invite Code",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = NavyPrimary
            )
        }
    }
}

@Composable
private fun ActiveFamilyView(
    family: FamilyGroup,
    onShareInvite: () -> Unit,
    onCopyInviteCode: () -> Unit,
    onEditIntentionClick: () -> Unit,
    onLeaveFamilyClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Family Banner Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(3.dp, RoundedCornerShape(20.dp))
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF102844), Color(0xFF091626))
                        )
                    )
                    .border(1.dp, GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
                    .padding(20.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "✟",
                        color = GoldAccent,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = family.name,
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "Created by ${family.creatorName}",
                        fontFamily = FontFamily.Serif,
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Stats Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.08f))
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${family.members.size}",
                                color = GoldAccent,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                            Text(
                                text = "Members",
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 11.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(32.dp)
                                .background(Color.White.copy(alpha = 0.2f))
                        )

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${family.totalRosaries}",
                                color = GoldAccent,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Serif
                            )
                            Text(
                                text = "Rosaries Prayed",
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Invite Code & Share Bar
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.White.copy(alpha = 0.12f))
                            .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                            .padding(horizontal = 14.dp, vertical = 10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "INVITE CODE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = GoldAccent,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = family.inviteCode,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    color = Color.White
                                )
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                IconButton(
                                    onClick = onCopyInviteCode,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.15f))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Code",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = onShareInvite,
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(GoldAccent)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Share Invite",
                                        tint = NavyPrimary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Primary Full-width Share Button
                    Button(
                        onClick = onShareInvite,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = GoldAccent),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = NavyPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Share Invite Link via WhatsApp / Email",
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = NavyPrimary
                        )
                    }
                }
            }
        }

        // 2. Family Intention Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(2.dp, RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardIvory)
                    .border(1.dp, SubtleBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "FAMILY PRAYER INTENTION",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = GoldAccent,
                            letterSpacing = 1.sp
                        )

                        IconButton(
                            onClick = onEditIntentionClick,
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Intention",
                                tint = MutedText,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "“${family.familyIntention}”",
                        fontFamily = FontFamily.Serif,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        fontSize = 14.sp,
                        color = CharcoalText,
                        lineHeight = 20.sp
                    )
                }
            }
        }

        // 3. Family Members Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Family Members (${family.members.size})",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp,
                    color = NavyPrimary
                )
            }
        }

        // 4. Members List
        items(family.members) { member ->
            MemberCard(member = member)
        }

        // 5. Leave Family Option
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onLeaveFamilyClick)
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Leave Family Circle",
                    fontFamily = FontFamily.Serif,
                    color = Color(0xFFC62828),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun MemberCard(member: FamilyMember) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(1.dp, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(CardIvory)
            .border(1.dp, SubtleBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3ECE0))
                    .border(1.dp, GoldAccent.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = member.name.firstOrNull()?.uppercase() ?: "P",
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = NavyPrimary
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.name,
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                    color = CharcoalText,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = member.role,
                    fontSize = 12.sp,
                    color = MutedText
                )
            }

            // Rosaries badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFFF9F5EC))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "📿 ${member.rosariesPrayed}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = NavyPrimary
                )
            }
        }
    }
}

@Composable
private fun CreateFamilyDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, creator: String) -> Unit
) {
    var familyName by remember { mutableStateOf("") }
    var creatorName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Create Family Circle",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Create a prayer circle for your family. A unique random hash code will be generated for your invite link.",
                    fontSize = 13.sp,
                    color = MutedText
                )

                OutlinedTextField(
                    value = familyName,
                    onValueChange = { familyName = it },
                    label = { Text("Family Circle Name") },
                    placeholder = { Text("e.g. The Joseph Family") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAccent,
                        focusedLabelColor = NavyPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = creatorName,
                    onValueChange = { creatorName = it },
                    label = { Text("Your Name") },
                    placeholder = { Text("e.g. Mary or Dad") },
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
                onClick = {
                    if (familyName.isNotBlank()) {
                        onConfirm(familyName.trim(), creatorName.trim())
                    }
                },
                enabled = familyName.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Create Circle", color = GoldAccent, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MutedText)
            }
        },
        containerColor = CardIvory
    )
}

@Composable
private fun JoinFamilyDialog(
    onDismiss: () -> Unit,
    onConfirm: (code: String, name: String) -> Unit
) {
    var inviteCode by remember { mutableStateOf("") }
    var memberName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Join Family Circle",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "Enter the random hash invite code (e.g. 8f7b2c9a) you received from your family member:",
                    fontSize = 13.sp,
                    color = MutedText
                )

                OutlinedTextField(
                    value = inviteCode,
                    onValueChange = { inviteCode = it.trim().lowercase() },
                    label = { Text("Invite Code (Hash)") },
                    placeholder = { Text("e.g. 8f7b2c9a") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldAccent,
                        focusedLabelColor = NavyPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = memberName,
                    onValueChange = { memberName = it },
                    label = { Text("Your Name") },
                    placeholder = { Text("e.g. Teresa") },
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
                onClick = {
                    if (inviteCode.isNotBlank()) {
                        onConfirm(inviteCode.trim(), memberName.trim())
                    }
                },
                enabled = inviteCode.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Join Family", color = GoldAccent, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MutedText)
            }
        },
        containerColor = CardIvory
    )
}

@Composable
private fun EditIntentionDialog(
    currentIntention: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var intention by remember { mutableStateOf(currentIntention) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Family Prayer Intention",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                color = NavyPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Share what your family is holding in prayer today:",
                    fontSize = 13.sp,
                    color = MutedText
                )

                OutlinedTextField(
                    value = intention,
                    onValueChange = { intention = it },
                    maxLines = 4,
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
                onClick = { onConfirm(intention.trim()) },
                colors = ButtonDefaults.buttonColors(containerColor = NavyPrimary)
            ) {
                Text("Save", color = GoldAccent, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = MutedText)
            }
        },
        containerColor = CardIvory
    )
}

/**
 * Shared helper to launch Android share sheet (WhatsApp, Email, Messages)
 */
fun shareFamilyInvite(context: Context, family: FamilyGroup) {
    val message = InviteCodeGenerator.buildShareMessage(
        familyName = family.name,
        inviteCode = family.inviteCode,
        inviterName = family.creatorName
    )
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, message)
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Invite Family to Rosarium")
    context.startActivity(shareIntent)
}

private fun copyToClipboard(context: Context, code: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Family Invite Code", code)
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "Invite Code ($code) copied to clipboard", Toast.LENGTH_SHORT).show()
}
