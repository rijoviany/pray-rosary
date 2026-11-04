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
    val beadColorHex: String = "#1E40AF",
    val category: String = "marian_rosary" // "marian_rosary" or "catholic_chaplet"
) {
    val isChaplet: Boolean
        get() = category == "catholic_chaplet" || id in setOf(
            "saint_michael", "divine_mercy", "saint_joseph", "seven_sorrows", "sacred_heart",
            "precious_blood", "franciscan_crown", "holy_souls", "holy_wounds", "saint_anne"
        )

    val effectiveBeadsPerDecade: Int
        get() = when (id) {
            "saint_michael", "seven_sorrows" -> 7
            "precious_blood", "saint_anne" -> 5
            else -> beadsPerDecade
        }

    val primaryBeadColor: Color
        get() = when (id) {
            "sorrowful" -> Color(0xFF991B1B)       // Passion Ruby Red
            "luminous" -> Color(0xFFD97706)        // Radiant Solar Amber / Topaz
            "glorious" -> Color(0xFF7C3AED)        // Royal Resurrection Amethyst
            "saint_michael" -> Color(0xFFDC2626)   // Archangelic Flame Crimson
            "divine_mercy" -> Color(0xFFB91C1C)    // Divine Mercy Ruby Ray
            "saint_joseph" -> Color(0xFFB45309)    // Saint Joseph Warm Amber
            "seven_sorrows" -> Color(0xFF581C87)   // Mater Dolorosa Royal Violet
            "sacred_heart" -> Color(0xFF881337)    // Sacred Heart Rosewood Crimson
            "precious_blood" -> Color(0xFF7F1D1D)  // Deep Precious Blood Crimson
            "franciscan_crown" -> Color(0xFFD97706)// Franciscan Cord Gold
            "holy_souls" -> Color(0xFF475569)      // Obsidian Slate / Purgatorial Gray
            "holy_wounds" -> Color(0xFF831843)     // Wounded Heart Velvet Rose
            "saint_anne" -> Color(0xFF047857)      // Saint Anne Emerald Green
            "joyful" -> Color(0xFF1E40AF)          // Marian Lapis Lazuli Blue
            else -> Color(0xFF1E40AF)
        }

    val specularBeadColor: Color
        get() = when (id) {
            "sorrowful" -> Color(0xFFEF4444)       // Bright ruby highlight
            "luminous" -> Color(0xFFFDE047)        // Bright gold amber highlight
            "glorious" -> Color(0xFFA78BFA)        // Bright lavender amethyst highlight
            "saint_michael" -> Color(0xFFFCA5A5)   // Bright flame highlight
            "divine_mercy" -> Color(0xFFFECACA)    // Pale Ray highlight
            "saint_joseph" -> Color(0xFFFDE68A)    // Solar Amber highlight
            "seven_sorrows" -> Color(0xFFD8B4FE)   // Lilac Violet highlight
            "sacred_heart" -> Color(0xFFFDA4AF)    // Bright Rose highlight
            "precious_blood" -> Color(0xFFF87171)  // Precious Blood Ruby highlight
            "franciscan_crown" -> Color(0xFFFEF08A)// Solar Joy highlight
            "holy_souls" -> Color(0xFFCBD5E1)      // Celestial Silver highlight
            "holy_wounds" -> Color(0xFFF472B6)     // Rose highlight
            "saint_anne" -> Color(0xFF6EE7B7)      // Mint Emerald highlight
            "joyful" -> Color(0xFF93C5FD)          // Bright lapis highlight
            else -> Color(0xFF93C5FD)
        }

    val darkBeadColor: Color
        get() = when (id) {
            "sorrowful", "divine_mercy", "saint_michael", "precious_blood" -> Color(0xFF450A0A)
            "sacred_heart", "holy_wounds" -> Color(0xFF4C0519)
            "luminous", "saint_joseph", "franciscan_crown" -> Color(0xFF451A03)
            "glorious", "seven_sorrows" -> Color(0xFF2E1065)
            "holy_souls" -> Color(0xFF0F172A)
            "saint_anne" -> Color(0xFF064E3B)
            "joyful" -> Color(0xFF0F172A)
            else -> Color(0xFF0F172A)
        }
}

@Serializable
data class MysteryCatalog(
    val mysterySets: List<MysterySet>
)
