package com.trivium.rosarium.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.trivium.rosarium.audio.RosaryAudioHelper
import com.trivium.rosarium.data.model.DecadeStep
import com.trivium.rosarium.data.model.MysteryCatalog
import com.trivium.rosarium.data.model.MysterySet
import com.trivium.rosarium.data.model.Prayer
import com.trivium.rosarium.data.model.PrayerStats
import com.trivium.rosarium.data.model.RosaryState
import com.trivium.rosarium.data.model.SettingsState
import com.trivium.rosarium.data.repository.PreferencesRepository
import com.trivium.rosarium.data.repository.RosaryRepository
import com.trivium.rosarium.physics.PhysicsConstants
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
    val audioHelper = RosaryAudioHelper(application)

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
            val saved = preferencesRepository.progressFlow.first()
            if (saved.mysteryId != null) {
                val catalog = rosaryRepository.getMysteryCatalog(_settingsState.value.language)
                val set = catalog.mysterySets.find { it.id == saved.mysteryId }
                if (set != null) {
                    _rosaryState.update {
                        it.copy(
                            currentMysterySet = set,
                            decadeIndex = saved.decadeIndex.coerceIn(0, 4),
                            completedHailMarysInDecade = saved.beadCount.coerceIn(0, 10),
                            currentStep = if (saved.beadCount == 0) DecadeStep.OUR_FATHER else DecadeStep.HAIL_MARY,
                            isOurFatherCompleted = saved.beadCount > 0
                        )
                    }
                    updateActivePrayer()
                }
            }
        }
    }

    fun getMysteryCatalog(): MysteryCatalog {
        return rosaryRepository.getMysteryCatalog(_settingsState.value.language)
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
        _rosaryState.update {
            it.copy(
                decadeIndex = index.coerceIn(0, 4),
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

    // --- Hold to Pray Interaction ---

    fun startPrayer() {
        if (_rosaryState.value.isPrayingActive || _rosaryState.value.isSpringSnapping) return

        prayerJob?.cancel()
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

        val totalDurationMs = _settingsState.value.prayerDurationSeconds * 1000L
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

            // Finished prayer duration: trigger snap-up animation
            _rosaryState.update {
                it.copy(
                    isPrayingActive = false,
                    isSpringSnapping = true,
                    prayerProgress = 1f
                )
            }

            if (_settingsState.value.soundEffectsEnabled) {
                audioHelper.playCompletionBell()
            }
        }
    }

    fun cancelPrayerEarly() {
        prayerJob?.cancel()
        _rosaryState.update {
            it.copy(
                isPrayingActive = false,
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
                val nextBead = currentState.completedHailMarysInDecade + 1
                if (nextBead >= 10) {
                    _rosaryState.update {
                        it.copy(
                            completedHailMarysInDecade = 10,
                            currentStep = DecadeStep.GLORY_BE,
                            justCompletedBeadIndex = 9
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
                if (_settingsState.value.includeFatimaPrayer) {
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
        val nextDecade = _rosaryState.value.decadeIndex + 1
        if (nextDecade < 5) {
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
            }
            _rosaryState.update {
                it.copy(
                    isRosaryFinished = true,
                    currentStep = DecadeStep.DECADE_FINISHED
                )
            }
        }
    }

    private fun updateActivePrayer() {
        val prayers = rosaryRepository.getPrayers(_settingsState.value.language)
        val prayerKey = when (_rosaryState.value.currentStep) {
            DecadeStep.OUR_FATHER -> "our_father"
            DecadeStep.HAIL_MARY -> "hail_mary"
            DecadeStep.GLORY_BE -> "glory_be"
            DecadeStep.FATIMA -> "fatima"
            DecadeStep.DECADE_FINISHED -> "hail_holy_queen"
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
                beadCount = state.completedHailMarysInDecade
            )
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioHelper.release()
    }
}
