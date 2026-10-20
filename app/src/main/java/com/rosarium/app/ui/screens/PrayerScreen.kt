package com.rosarium.app.ui.screens

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rosarium.app.data.model.DecadeStep
import com.rosarium.app.ui.components.BeadChain
import com.rosarium.app.ui.components.BottomMedievalControls
import com.rosarium.app.ui.components.DraggableBead
import com.rosarium.app.ui.components.MedievalFrameOverlay
import com.rosarium.app.ui.components.MysteryBackground
import com.rosarium.app.ui.components.PrayerBanner
import com.rosarium.app.ui.components.TopMedievalBar
import com.rosarium.app.ui.theme.BackgroundVignette
import com.rosarium.app.ui.theme.MedievalGoldBright
import com.rosarium.app.ui.theme.ParchmentLight
import com.rosarium.app.ui.viewmodel.RosaryViewModel

@Composable
fun PrayerScreen(
    viewModel: RosaryViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.rosaryState.collectAsState()
    val settings by viewModel.settingsState.collectAsState()

    var showMysterySelector by remember { mutableStateOf(false) }
    var showProgressSheet by remember { mutableStateOf(false) }
    var showSettingsSheet by remember { mutableStateOf(false) }

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
        // 1. Crossfading Mystery Artwork with soft vignette
        MysteryBackground(
            currentMystery = state.currentMystery,
            isHoldPraying = state.isPrayingActive
        )

        // 2. The 10-bead Decade Chain & Our Father Centerpiece
        BeadChain(
            completedHailMarys = state.completedHailMarysInDecade,
            isOurFatherCompleted = state.isOurFatherCompleted,
            justCompletedIndex = state.justCompletedBeadIndex,
            isHoldPraying = state.isPrayingActive
        )

        // 3. Active Draggable / Hold-to-Pray Bead with spring physics
        DraggableBead(
            isWhiteBead = state.isWhiteBeadActive,
            isPrayingActive = state.isPrayingActive,
            isTapToPrayEnabled = settings.tapToPrayEnabled,
            prayerProgress = state.prayerProgress,
            isSnappingUp = state.isSpringSnapping,
            onHoldStart = { viewModel.startPrayer() },
            onHoldReleaseEarly = { viewModel.cancelPrayerEarly() },
            onPrayerFinished = { viewModel.onSnapUpAnimationFinished() },
            onBounceImpact = { decay -> viewModel.onBounceImpact(decay) },
            onStringTension = { viewModel.onStringTension() }
        )

        // 4. Manuscript Frame & Corner Flourishes
        MedievalFrameOverlay()

        // 5. Header and Prayer Banner Overlay
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TopMedievalBar(
                mysterySetName = state.currentMysterySet.name,
                currentMysteryTitle = "${state.decadeIndex + 1}. ${state.currentMystery?.title ?: ""}",
                onTitleClick = { showMysterySelector = true },
                onScriptureClick = { showProgressSheet = true }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Prayer Banner with line-by-line karaoke reveal
            PrayerBanner(
                prayer = state.activePrayer,
                currentPhraseIndex = state.currentPhraseIndex,
                prayerProgress = state.prayerProgress,
                visible = state.isPrayingActive,
                fontScale = settings.fontScale
            )
        }

        // 6. Bottom Navigation Controls (Menu & Progress)
        BottomMedievalControls(
            onMenuClick = { showSettingsSheet = true },
            onProgressClick = { showProgressSheet = true },
            modifier = Modifier.align(Alignment.BottomCenter)
        )

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

        if (showProgressSheet) {
            ProgressSheet(
                state = state,
                onResetRosary = { viewModel.resetRosary() },
                onDismiss = { showProgressSheet = false }
            )
        }

        if (showSettingsSheet) {
            SettingsSheet(
                settings = settings,
                onUpdateSettings = { viewModel.updateSettings(it) },
                onDismiss = { showSettingsSheet = false }
            )
        }
    }
}
