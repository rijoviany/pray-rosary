package com.rosarium.app.data.repository

import android.content.Context
import com.rosarium.app.R
import com.rosarium.app.data.model.MysteryCatalog
import com.rosarium.app.data.model.MysterySet
import com.rosarium.app.data.model.Prayer
import kotlinx.serialization.json.Json
import java.io.InputStreamReader
import java.util.Calendar

class RosaryRepository(private val context: Context) {

    private val jsonParser = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    private var englishMysteries: MysteryCatalog? = null
    private var malayalamMysteries: MysteryCatalog? = null

    private var englishPrayers: Map<String, Prayer>? = null
    private var malayalamPrayers: Map<String, Prayer>? = null

    fun getMysteryCatalog(language: String): MysteryCatalog {
        return if (language == "ml") {
            if (malayalamMysteries == null) {
                malayalamMysteries = loadMysteryCatalog(R.raw.mysteries_ml)
            }
            malayalamMysteries ?: loadMysteryCatalog(R.raw.mysteries_en)
        } else {
            if (englishMysteries == null) {
                englishMysteries = loadMysteryCatalog(R.raw.mysteries_en)
            }
            englishMysteries!!
        }
    }

    fun getPrayers(language: String): Map<String, Prayer> {
        return if (language == "ml") {
            if (malayalamPrayers == null) {
                malayalamPrayers = loadPrayers(R.raw.prayers_ml)
            }
            malayalamPrayers ?: loadPrayers(R.raw.prayers_en)
        } else {
            if (englishPrayers == null) {
                englishPrayers = loadPrayers(R.raw.prayers_en)
            }
            englishPrayers!!
        }
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
        val mapSerializer = kotlinx.serialization.builtins.MapSerializer(
            kotlinx.serialization.builtins.serializer(String::class),
            Prayer.serializer()
        )
        return jsonParser.decodeFromString(mapSerializer, text)
    }
}
