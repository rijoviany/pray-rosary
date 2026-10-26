package com.trivium.rosarium.data.repository

import android.content.Context
import com.trivium.rosarium.R
import com.trivium.rosarium.data.model.MysteryCatalog
import com.trivium.rosarium.data.model.MysterySet
import com.trivium.rosarium.data.model.Prayer
import kotlinx.serialization.json.Json
import java.io.InputStreamReader
import java.util.Calendar

class RosaryRepository(private val context: Context) {

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private val mysteryCache = mutableMapOf<String, MysteryCatalog>()
    private val prayerCache = mutableMapOf<String, Map<String, Prayer>>()

    fun getMysteryCatalog(language: String): MysteryCatalog {
        mysteryCache[language]?.let { return it }

        val resName = "mysteries_$language"
        var resId = context.resources.getIdentifier(resName, "raw", context.packageName)
        if (resId == 0) {
            resId = R.raw.mysteries_en
        }

        var catalog = loadMysteryCatalog(resId)
        val chapletIds = setOf("saint_michael", "divine_mercy", "saint_joseph", "seven_sorrows", "sacred_heart")
        val missingChapletIds = chapletIds.filter { chapletId -> catalog.mysterySets.none { it.id == chapletId } }
        if (missingChapletIds.isNotEmpty()) {
            val enCatalog = loadMysteryCatalog(R.raw.mysteries_en)
            val missingSets = enCatalog.mysterySets.filter { it.id in missingChapletIds }
            catalog = catalog.copy(mysterySets = catalog.mysterySets + missingSets)
        }
        mysteryCache[language] = catalog
        return catalog
    }

    fun getPrayers(language: String): Map<String, Prayer> {
        prayerCache[language]?.let { return it }

        val resName = "prayers_$language"
        var resId = context.resources.getIdentifier(resName, "raw", context.packageName)
        if (resId == 0) {
            resId = R.raw.prayers_en
        }

        val prayers = loadPrayers(resId).toMutableMap()
        if (resId != R.raw.prayers_en) {
            val enPrayers = loadPrayers(R.raw.prayers_en)
            for ((key, prayer) in enPrayers) {
                if (!prayers.containsKey(key)) {
                    prayers[key] = prayer
                }
            }
        }
        prayerCache[language] = prayers
        return prayers
    }

    /**
     * Determines today's traditional Mystery set according to weekday:
     * Monday & Saturday: Joyful
     * Tuesday & Friday: Sorrowful
     * Wednesday & Sunday: Glorious
     * Thursday: Luminous
     */
    fun getTodayMysterySetId(): String {
        val calendar = Calendar.getInstance()
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY, Calendar.SATURDAY -> "joyful"
            Calendar.TUESDAY, Calendar.FRIDAY -> "sorrowful"
            Calendar.THURSDAY -> "luminous"
            Calendar.WEDNESDAY, Calendar.SUNDAY -> "glorious"
            else -> "joyful"
        }
    }

    fun getDefaultMysterySet(language: String): MysterySet {
        val catalog = getMysteryCatalog(language)
        val todayId = getTodayMysterySetId()
        return catalog.mysterySets.find { it.id == todayId } ?: catalog.mysterySets.first()
    }

    private fun loadMysteryCatalog(resId: Int): MysteryCatalog {
        val inputStream = context.resources.openRawResource(resId)
        val text = InputStreamReader(inputStream).use { it.readText() }
        return jsonParser.decodeFromString(MysteryCatalog.serializer(), text)
    }

    private fun loadPrayers(resId: Int): Map<String, Prayer> {
        val inputStream = context.resources.openRawResource(resId)
        val text = InputStreamReader(inputStream).use { it.readText() }
        return jsonParser.decodeFromString<Map<String, Prayer>>(text)
    }
}
