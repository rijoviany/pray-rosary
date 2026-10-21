package com.trivium.rosarium.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Prayer(
    val id: String,
    val title: String,
    val phrases: List<String>
) {
    val fullText: String
        get() = phrases.joinToString(" ")
}

enum class PrayerType(val id: String) {
    OUR_FATHER("our_father"),
    HAIL_MARY("hail_mary"),
    GLORY_BE("glory_be"),
    FATIMA("fatima"),
    HAIL_HOLY_QUEEN("hail_holy_queen");

    companion object {
        fun fromId(id: String): PrayerType = entries.find { it.id == id } ?: HAIL_MARY
    }
}
