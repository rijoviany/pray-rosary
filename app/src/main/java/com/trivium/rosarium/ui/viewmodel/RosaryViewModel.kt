package com.trivium.rosarium.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.trivium.rosarium.audio.RosaryAudioHelper
import com.trivium.rosarium.data.model.DecadeStep
import com.trivium.rosarium.data.model.FamilyGroup
import com.trivium.rosarium.data.model.MysteryCatalog
import com.trivium.rosarium.data.model.MysterySet
import com.trivium.rosarium.data.model.Novena
import com.trivium.rosarium.data.model.NovenaProgress
import com.trivium.rosarium.data.model.PendingInvite
import com.trivium.rosarium.data.model.Prayer
import com.trivium.rosarium.data.model.PrayerStats
import com.trivium.rosarium.data.model.RosaryState
import com.trivium.rosarium.data.model.SettingsState
import com.trivium.rosarium.data.repository.FamilyRepository
import com.trivium.rosarium.data.repository.NovenaRepository
import com.trivium.rosarium.data.repository.PreferencesRepository
import com.trivium.rosarium.data.repository.RosaryRepository
import com.trivium.rosarium.physics.PhysicsConstants
import com.trivium.rosarium.update.InAppUpdateManager
import com.trivium.rosarium.update.UpdateStatus
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RosaryViewModel(application: Application) : AndroidViewModel(application) {

    private val rosaryRepository = RosaryRepository(application)
    private val preferencesRepository = PreferencesRepository(application)
    private val novenaRepository = NovenaRepository()
    val familyRepository = FamilyRepository(application)
    val audioHelper = RosaryAudioHelper(application)
    val updateManager = InAppUpdateManager(application)

    val updateStatus: StateFlow<UpdateStatus> = updateManager.updateStatus
    val familyState: StateFlow<FamilyGroup?> = familyRepository.currentFamily
    val pendingInviteState: StateFlow<PendingInvite?> = familyRepository.pendingInvite

    val allNovenas: List<Novena> = novenaRepository.getAllNovenas()

    private val _novenaProgressMap = MutableStateFlow<Map<String, NovenaProgress>>(emptyMap())
    val novenaProgressMap: StateFlow<Map<String, NovenaProgress>> = _novenaProgressMap.asStateFlow()

    private val _savedProgress = MutableStateFlow<PreferencesRepository.SavedProgress?>(null)
    val savedProgress: StateFlow<PreferencesRepository.SavedProgress?> = _savedProgress.asStateFlow()

    private val _lastActiveNovena = MutableStateFlow<Pair<String?, Int>>(Pair(null, 1))
    val lastActiveNovena: StateFlow<Pair<String?, Int>> = _lastActiveNovena.asStateFlow()

    private val _settingsState = MutableStateFlow(SettingsState())
    val settingsState: StateFlow<SettingsState> = _settingsState.asStateFlow()

    private val _statsState = MutableStateFlow(PrayerStats())
    val statsState: StateFlow<PrayerStats> = _statsState.asStateFlow()

    private val _rosaryState: MutableStateFlow<RosaryState>
    val rosaryState: StateFlow<RosaryState>

    private var prayerJob: Job? = null
    private var lastHapticTime = 0L

    init {
        val initialSettings = SettingsState()
        val defaultMysterySet = rosaryRepository.getDefaultMysterySet(initialSettings.language)
        val initialPrayers = rosaryRepository.getPrayers(initialSettings.language)

        _rosaryState = MutableStateFlow(
            RosaryState(
                currentMysterySet = defaultMysterySet,
                activePrayer = initialPrayers["our_father"]
            )
        )
        rosaryState = _rosaryState.asStateFlow()

        // Load saved preferences and state
        viewModelScope.launch {
            preferencesRepository.settingsFlow.collect { settings ->
                _settingsState.value = settings
                reloadPrayersAndMysteries(settings.language)
            }
        }

        viewModelScope.launch {
            preferencesRepository.statsFlow.collect { stats ->
                _statsState.value = stats
            }
        }

        viewModelScope.launch {
            preferencesRepository.novenaProgressFlow.collect { progressMap ->
                _novenaProgressMap.value = progressMap
            }
        }

        viewModelScope.launch {
            preferencesRepository.progressFlow.collect { saved ->
                _savedProgress.value = saved
            }
        }

        viewModelScope.launch {
            preferencesRepository.lastActiveNovenaFlow.collect { lastNovena ->
                _lastActiveNovena.value = lastNovena
            }
        }

        viewModelScope.launch {
            val saved = preferencesRepository.progressFlow.first()
            if (saved.mysteryId != null && !saved.isFinished) {
                val catalog = rosaryRepository.getMysteryCatalog(_settingsState.value.language)
                val set = catalog.mysterySets.find { it.id == saved.mysteryId }
                if (set != null) {
                    val restoredStep = try {
                        DecadeStep.valueOf(saved.step)
                    } catch (_: Exception) {
                        if (saved.beadCount == 0) DecadeStep.OUR_FATHER else DecadeStep.HAIL_MARY
                    }
                    val maxDecade = (set.mysteries.size - 1).coerceAtLeast(0)
                    _rosaryState.update {
                        it.copy(
                            currentMysterySet = set,
                            decadeIndex = saved.decadeIndex.coerceIn(0, maxDecade),
                            completedHailMarysInDecade = saved.beadCount.coerceIn(0, 10),
                            currentStep = restoredStep,
                            isOurFatherCompleted = saved.beadCount > 0 || restoredStep != DecadeStep.OUR_FATHER,
                            isGloryBeCompleted = restoredStep == DecadeStep.FATIMA || restoredStep == DecadeStep.DECADE_FINISHED,
                            isFatimaCompleted = restoredStep == DecadeStep.DECADE_FINISHED,
                            isRosaryFinished = false
                        )
                    }
                    updateActivePrayer()
                }
            }
        }

        // Check for updates on startup
        updateManager.checkForUpdate()
    }

    fun getMysteryCatalog(): MysteryCatalog {
        return rosaryRepository.getMysteryCatalog(_settingsState.value.language)
    }

    fun getPrayers(language: String = _settingsState.value.language): List<Prayer> {
        return rosaryRepository.getPrayers(language).values.toList()
    }

    fun getTodayMysterySetId(): String {
        return rosaryRepository.getTodayMysterySetId()
    }

    fun selectMysterySet(set: MysterySet) {
        _rosaryState.update {
            it.copy(
                currentMysterySet = set,
                decadeIndex = 0,
                currentStep = DecadeStep.OUR_FATHER,
                completedHailMarysInDecade = 0,
                isOurFatherCompleted = false,
                isGloryBeCompleted = false,
                isFatimaCompleted = false,
                isRosaryFinished = false
            )
        }
        updateActivePrayer()
        persistCurrentProgress()
    }

    fun selectDecade(index: Int) {
        val maxIndex = (_rosaryState.value.currentMysterySet.mysteries.size - 1).coerceAtLeast(0)
        _rosaryState.update {
            it.copy(
                decadeIndex = index.coerceIn(0, maxIndex),
                currentStep = DecadeStep.OUR_FATHER,
                completedHailMarysInDecade = 0,
                isOurFatherCompleted = false,
                isGloryBeCompleted = false,
                isFatimaCompleted = false
            )
        }
        updateActivePrayer()
        persistCurrentProgress()
    }

    fun resetRosary() {
        val todaySet = rosaryRepository.getDefaultMysterySet(_settingsState.value.language)
        selectMysterySet(todaySet)
    }

    fun updateSettings(newSettings: SettingsState) {
        viewModelScope.launch {
            preferencesRepository.updateSettings(newSettings)
        }
    }

    fun recordNovenaOpened(novenaId: String, dayNumber: Int) {
        viewModelScope.launch {
            preferencesRepository.saveLastActiveNovena(novenaId, dayNumber)
        }
    }

    fun markNovenaDayCompleted(novenaId: String, dayNumber: Int) {
        viewModelScope.launch {
            preferencesRepository.saveNovenaDayCompleted(novenaId, dayNumber)
            val nextDay = if (dayNumber < 9) dayNumber + 1 else 9
            preferencesRepository.saveLastActiveNovena(novenaId, nextDay)
            if (_settingsState.value.soundEffectsEnabled) {
                audioHelper.playPaperTear()
                delay(120)
                audioHelper.playCompletionBell()
            }
            if (_settingsState.value.hapticFeedbackEnabled) {
                audioHelper.performPaperTearHaptic()
            }
        }
    }

    fun resetNovena(novenaId: String) {
        viewModelScope.launch {
            preferencesRepository.resetNovena(novenaId)
            preferencesRepository.saveLastActiveNovena(novenaId, 1)
        }
    }

    // --- Hold to Pray Interaction ---

    fun startPrayer() {
        if (_rosaryState.value.isPrayingActive || _rosaryState.value.isSpringSnapping) return

        prayerJob?.cancel()
        lastHapticTime = 0L
        _rosaryState.update {
            it.copy(
                isPrayingActive = true,
                prayerProgress = 0f,
                currentPhraseIndex = 0
            )
        }

        if (_settingsState.value.hapticFeedbackEnabled) {
            audioHelper.performHapticTick()
        }

        val baseSpeedSeconds = _settingsState.value.prayerDurationSeconds
        val totalDurationMs = when (_rosaryState.value.currentStep) {
            DecadeStep.OUR_FATHER -> maxOf(PhysicsConstants.MIN_OUR_FATHER_DURATION_MS, baseSpeedSeconds * 2000L)
            DecadeStep.HAIL_MARY -> maxOf(PhysicsConstants.MIN_HAIL_MARY_DURATION_MS, baseSpeedSeconds * 1000L)
            DecadeStep.GLORY_BE -> PhysicsConstants.GLORY_BE_DURATION_MS // Reduced to 2s
            DecadeStep.FATIMA -> maxOf(6_000L, (baseSpeedSeconds * 1200L))
            else -> maxOf(8_000L, baseSpeedSeconds * 1500L)
        }
        val tickInterval = PhysicsConstants.PROGRESS_TICK_INTERVAL_MS

        prayerJob = viewModelScope.launch {
            var elapsed = 0L
            val phrasesCount = _rosaryState.value.activePrayer?.phrases?.size ?: 1

            while (elapsed < totalDurationMs) {
                delay(tickInterval)
                elapsed += tickInterval

                val progress = (elapsed.toFloat() / totalDurationMs).coerceIn(0f, 1f)
                val phraseIndex = ((progress * phrasesCount).toInt()).coerceIn(0, phrasesCount - 1)

                _rosaryState.update {
                    it.copy(
                        prayerProgress = progress,
                        currentPhraseIndex = phraseIndex
                    )
                }

                // Subtle rhythmic haptic tick every ~1.2 seconds
                if (elapsed - lastHapticTime >= PhysicsConstants.HAPTIC_PULSE_INTERVAL_MS) {
                    lastHapticTime = elapsed
                    if (_settingsState.value.hapticFeedbackEnabled) {
                        audioHelper.performHapticPulse()
                    }
                }
            }

            // Duration completed (5s for Hail Mary / 10s for Our Father / 2s for Glory Be):
            // Ready for user to release the bead! No automatic snap!
            _rosaryState.update {
                it.copy(
                    isReadyToRelease = true,
                    prayerProgress = 1f,
                    currentPhraseIndex = phrasesCount - 1
                )
            }

            // Tiny haptic notifying user that it is time to release the bead
            if (_settingsState.value.hapticFeedbackEnabled) {
                audioHelper.performReleaseReadyHaptic()
            }

            // If in Tap-to-Pray mode, automatically release after completion duration
            if (_settingsState.value.tapToPrayEnabled) {
                delay(150L)
                onReleaseBead()
            }
        }
    }

    fun onReleaseBead() {
        val state = _rosaryState.value
        if (state.isReadyToRelease || state.prayerProgress >= 0.95f) {
            prayerJob?.cancel()
            _rosaryState.update {
                it.copy(
                    isPrayingActive = false,
                    isSpringSnapping = true,
                    isReadyToRelease = false,
                    prayerProgress = 1f
                )
            }
            if (_settingsState.value.soundEffectsEnabled) {
                audioHelper.playCompletionBell()
            }
        } else {
            // User released too early (< 5s / 10s)
            cancelPrayerEarly()
        }
    }

    fun cancelPrayerEarly() {
        prayerJob?.cancel()
        _rosaryState.update {
            it.copy(
                isPrayingActive = false,
                isReadyToRelease = false,
                prayerProgress = 0f,
                currentPhraseIndex = 0
            )
        }
    }

    fun onSnapUpAnimationFinished() {
        _rosaryState.update { it.copy(isSpringSnapping = false) }
        advanceRosaryStep()
    }

    fun onBounceImpact(decayLevel: Float) {
        if (_settingsState.value.soundEffectsEnabled) {
            audioHelper.playBeadClack(decayLevel)
        }
        if (_settingsState.value.hapticFeedbackEnabled) {
            audioHelper.performImpactClack(decayLevel)
        }
    }

    fun onStringTension() {
        if (_settingsState.value.soundEffectsEnabled) {
            audioHelper.playStringTension(0.6f)
        }
    }

    private fun advanceRosaryStep() {
        val currentState = _rosaryState.value

        when (currentState.currentStep) {
            DecadeStep.OUR_FATHER -> {
                _rosaryState.update {
                    it.copy(
                        isOurFatherCompleted = true,
                        currentStep = DecadeStep.HAIL_MARY,
                        justCompletedBeadIndex = -1
                    )
                }
            }
            DecadeStep.HAIL_MARY -> {
                val targetCount = currentState.currentMysterySet.effectiveBeadsPerDecade
                val nextBead = currentState.completedHailMarysInDecade + 1
                if (nextBead >= targetCount) {
                    _rosaryState.update {
                        it.copy(
                            completedHailMarysInDecade = targetCount,
                            currentStep = DecadeStep.GLORY_BE,
                            justCompletedBeadIndex = targetCount - 1
                        )
                    }
                } else {
                    _rosaryState.update {
                        it.copy(
                            completedHailMarysInDecade = nextBead,
                            justCompletedBeadIndex = nextBead - 1
                        )
                    }
                }
            }
            DecadeStep.GLORY_BE -> {
                val mysteryId = currentState.currentMysterySet.id
                if (mysteryId == "divine_mercy") {
                    _rosaryState.update {
                        it.copy(
                            isGloryBeCompleted = true,
                            currentStep = DecadeStep.FATIMA
                        )
                    }
                } else if (_settingsState.value.includeFatimaPrayer && !currentState.currentMysterySet.isChaplet) {
                    _rosaryState.update {
                        it.copy(
                            isGloryBeCompleted = true,
                            currentStep = DecadeStep.FATIMA
                        )
                    }
                } else {
                    onDecadeFinished()
                }
            }
            DecadeStep.FATIMA -> {
                _rosaryState.update { it.copy(isFatimaCompleted = true) }
                onDecadeFinished()
            }
            DecadeStep.DECADE_FINISHED -> {
                // Already transitioning
            }
        }

        updateActivePrayer()
        persistCurrentProgress()
        viewModelScope.launch {
            preferencesRepository.recordPrayerCompleted()
        }
    }

    private fun onDecadeFinished() {
        viewModelScope.launch {
            preferencesRepository.recordDecadeCompleted()
        }
        val totalGroups = _rosaryState.value.currentMysterySet.mysteries.size.coerceAtLeast(1)
        val nextDecade = _rosaryState.value.decadeIndex + 1
        if (nextDecade < totalGroups) {
            _rosaryState.update {
                it.copy(
                    decadeIndex = nextDecade,
                    currentStep = DecadeStep.OUR_FATHER,
                    completedHailMarysInDecade = 0,
                    isOurFatherCompleted = false,
                    isGloryBeCompleted = false,
                    isFatimaCompleted = false,
                    justCompletedBeadIndex = null
                )
            }
        } else {
            // Full Rosary completed! Conclude with Hail Holy Queen
            viewModelScope.launch {
                preferencesRepository.recordRosaryCompleted()
                familyRepository.recordRosaryForFamily()
            }
            _rosaryState.update {
                it.copy(
                    isRosaryFinished = true,
                    currentStep = DecadeStep.DECADE_FINISHED
                )
            }
            persistCurrentProgress()
        }
    }

    private fun updateActivePrayer() {
        val prayers = rosaryRepository.getPrayers(_settingsState.value.language)
        val mysteryId = _rosaryState.value.currentMysterySet.id
        val prayerKey = when (_rosaryState.value.currentStep) {
            DecadeStep.OUR_FATHER -> when (mysteryId) {
                "divine_mercy" -> "eternal_father"
                "sacred_heart" -> "sacred_heart_large"
                "holy_wounds" -> "holy_wounds_father"
                else -> "our_father"
            }
            DecadeStep.HAIL_MARY -> when (mysteryId) {
                "divine_mercy" -> "sorrowful_passion"
                "saint_joseph" -> "hail_joseph"
                "sacred_heart" -> "sacred_heart_small"
                "holy_souls" -> "requiem_aeternam"
                "holy_wounds" -> "holy_wounds_bead"
                "precious_blood" -> "precious_blood_bead"
                "saint_anne" -> "saint_anne_bead"
                else -> "hail_mary"
            }
            DecadeStep.GLORY_BE -> when (mysteryId) {
                "holy_souls" -> "requiem_aeternam"
                else -> "glory_be"
            }
            DecadeStep.FATIMA -> when (mysteryId) {
                "divine_mercy" -> "holy_god"
                else -> "fatima"
            }
            DecadeStep.DECADE_FINISHED -> when (mysteryId) {
                "divine_mercy" -> "holy_god"
                "holy_souls" -> "requiem_aeternam"
                else -> "hail_holy_queen"
            }
        }
        _rosaryState.update {
            it.copy(activePrayer = prayers[prayerKey] ?: prayers["hail_mary"])
        }
    }

    private fun reloadPrayersAndMysteries(language: String) {
        val catalog = rosaryRepository.getMysteryCatalog(language)
        val currentId = _rosaryState.value.currentMysterySet.id
        val updatedSet = catalog.mysterySets.find { it.id == currentId } ?: catalog.mysterySets.first()

        _rosaryState.update {
            it.copy(currentMysterySet = updatedSet)
        }
        updateActivePrayer()
    }

    private fun persistCurrentProgress() {
        val state = _rosaryState.value
        viewModelScope.launch {
            preferencesRepository.saveProgress(
                mysteryId = state.currentMysterySet.id,
                decadeIndex = state.decadeIndex,
                beadCount = state.completedHailMarysInDecade,
                step = state.currentStep.name,
                isFinished = state.isRosaryFinished
            )
        }
    }

    // --- Family / Prayer Circle Management ---

    fun createFamily(familyName: String, creatorName: String) {
        viewModelScope.launch {
            familyRepository.createFamily(familyName, creatorName)
        }
    }

    fun joinFamilyByCode(inviteCode: String, memberName: String = "", onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val result = familyRepository.joinFamilyByCode(inviteCode, memberName)
            onResult(result.isSuccess)
        }
    }

    fun leaveFamily() {
        viewModelScope.launch {
            familyRepository.leaveFamily()
        }
    }

    fun updateFamilyIntention(intention: String) {
        viewModelScope.launch {
            familyRepository.updateFamilyIntention(intention)
        }
    }

    fun handleIncomingInviteCode(inviteCode: String) {
        if (inviteCode.isBlank()) return
        viewModelScope.launch {
            val resolved = familyRepository.resolveInvite(inviteCode)
            if (resolved != null) {
                familyRepository.setPendingInvite(resolved)
            }
        }
    }

    fun acceptPendingInvite(memberName: String = "") {
        val pending = pendingInviteState.value ?: return
        viewModelScope.launch {
            familyRepository.joinFamilyByCode(pending.inviteCode, memberName)
        }
    }

    fun declinePendingInvite() {
        familyRepository.setPendingInvite(null)
    }

    // In-App Updates
    fun checkForUpdate(onResult: (Boolean, com.google.android.play.core.appupdate.AppUpdateInfo?) -> Unit = { _, _ -> }) {
        updateManager.checkForUpdate(onResult)
    }

    fun dismissUpdatePrompt() {
        updateManager.dismissPrompt()
    }

    fun startUpdate(
        activity: android.app.Activity,
        launcher: androidx.activity.result.ActivityResultLauncher<androidx.activity.result.IntentSenderRequest>? = null
    ) {
        updateManager.startUpdate(activity, launcher)
    }

    fun completeFlexibleUpdate() {
        updateManager.completeFlexibleUpdate()
    }

    override fun onCleared() {
        super.onCleared()
        audioHelper.release()
        updateManager.unregister()
    }
}
