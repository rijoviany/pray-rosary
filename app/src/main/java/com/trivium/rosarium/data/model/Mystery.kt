package com.trivium.rosarium.data.model

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
data class Mystery(
    val number: Int,
    val title: String,
    val scriptureReference: String,
    val scriptureText: String,
    val meditation: String,
    val fruitOfMystery: String,
    val imageAsset: String
)

@Serializable
data class MysterySet(
    val id: String,
    val name: String,
    val recommendedDays: List<String>,
    val mysteries: List<Mystery>,
    val beadsPerDecade: Int = 10,
    val beadColorHex: String = "#1E40AF"
) {
    val effectiveBeadsPerDecade: Int
        get() = if (id == "saint_michael" || beadsPerDecade == 7) 7 else 10

    val primaryBeadColor: Color
        get() = when (id) {
            "sorrowful" -> Color(0xFF991B1B)      // Passion Ruby Red
            "luminous" -> Color(0xFFD97706)       // Radiant Solar Amber / Topaz
            "glorious" -> Color(0xFF7C3AED)       // Royal Resurrection Amethyst
            "saint_michael" -> Color(0xFFDC2626)  // Archangelic Flame Crimson
            "joyful" -> Color(0xFF1E40AF)         // Marian Lapis Lazuli Blue
            else -> Color(0xFF1E40AF)
        }

    val specularBeadColor: Color
        get() = when (id) {
            "sorrowful" -> Color(0xFFEF4444)      // Bright ruby highlight
            "luminous" -> Color(0xFFFDE047)       // Bright gold amber highlight
            "glorious" -> Color(0xFFA78BFA)       // Bright lavender amethyst highlight
            "saint_michael" -> Color(0xFFFCA5A5)  // Bright flame highlight
            "joyful" -> Color(0xFF93C5FD)         // Bright lapis highlight
            else -> Color(0xFF93C5FD)
        }

    val darkBeadColor: Color
        get() = when (id) {
            "sorrowful" -> Color(0xFF450A0A)
            "luminous" -> Color(0xFF451A03)
            "glorious" -> Color(0xFF2E1065)
            "saint_michael" -> Color(0xFF450A0A)
            "joyful" -> Color(0xFF0F172A)
            else -> Color(0xFF0F172A)
        }
}

@Serializable
data class MysteryCatalog(
    val mysterySets: List<MysterySet>
)
