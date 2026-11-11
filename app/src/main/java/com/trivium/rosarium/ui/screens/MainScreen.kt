package com.trivium.rosarium.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.layout.WindowInsets
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
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trivium.rosarium.R
import com.trivium.rosarium.ui.components.InviteReceivedDialog
import com.trivium.rosarium.ui.components.QuickAccessDrawerContent
import com.trivium.rosarium.ui.components.QuickAccessPrayersSheet
import com.trivium.rosarium.ui.components.UpdatePromptDialog
import com.trivium.rosarium.ui.theme.MedievalGoldBright
import com.trivium.rosarium.ui.viewmodel.RosaryViewModel
import kotlinx.coroutines.launch

enum class RosariumTab {
    HOME,
    PRAY,
    LIBRARY,
    NOVENAS,
    PROGRESS,
    MORE
}

private val NavyActive = Color(0xFF0D2036)
private val ParchmentBar = Color(0xFFFCF9F2)
private val BorderColor = Color(0xFFE2D9CB)
private val MutedSlate = Color(0xFF6B7A8D)

@Composable
fun MainScreen(
    viewModel: RosaryViewModel,
    onTriggerUpdate: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val state by viewModel.rosaryState.collectAsState()
    val settings by viewModel.settingsState.collectAsState()
    val stats by viewModel.statsState.collectAsState()
    val novenaProgressMap by viewModel.novenaProgressMap.collectAsState()
    val family by viewModel.familyState.collectAsState()
    val pendingInvite by viewModel.pendingInviteState.collectAsState()
    val updateStatus by viewModel.updateStatus.collectAsState()
    val savedProgress by viewModel.savedProgress.collectAsState()
    val lastActiveNovena by viewModel.lastActiveNovena.collectAsState()

    var currentTab by remember { mutableStateOf(RosariumTab.HOME) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Sheets & Dialogs state
    var showQuickAccessPrayersSheet by remember { mutableStateOf(false) }
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

    val activeNovena = remember(lastActiveNovena.first, novenaProgressMap) {
        viewModel.allNovenas.find { it.id == lastActiveNovena.first }
            ?: viewModel.allNovenas.firstOrNull { novenaProgressMap[it.id]?.completedDays?.isNotEmpty() == true }
    }

    val prayersList = remember(settings.language) {
        viewModel.getPrayers(settings.language)
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = currentTab != RosariumTab.PRAY,
        drawerContent = {
            QuickAccessDrawerContent(
                prayers = prayersList,
                isDark = settings.isDarkMode,
                onSelectPrayer = { prayer ->
                    coroutineScope.launch { drawerState.close() }
                    initialPrayerId = prayer.id
                    currentTab = RosariumTab.LIBRARY
                },
                onClose = {
                    coroutineScope.launch { drawerState.close() }
                },
                onToggleDarkMode = {
                    viewModel.updateSettings(settings.copy(isDarkMode = it))
                },
                onOpenRosary = {
                    coroutineScope.launch { drawerState.close() }
                    currentTab = RosariumTab.PRAY
                },
                onOpenNovenas = {
                    coroutineScope.launch { drawerState.close() }
                    currentTab = RosariumTab.NOVENAS
                },
                onOpenProgress = {
                    coroutineScope.launch { drawerState.close() }
                    currentTab = RosariumTab.PROGRESS
                },
                onOpenSettings = {
                    coroutineScope.launch { drawerState.close() }
                    showSettingsSheet = true
                }
            )
        }
    ) {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                // Show bottom bar on all tabs except during focused prayer screen
                if (currentTab != RosariumTab.PRAY) {
                    RosariumBottomNavBar(
                        selectedTab = currentTab,
                        isDark = settings.isDarkMode,
                        onSelectTab = { tab ->
                            if (tab == RosariumTab.MORE) {
                                showSettingsSheet = true
                            } else {
                                currentTab = tab
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
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
                                savedProgress = savedProgress,
                                activeMysterySet = state.currentMysterySet,
                                activeNovena = activeNovena,
                                activeNovenaProgress = novenaProgressMap[activeNovena?.id],
                                isDark = settings.isDarkMode,
                                onOpenQuickPrayers = {
                                    coroutineScope.launch {
                                        drawerState.open()
                                    }
                                },
                                onNavigateToPray = {
                                    currentTab = RosariumTab.PRAY
                                },
                                onResumeRosary = {
                                    currentTab = RosariumTab.PRAY
                                },
                                onStartTodayRosary = {
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
                                    currentTab = RosariumTab.NOVENAS
                                },
                                onOpenActiveNovena = { novenaId ->
                                    focusedNovenaId = novenaId
                                    currentTab = RosariumTab.NOVENAS
                                },
                                onNavigateToScripture = {
                                    showIntentionsSheet = true
                                },
                                onNavigateToProgress = {
                                    currentTab = RosariumTab.PROGRESS
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
                            // Screen 2: Prayers & Invocations (and Screen 3: Prayer Detail)
                            PrayersScreenContent(
                                prayers = prayersList,
                                initialPrayerId = initialPrayerId,
                                isDark = settings.isDarkMode,
                                onBack = { currentTab = RosariumTab.HOME }
                            )
                        }

                        RosariumTab.NOVENAS -> {
                            // Screen 5: 9-Day Novenas (Full Screen matching Screen 5 reference)
                            NovenasScreenContent(
                                novenas = viewModel.allNovenas,
                                novenaProgressMap = novenaProgressMap,
                                initialNovenaId = focusedNovenaId ?: lastActiveNovena.first,
                                isDark = settings.isDarkMode,
                                onNovenaOpened = { novenaId, day -> viewModel.recordNovenaOpened(novenaId, day) },
                                onMarkDayComplete = { novenaId, day -> viewModel.markNovenaDayCompleted(novenaId, day) },
                                onResetNovena = { novenaId -> viewModel.resetNovena(novenaId) },
                                onBack = { currentTab = RosariumTab.HOME }
                            )
                        }

                        RosariumTab.PROGRESS -> {
                            // Screen 6: Prayer Progress (Full Screen matching Screen 6 reference)
                            ProgressScreenContent(
                                state = state,
                                stats = stats,
                                isDark = settings.isDarkMode,
                                onResetRosary = { viewModel.resetRosary() },
                                onBack = { currentTab = RosariumTab.HOME }
                            )
                        }

                        RosariumTab.MORE -> {
                            // Quick More view allowing access to Settings, Novenas, Family, and Progress
                            HomeScreen(
                                todayMysterySet = todaySet,
                                family = family,
                                isDark = settings.isDarkMode,
                                onOpenQuickPrayers = { coroutineScope.launch { drawerState.open() } },
                                onNavigateToPray = { currentTab = RosariumTab.PRAY },
                                onNavigateToPrayers = { currentTab = RosariumTab.LIBRARY },
                                onNavigateToChaplets = { showMysterySelector = true },
                                onNavigateToNovenas = { currentTab = RosariumTab.NOVENAS },
                                onNavigateToScripture = { showIntentionsSheet = true },
                                onNavigateToProgress = { currentTab = RosariumTab.PROGRESS },
                                onOpenFamily = { showFamilySheet = true },
                                onOpenSettings = { showSettingsSheet = true }
                            )
                        }
                    }
                }

                // Quick Access Prayers Drawer Fallback (if triggered explicitly)
                if (showQuickAccessPrayersSheet) {
                    QuickAccessPrayersSheet(
                        prayers = prayersList,
                        onSelectPrayer = { prayer ->
                            initialPrayerId = prayer.id
                            currentTab = RosariumTab.LIBRARY
                            showQuickAccessPrayersSheet = false
                        },
                        onDismiss = { showQuickAccessPrayersSheet = false },
                        isDark = settings.isDarkMode,
                        onToggleDarkMode = {
                            viewModel.updateSettings(settings.copy(isDarkMode = it))
                        }
                    )
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
                    initialNovenaId = focusedNovenaId ?: lastActiveNovena.first,
                    isDark = settings.isDarkMode,
                    onNovenaOpened = { novenaId, day -> viewModel.recordNovenaOpened(novenaId, day) },
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
                    isDark = settings.isDarkMode,
                    onResetRosary = { viewModel.resetRosary() },
                    onDismiss = { showProgressSheet = false }
                )
            }

            if (showPrayersSheet) {
                PrayersSheet(
                    prayers = prayersList,
                    initialPrayerId = initialPrayerId,
                    isDark = settings.isDarkMode,
                    onDismiss = {
                        showPrayersSheet = false
                        initialPrayerId = null
                    }
                )
            }

            if (showSettingsSheet) {
                SettingsSheet(
                    settings = settings,
                    onUpdateSettings = { viewModel.updateSettings(it) },
                    onOpenFamily = { showFamilySheet = true },
                    onCheckForUpdate = {
                        Toast.makeText(context, "Checking for updates...", Toast.LENGTH_SHORT).show()
                        viewModel.checkForUpdate { isAvailable, _ ->
                            if (!isAvailable) {
                                Toast.makeText(
                                    context,
                                    "Rosarium is up to date (v${com.trivium.rosarium.BuildConfig.VERSION_NAME})",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
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

            // In-App Update Prompt Dialog
            if (updateStatus.isAvailable || updateStatus.isDownloaded) {
                UpdatePromptDialog(
                    isDownloaded = updateStatus.isDownloaded,
                    isDark = settings.isDarkMode,
                    onUpdate = {
                        if (updateStatus.isDownloaded) {
                            viewModel.completeFlexibleUpdate()
                        } else {
                            onTriggerUpdate()
                        }
                    },
                    onDismiss = {
                        viewModel.dismissUpdatePrompt()
                    }
                )
            }
        }
    }
}
}

@Composable
fun RosariumBottomNavBar(
    selectedTab: RosariumTab,
    isDark: Boolean = false,
    onSelectTab: (RosariumTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val barBg = if (isDark) Color(0xFF09121D) else ParchmentBar
    val borderColor = if (isDark) Color(0xFF1E2E40) else BorderColor
    val activeColor = if (isDark) MedievalGoldBright else NavyActive
    val activeBg = if (isDark) Color(0xFF162B40) else NavyActive
    val mutedColor = if (isDark) Color(0xFF8A9FB5) else MutedSlate

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(barBg)
            .border(width = 1.dp, color = borderColor)
            .navigationBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavItem(
            label = "Home",
            icon = Icons.Default.Home,
            isSelected = selectedTab == RosariumTab.HOME,
            activeBg = activeBg,
            activeColor = activeColor,
            mutedColor = mutedColor,
            onClick = { onSelectTab(RosariumTab.HOME) }
        )

        BottomNavDrawableItem(
            label = "Pray",
            drawableRes = R.drawable.ic_rosary_loop,
            isSelected = selectedTab == RosariumTab.PRAY,
            activeBg = activeBg,
            activeColor = activeColor,
            mutedColor = mutedColor,
            onClick = { onSelectTab(RosariumTab.PRAY) }
        )

        BottomNavItem(
            label = "Library",
            icon = Icons.AutoMirrored.Filled.MenuBook,
            isSelected = selectedTab == RosariumTab.LIBRARY,
            activeBg = activeBg,
            activeColor = activeColor,
            mutedColor = mutedColor,
            onClick = { onSelectTab(RosariumTab.LIBRARY) }
        )

        BottomNavItem(
            label = "More",
            icon = Icons.Default.GridView,
            isSelected = selectedTab == RosariumTab.MORE || selectedTab == RosariumTab.NOVENAS || selectedTab == RosariumTab.PROGRESS,
            activeBg = activeBg,
            activeColor = activeColor,
            mutedColor = mutedColor,
            onClick = { onSelectTab(RosariumTab.MORE) }
        )
    }
}

@Composable
private fun BottomNavItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    activeBg: Color,
    activeColor: Color,
    mutedColor: Color,
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
                    .background(activeBg)
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
                tint = mutedColor,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = label,
            color = if (isSelected) activeColor else mutedColor,
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
    activeBg: Color,
    activeColor: Color,
    mutedColor: Color,
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
                    .background(activeBg)
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
            color = if (isSelected) activeColor else mutedColor,
            fontSize = 11.sp,
            fontFamily = FontFamily.Serif,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
