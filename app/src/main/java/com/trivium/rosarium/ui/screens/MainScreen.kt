package com.trivium.rosarium.ui.screens

import androidx.compose.animation.Crossfade
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trivium.rosarium.R
import com.trivium.rosarium.ui.components.InviteReceivedDialog
import com.trivium.rosarium.ui.viewmodel.RosaryViewModel

enum class RosariumTab {
    HOME,
    PRAY,
    LIBRARY,
    MORE
}

private val NavyActive = Color(0xFF0D2036)
private val ParchmentBar = Color(0xFFFCF9F2)
private val BorderColor = Color(0xFFE2D9CB)
private val MutedSlate = Color(0xFF6B7A8D)

@Composable
fun MainScreen(
    viewModel: RosaryViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.rosaryState.collectAsState()
    val settings by viewModel.settingsState.collectAsState()
    val stats by viewModel.statsState.collectAsState()
    val novenaProgressMap by viewModel.novenaProgressMap.collectAsState()
    val family by viewModel.familyState.collectAsState()
    val pendingInvite by viewModel.pendingInviteState.collectAsState()

    var currentTab by remember { mutableStateOf(RosariumTab.HOME) }

    // Sheets & Dialogs state
    var showMysterySelector by remember { mutableStateOf(false) }
    var showProgressSheet by remember { mutableStateOf(false) }
    var showIntentionsSheet by remember { mutableStateOf(false) }
    var showNovenasSheet by remember { mutableStateOf(false) }
    var focusedNovenaId by remember { mutableStateOf<String?>(null) }
    var showSettingsSheet by remember { mutableStateOf(false) }
    var showPrayersSheet by remember { mutableStateOf(false) }
    var showFamilySheet by remember { mutableStateOf(false) }
    var initialPrayerId by remember { mutableStateOf<String?>(null) }

    val todaySet = remember(settings.language) {
        viewModel.getMysteryCatalog().mysterySets.find { it.id == viewModel.getTodayMysterySetId() }
            ?: viewModel.getMysteryCatalog().mysterySets.first()
    }

    val prayersList = remember(settings.language) {
        viewModel.getPrayers(settings.language)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = {
            // Show bottom bar on all tabs except during focused prayer screen
            if (currentTab != RosariumTab.PRAY) {
                RosariumBottomNavBar(
                    selectedTab = currentTab,
                    onSelectTab = { currentTab = it }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = currentTab,
                label = "MainTabTransition"
            ) { tab ->
                when (tab) {
                    RosariumTab.HOME -> {
                        HomeScreen(
                            todayMysterySet = todaySet,
                            family = family,
                            onNavigateToPray = {
                                viewModel.selectMysterySet(todaySet)
                                currentTab = RosariumTab.PRAY
                            },
                            onNavigateToPrayers = {
                                currentTab = RosariumTab.LIBRARY
                            },
                            onNavigateToChaplets = {
                                showMysterySelector = true
                            },
                            onNavigateToNovenas = {
                                showNovenasSheet = true
                            },
                            onNavigateToScripture = {
                                showIntentionsSheet = true
                            },
                            onNavigateToProgress = {
                                showProgressSheet = true
                            },
                            onOpenFamily = {
                                showFamilySheet = true
                            },
                            onOpenSettings = {
                                showSettingsSheet = true
                            }
                        )
                    }

                    RosariumTab.PRAY -> {
                        PrayerScreen(
                            viewModel = viewModel,
                            onNavigateBack = {
                                currentTab = RosariumTab.HOME
                            }
                        )
                    }

                    RosariumTab.LIBRARY -> {
                        // Screen 2: Prayers & Invocations
                        PrayersScreenContent(
                            prayers = prayersList,
                            initialPrayerId = initialPrayerId,
                            onBack = { currentTab = RosariumTab.HOME }
                        )
                    }

                    RosariumTab.MORE -> {
                        // Quick More view allowing access to Settings, Novenas, Family, and Progress
                        HomeScreen(
                            todayMysterySet = todaySet,
                            family = family,
                            onNavigateToPray = { currentTab = RosariumTab.PRAY },
                            onNavigateToPrayers = { currentTab = RosariumTab.LIBRARY },
                            onNavigateToChaplets = { showMysterySelector = true },
                            onNavigateToNovenas = { showNovenasSheet = true },
                            onNavigateToScripture = { showIntentionsSheet = true },
                            onNavigateToProgress = { showProgressSheet = true },
                            onOpenFamily = { showFamilySheet = true },
                            onOpenSettings = { showSettingsSheet = true }
                        )
                    }
                }
            }

            // Sheets & Modals
            if (showMysterySelector) {
                MysterySelectorSheet(
                    catalog = viewModel.getMysteryCatalog(),
                    currentSetId = state.currentMysterySet.id,
                    todaySetId = viewModel.getTodayMysterySetId(),
                    onSelectMysterySet = {
                        viewModel.selectMysterySet(it)
                        currentTab = RosariumTab.PRAY
                        showMysterySelector = false
                    },
                    onSelectDecade = {
                        viewModel.selectDecade(it)
                        currentTab = RosariumTab.PRAY
                        showMysterySelector = false
                    },
                    onDismiss = { showMysterySelector = false }
                )
            }

            if (showNovenasSheet) {
                NovenasSheet(
                    novenas = viewModel.allNovenas,
                    novenaProgressMap = novenaProgressMap,
                    initialNovenaId = focusedNovenaId,
                    onMarkDayComplete = { novenaId, day -> viewModel.markNovenaDayCompleted(novenaId, day) },
                    onResetNovena = { novenaId -> viewModel.resetNovena(novenaId) },
                    onDismiss = {
                        showNovenasSheet = false
                        focusedNovenaId = null
                    }
                )
            }

            if (showIntentionsSheet) {
                IntentionsSheet(
                    state = state,
                    onDismiss = { showIntentionsSheet = false }
                )
            }

            if (showProgressSheet) {
                ProgressSheet(
                    state = state,
                    stats = stats,
                    onResetRosary = { viewModel.resetRosary() },
                    onDismiss = { showProgressSheet = false }
                )
            }

            if (showPrayersSheet) {
                PrayersSheet(
                    prayers = prayersList,
                    initialPrayerId = initialPrayerId,
                    onDismiss = {
                        showPrayersSheet = false
                        initialPrayerId = null
                    }
                )
            }

            if (showSettingsSheet) {
                val totalGroups = state.currentMysterySet.mysteries.size.coerceAtLeast(1)
                val beadsPerGroup = state.currentMysterySet.effectiveBeadsPerDecade
                val currentBeadCount = (state.decadeIndex * beadsPerGroup) + state.completedHailMarysInDecade + (if (state.isOurFatherCompleted) 1 else 0)
                val totalBeadCount = (totalGroups * beadsPerGroup) + totalGroups

                SettingsSheet(
                    settings = settings,
                    catalog = viewModel.getMysteryCatalog(),
                    currentMysterySetId = state.currentMysterySet.id,
                    onSelectMysterySet = {
                        viewModel.selectMysterySet(it)
                        currentTab = RosariumTab.PRAY
                    },
                    allNovenas = viewModel.allNovenas,
                    novenaProgressMap = novenaProgressMap,
                    onSelectNovena = { novena ->
                        focusedNovenaId = novena.id
                        showNovenasSheet = true
                    },
                    onOpenReadIntentions = { showIntentionsSheet = true },
                    onOpenProgress = { showProgressSheet = true },
                    onOpenPrayers = {
                        currentTab = RosariumTab.LIBRARY
                    },
                    onOpenFamily = {
                        showFamilySheet = true
                    },
                    currentBeadCount = currentBeadCount.coerceIn(1, totalBeadCount),
                    totalBeadCount = totalBeadCount,
                    onUpdateSettings = { viewModel.updateSettings(it) },
                    onDismiss = { showSettingsSheet = false }
                )
            }

            if (showFamilySheet) {
                FamilySheet(
                    family = family,
                    onCreateFamily = { name, creator -> viewModel.createFamily(name, creator) },
                    onJoinFamily = { code, name -> viewModel.joinFamilyByCode(code, name) },
                    onLeaveFamily = { viewModel.leaveFamily() },
                    onUpdateIntention = { viewModel.updateFamilyIntention(it) },
                    onDismiss = { showFamilySheet = false }
                )
            }

            // Incoming Family Invitation Prompt (from Deep Link / Shared Link / Invite Code)
            pendingInvite?.let { invite ->
                InviteReceivedDialog(
                    pendingInvite = invite,
                    onJoin = { memberName -> viewModel.acceptPendingInvite(memberName) },
                    onDecline = { viewModel.declinePendingInvite() }
                )
            }
        }
    }
}

@Composable
fun RosariumBottomNavBar(
    selectedTab: RosariumTab,
    onSelectTab: (RosariumTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(ParchmentBar)
            .border(width = 1.dp, color = BorderColor)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            label = "Home",
            icon = Icons.Default.Home,
            isSelected = selectedTab == RosariumTab.HOME,
            onClick = { onSelectTab(RosariumTab.HOME) }
        )

        BottomNavDrawableItem(
            label = "Pray",
            drawableRes = R.drawable.ic_rosary_loop,
            isSelected = selectedTab == RosariumTab.PRAY,
            onClick = { onSelectTab(RosariumTab.PRAY) }
        )

        BottomNavItem(
            label = "Library",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            isSelected = selectedTab == RosariumTab.LIBRARY,
            onClick = { onSelectTab(RosariumTab.LIBRARY) }
        )

        BottomNavItem(
            label = "More",
            icon = Icons.Default.GridView,
            isSelected = selectedTab == RosariumTab.MORE,
            onClick = { onSelectTab(RosariumTab.MORE) }
        )
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(NavyActive)
                    .padding(horizontal = 14.dp, vertical = 3.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
        } else {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = MutedSlate,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            color = if (isSelected) NavyActive else MutedSlate,
            fontSize = 11.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
private fun BottomNavDrawableItem(
    label: String,
    drawableRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(NavyActive)
                    .padding(horizontal = 14.dp, vertical = 3.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = drawableRes),
                    contentDescription = label,
                    modifier = Modifier.size(20.dp)
                )
            }
        } else {
            Image(
                painter = painterResource(id = drawableRes),
                contentDescription = label,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            color = if (isSelected) NavyActive else MutedSlate,
            fontSize = 11.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
