package com.rosarium.app.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.rosarium.app.data.model.SettingsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "rosarium_preferences")

class PreferencesRepository(private val context: Context) {

    private object PreferencesKeys {
        val KEY_SAVED_MYSTERY_ID = stringPreferencesKey("saved_mystery_id")
        val KEY_SAVED_DECADE_INDEX = intPreferencesKey("saved_decade_index")
        val KEY_SAVED_BEAD_COUNT = intPreferencesKey("saved_bead_count")

        val KEY_LANGUAGE = stringPreferencesKey("language")
        val KEY_TAP_TO_PRAY = booleanPreferencesKey("tap_to_pray")
        val KEY_SOUND_EFFECTS = booleanPreferencesKey("sound_effects")
        val KEY_AMBIENT_CHANT = booleanPreferencesKey("ambient_chant")
        val KEY_HAPTICS = booleanPreferencesKey("haptics")
        val KEY_FATIMA = booleanPreferencesKey("include_fatima")
        val KEY_PRAYER_DURATION = intPreferencesKey("prayer_duration")
        val KEY_FONT_SCALE = floatPreferencesKey("font_scale")
    }

    val settingsFlow: Flow<SettingsState> = context.dataStore.data.map { prefs ->
        SettingsState(
            language = prefs[PreferencesKeys.KEY_LANGUAGE] ?: "en",
            tapToPrayEnabled = prefs[PreferencesKeys.KEY_TAP_TO_PRAY] ?: false,
            soundEffectsEnabled = prefs[PreferencesKeys.KEY_SOUND_EFFECTS] ?: true,
            ambientChantEnabled = prefs[PreferencesKeys.KEY_AMBIENT_CHANT] ?: false,
            hapticFeedbackEnabled = prefs[PreferencesKeys.KEY_HAPTICS] ?: true,
            includeFatimaPrayer = prefs[PreferencesKeys.KEY_FATIMA] ?: true,
            prayerDurationSeconds = prefs[PreferencesKeys.KEY_PRAYER_DURATION] ?: 16,
            fontScale = prefs[PreferencesKeys.KEY_FONT_SCALE] ?: 1.0f
        )
    }

    data class SavedProgress(
        val mysteryId: String?,
        val decadeIndex: Int,
        val beadCount: Int
    )

    val progressFlow: Flow<SavedProgress> = context.dataStore.data.map { prefs ->
        SavedProgress(
            mysteryId = prefs[PreferencesKeys.KEY_SAVED_MYSTERY_ID],
            decadeIndex = prefs[PreferencesKeys.KEY_SAVED_DECADE_INDEX] ?: 0,
            beadCount = prefs[PreferencesKeys.KEY_SAVED_BEAD_COUNT] ?: 0
        )
    }

    suspend fun saveProgress(mysteryId: String, decadeIndex: Int, beadCount: Int) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.KEY_SAVED_MYSTERY_ID] = mysteryId
            prefs[PreferencesKeys.KEY_SAVED_DECADE_INDEX] = decadeIndex
            prefs[PreferencesKeys.KEY_SAVED_BEAD_COUNT] = beadCount
        }
    }

    suspend fun updateSettings(settings: SettingsState) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.KEY_LANGUAGE] = settings.language
            prefs[PreferencesKeys.KEY_TAP_TO_PRAY] = settings.tapToPrayEnabled
            prefs[PreferencesKeys.KEY_SOUND_EFFECTS] = settings.soundEffectsEnabled
            prefs[PreferencesKeys.KEY_AMBIENT_CHANT] = settings.ambientChantEnabled
            prefs[PreferencesKeys.KEY_HAPTICS] = settings.hapticFeedbackEnabled
            prefs[PreferencesKeys.KEY_FATIMA] = settings.includeFatimaPrayer
            prefs[PreferencesKeys.KEY_PRAYER_DURATION] = settings.prayerDurationSeconds
            prefs[PreferencesKeys.KEY_FONT_SCALE] = settings.fontScale
        }
    }
}
