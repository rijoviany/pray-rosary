package com.trivium.rosarium.ui.screens

import android.app.Activity
import android.view.WindowManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trivium.rosarium.data.model.DecadeStep
import com.trivium.rosarium.ui.components.BeadChain
import com.trivium.rosarium.ui.components.DraggableBead
import com.trivium.rosarium.ui.components.MysteryBackground
import com.trivium.rosarium.ui.components.MysteryMedallion
import com.trivium.rosarium.ui.components.PrayerBanner
import com.trivium.rosarium.ui.components.TopMedievalBar
import com.trivium.rosarium.ui.theme.BackgroundVignette
import com.trivium.rosarium.ui.theme.MedievalGoldBright
import com.trivium.rosarium.ui.theme.ParchmentLight
import com.trivium.rosarium.ui.viewmodel.RosaryViewModel

@Composable
fun PrayerScreen(
    viewModel: RosaryViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.rosaryState.collectAsState()
    val settings by viewModel.settingsState.collectAsState()
    val stats by viewModel.statsState.collectAsState()
    val novenaProgressMap by viewModel.novenaProgressMap.collectAsState()

    val density = LocalDensity.current
    var showMysterySelector by remember { mutableStateOf(false) }
    var showProgressSheet by remember { mutableStateOf(false) }
    var showIntentionsSheet by remember { mutableStateOf(false) }
    var showNovenasSheet by remember { mutableStateOf(false) }
    var focusedNovenaId by remember { mutableStateOf<String?>(null) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    // Screen-space vertical coordinates for pixel-perfect snap-up animation
    var centerSocketWindowY by remember { mutableFloatStateOf(0f) }
    var draggableBeadWindowY by remember { mutableFloatStateOf(0f) }
    val targetSnapOffsetPx = if (centerSocketWindowY > 0f && draggableBeadWindowY > 0f) {
        centerSocketWindowY - draggableBeadWindowY
    } else {
        with(density) { -410.dp.toPx() }
    }

    // Keep screen awake while user is praying the Holy Rosary
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(BackgroundVignette)
            .semantics {
                contentDescription = "Rosarium Prayer Screen. ${state.currentMysterySet.name}, decade ${state.decadeIndex + 1} of 5."
            }
    ) {
        // 1. Atmospheric Cathedral Sanctuary Background
        MysteryBackground(
            currentMystery = state.currentMystery,
            currentStep = state.currentStep,
            isHoldPraying = state.isPrayingActive
        )

        // 2. Main Holy Rosary Column flowing from Top to Bottom
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Navigation Bar (Hamburg Menu, Mystery Selector)
            TopMedievalBar(
                mysterySetName = state.currentMysterySet.name,
                currentMysteryTitle = "${state.decadeIndex + 1}. ${state.currentMystery?.title ?: ""}",
                onMenuClick = { showSettingsSheet = true },
                onTitleClick = { showMysterySelector = true }
            )

            val currentSet = state.currentMysterySet
            val beadsPerGroup = currentSet.effectiveBeadsPerDecade
            val primaryColor = currentSet.primaryBeadColor
            val specularColor = currentSet.specularBeadColor
            val darkColor = currentSet.darkBeadColor

            // Top Rosary Arc: 10 Hail Mary beads (or 7 for St. Michael), center Our Father bead, and hanging crucifix
            BeadChain(
                completedHailMarys = state.completedHailMarysInDecade,
                isOurFatherCompleted = state.isOurFatherCompleted,
                justCompletedIndex = state.justCompletedBeadIndex,
                isHoldPraying = state.isPrayingActive,
                totalBeadsInDecade = beadsPerGroup,
                primaryColor = primaryColor,
                specularColor = specularColor,
                darkColor = darkColor,
                modifier = Modifier.onGloballyPositioned { coordinates ->
                    centerSocketWindowY = coordinates.positionInWindow().y + with(density) { 22.dp.toPx() }
                }
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Centerpiece Mystery Medallion: Ornate carved gold bezel framing the mystery artwork
            MysteryMedallion(
                currentMystery = state.currentMystery,
                currentStep = state.currentStep,
                isHoldPraying = state.isPrayingActive
            )

            Spacer(modifier = Modifier.height(3.dp))

            // Parchment Prayer Card: PRAYING header, title, karaoke line-by-line text, cross divider
            PrayerBanner(
                prayer = state.activePrayer,
                currentPhraseIndex = state.currentPhraseIndex,
                prayerProgress = state.prayerProgress,
                isHoldPraying = state.isPrayingActive,
                currentStep = state.currentStep,
                fontScale = settings.fontScale
            )

            // Active Draggable Bead suspended on vertical golden chain directly below banner
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                key(state.decadeIndex, state.currentStep, state.completedHailMarysInDecade) {
                    DraggableBead(
                        isWhiteBead = state.isWhiteBeadActive,
                        completedBeadIndex = state.completedHailMarysInDecade,
                        primaryColor = primaryColor,
                        specularColor = specularColor,
                        darkColor = darkColor,
                        isPrayingActive = state.isPrayingActive,
                        isReadyToRelease = state.isReadyToRelease,
                        isTapToPrayEnabled = settings.tapToPrayEnabled,
                        prayerProgress = state.prayerProgress,
                        isSnappingUp = state.isSpringSnapping,
                        currentStep = state.currentStep,
                        targetSnapOffsetPx = targetSnapOffsetPx,
                        onHoldStart = { viewModel.startPrayer() },
                        onReleaseBead = { viewModel.onReleaseBead() },
                        onPrayerFinished = { viewModel.onSnapUpAnimationFinished() },
                        onBounceImpact = { decay -> viewModel.onBounceImpact(decay) },
                        onStringTension = { viewModel.onStringTension() },
                        modifier = Modifier.onGloballyPositioned { coordinates ->
                            draggableBeadWindowY = coordinates.positionInWindow().y + with(density) { 28.dp.toPx() }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Rosary Completion Celebration Overlay
        AnimatedVisibility(
            visible = state.isRosaryFinished,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier.align(Alignment.Center)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Deo Gratias",
                        color = MedievalGoldBright,
                        fontSize = 32.sp,
                        fontFamily = FontFamily.Serif
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "You have completed the Holy Rosary.",
                        color = ParchmentLight,
                        fontSize = 16.sp,
                        fontFamily = FontFamily.Serif
                    )
                }
            }
        }

        // Modals & Sheets
        if (showMysterySelector) {
            MysterySelectorSheet(
                catalog = viewModel.getMysteryCatalog(),
                currentSetId = state.currentMysterySet.id,
                todaySetId = viewModel.getTodayMysterySetId(),
                onSelectMysterySet = { viewModel.selectMysterySet(it) },
                onSelectDecade = { viewModel.selectDecade(it) },
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

        if (showSettingsSheet) {
            val totalGroups = state.currentMysterySet.mysteries.size.coerceAtLeast(1)
            val beadsPerGroup = state.currentMysterySet.effectiveBeadsPerDecade
            val currentBeadCount = (state.decadeIndex * beadsPerGroup) + state.completedHailMarysInDecade + (if (state.isOurFatherCompleted) 1 else 0)
            val totalBeadCount = (totalGroups * beadsPerGroup) + totalGroups

            SettingsSheet(
                settings = settings,
                catalog = viewModel.getMysteryCatalog(),
                currentMysterySetId = state.currentMysterySet.id,
                onSelectMysterySet = { viewModel.selectMysterySet(it) },
                allNovenas = viewModel.allNovenas,
                novenaProgressMap = novenaProgressMap,
                onSelectNovena = { novena ->
                    focusedNovenaId = novena.id
                    showNovenasSheet = true
                },
                onOpenReadIntentions = { showIntentionsSheet = true },
                onOpenProgress = { showProgressSheet = true },
                currentBeadCount = currentBeadCount.coerceIn(1, totalBeadCount),
                totalBeadCount = totalBeadCount,
                onUpdateSettings = { viewModel.updateSettings(it) },
                onDismiss = { showSettingsSheet = false }
            )
        }
    }
}
