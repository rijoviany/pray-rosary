package com.rosarium.app.data.model

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class PrayerStats(
    val totalPrayers: Int = 0,
    val totalDecades: Int = 0,
    val totalRosaries: Int = 0,
    val historyMap: Map<String, Int> = emptyMap() // "yyyy-MM-dd" -> count
) {
    /**
     * Calculates the current consecutive days of prayer streak.
     */
    fun calculateStreak(): Int {
        val today = LocalDate.now()
        val formatter = DateTimeFormatter.ISO_LOCAL_DATE

        val todayKey = today.format(formatter)
        val yesterdayKey = today.minusDays(1).format(formatter)

        // If prayed neither today nor yesterday, streak is broken
        if (historyMap.getOrDefault(todayKey, 0) == 0 && historyMap.getOrDefault(yesterdayKey, 0) == 0) {
            return 0
        }

        var streak = 0
        var current = if (historyMap.getOrDefault(todayKey, 0) > 0) today else today.minusDays(1)

        while (historyMap.getOrDefault(current.format(formatter), 0) > 0) {
            streak++
            current = current.minusDays(1)
        }

        return streak
    }

    /**
     * Returns the prayer count for a specific date (yyyy-MM-dd).
     */
    fun getCountForDate(date: LocalDate): Int {
        val key = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
        return historyMap.getOrDefault(key, 0)
    }

    /**
     * Maps prayer count on a day to a medieval heatmap intensity level (0 to 4):
     * 0: Empty
     * 1: 1-9 prayers (few beads)
     * 2: 10-24 prayers (1-2 decades)
     * 3: 25-52 prayers (several decades)
     * 4: 53+ prayers (1 full Rosary or more)
     */
    fun getIntensityLevel(count: Int): Int {
        return when {
            count <= 0 -> 0
            count < 10 -> 1
            count < 25 -> 2
            count < 53 -> 3
            else -> 4
        }
    }
}
