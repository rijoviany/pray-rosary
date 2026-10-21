package com.trivium.rosarium.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.trivium.rosarium.data.model.PrayerStats
import com.trivium.rosarium.data.model.SettingsState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val Context.dataStore by preferencesDataStore(name = "rosarium_preferences")

class PreferencesRepository(private val context: Context) {

    private val json = Json { ignoreUnknownKeys = true }

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

        // Stats & Heatmap History
        val KEY_TOTAL_PRAYERS = intPreferencesKey("total_prayers_count")
        val KEY_TOTAL_DECADES = intPreferencesKey("total_decades_count")
        val KEY_TOTAL_ROSARIES = intPreferencesKey("total_rosaries_count")
        val KEY_HISTORY_JSON = stringPreferencesKey("prayer_history_json")
    }

    val settingsFlow: Flow<SettingsState> = context.dataStore.data.map { prefs ->
        SettingsState(
            language = prefs[PreferencesKeys.KEY_LANGUAGE] ?: "en",
            tapToPrayEnabled = prefs[PreferencesKeys.KEY_TAP_TO_PRAY] ?: false,
            soundEffectsEnabled = prefs[PreferencesKeys.KEY_SOUND_EFFECTS] ?: true,
            ambientChantEnabled = prefs[PreferencesKeys.KEY_AMBIENT_CHANT] ?: false,
            hapticFeedbackEnabled = prefs[PreferencesKeys.KEY_HAPTICS] ?: true,
            includeFatimaPrayer = prefs[PreferencesKeys.KEY_FATIMA] ?: true,
            prayerDurationSeconds = prefs[PreferencesKeys.KEY_PRAYER_DURATION] ?: 5,
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

    val statsFlow: Flow<PrayerStats> = context.dataStore.data.map { prefs ->
        val jsonString = prefs[PreferencesKeys.KEY_HISTORY_JSON] ?: "{}"
        val historyMap: Map<String, Int> = try {
            json.decodeFromString(jsonString)
        } catch (e: Exception) {
            emptyMap()
        }
        PrayerStats(
            totalPrayers = prefs[PreferencesKeys.KEY_TOTAL_PRAYERS] ?: 0,
            totalDecades = prefs[PreferencesKeys.KEY_TOTAL_DECADES] ?: 0,
            totalRosaries = prefs[PreferencesKeys.KEY_TOTAL_ROSARIES] ?: 0,
            historyMap = historyMap
        )
    }

    suspend fun saveProgress(mysteryId: String, decadeIndex: Int, beadCount: Int) {
        context.dataStore.edit { prefs ->
            prefs[PreferencesKeys.KEY_SAVED_MYSTERY_ID] = mysteryId
            prefs[PreferencesKeys.KEY_SAVED_DECADE_INDEX] = decadeIndex
            prefs[PreferencesKeys.KEY_SAVED_BEAD_COUNT] = beadCount
        }
    }

    suspend fun recordPrayerCompleted() {
        val today = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE)
        context.dataStore.edit { prefs ->
            val currentTotal = prefs[PreferencesKeys.KEY_TOTAL_PRAYERS] ?: 0
            prefs[PreferencesKeys.KEY_TOTAL_PRAYERS] = currentTotal + 1

            val jsonString = prefs[PreferencesKeys.KEY_HISTORY_JSON] ?: "{}"
            val historyMap: MutableMap<String, Int> = try {
                json.decodeFromString<Map<String, Int>>(jsonString).toMutableMap()
            } catch (e: Exception) {
                mutableMapOf()
            }
            val currentDayCount = historyMap.getOrDefault(today, 0)
            historyMap[today] = currentDayCount + 1
            prefs[PreferencesKeys.KEY_HISTORY_JSON] = json.encodeToString(historyMap)
        }
    }

    suspend fun recordDecadeCompleted() {
        context.dataStore.edit { prefs ->
            val currentTotal = prefs[PreferencesKeys.KEY_TOTAL_DECADES] ?: 0
            prefs[PreferencesKeys.KEY_TOTAL_DECADES] = currentTotal + 1
        }
    }

    suspend fun recordRosaryCompleted() {
        context.dataStore.edit { prefs ->
            val currentTotal = prefs[PreferencesKeys.KEY_TOTAL_ROSARIES] ?: 0
            prefs[PreferencesKeys.KEY_TOTAL_ROSARIES] = currentTotal + 1
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
