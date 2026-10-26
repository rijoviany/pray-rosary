package com.trivium.rosarium.data.model

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
data class NovenaDay(
    val dayNumber: Int,
    val theme: String,
    val scriptureReference: String,
    val scriptureText: String,
    val intention: String,
    val meditation: String,
    val prayer: List<String>
) {
    val fullPrayerText: String
        get() = prayer.joinToString(" ")
}

@Serializable
data class Novena(
    val id: String,
    val title: String,
    val subtitle: String,
    val patron: String,
    val feastDay: String,
    val description: String,
    val colorHex: String = "#B45309",
    val days: List<NovenaDay>
) {
    val themeColor: Color
        get() = try {
            Color(android.graphics.Color.parseColor(colorHex))
        } catch (e: Exception) {
            Color(0xFFB45309)
        }
}

@Serializable
data class NovenaProgress(
    val novenaId: String,
    val currentDay: Int = 1,
    val completedDays: List<Int> = emptyList(),
    val lastCompletedDate: String = "",
    val isCompleted: Boolean = false
) {
    val percentComplete: Float
        get() = (completedDays.size.toFloat() / 9f).coerceIn(0f, 1f)

    fun isDayCompleted(day: Int): Boolean = completedDays.contains(day)
}
