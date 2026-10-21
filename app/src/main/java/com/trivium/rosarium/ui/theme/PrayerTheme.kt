package com.trivium.rosarium.ui.theme

import androidx.compose.ui.graphics.Color
import com.trivium.rosarium.data.model.DecadeStep

/**
 * Dedicated visual themes and color signatures for each specific prayer
 * in the Holy Rosary:
 * - Our Father: Celestial Ivory & Solar Gold
 * - Hail Mary: Marian Ultramarine & Lapis Lazuli
 * - Glory Be: Holy Trinity Sunburst & Amber Flame
 * - Fatima Prayer: Sacred Heart Crimson & Passion Red
 * - Hail Holy Queen: Queen of Heaven Imperial Amethyst & Crown Gold
 */
data class PrayerThemeColors(
    val step: DecadeStep,
    val name: String,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val auraGlow: Color,
    val vignetteBase: Color,
    val frescoTone: Color,
    val bannerGoldTrim: Color,
    val titleHighlight: Color
)

object PrayerThemes {
    val OurFather = PrayerThemeColors(
        step = DecadeStep.OUR_FATHER,
        name = "Celestial Ivory & Gold",
        primaryAccent = PearlWhite,
        secondaryAccent = MedievalGoldBright,
        auraGlow = Color(0xFFFFF8E1),
        vignetteBase = Color(0xFF16110B),
        frescoTone = Color(0xFFD4AF37),
        bannerGoldTrim = MedievalGoldBright,
        titleHighlight = MedievalGoldBright
    )

    val HailMary = PrayerThemeColors(
        step = DecadeStep.HAIL_MARY,
        name = "Marian Ultramarine",
        primaryAccent = UltramarineLapis,
        secondaryAccent = UltramarineSpecular,
        auraGlow = GoldGlow,
        vignetteBase = Color(0xFF090D1A),
        frescoTone = UltramarineLapis,
        bannerGoldTrim = MedievalGold,
        titleHighlight = MedievalGoldBright
    )

    val GloryBe = PrayerThemeColors(
        step = DecadeStep.GLORY_BE,
        name = "Holy Trinity Sunburst",
        primaryAccent = Color(0xFFE5A823),
        secondaryAccent = Color(0xFFFFD54F),
        auraGlow = Color(0xFFFFE082),
        vignetteBase = Color(0xFF1A1208),
        frescoTone = Color(0xFFFFA000),
        bannerGoldTrim = Color(0xFFFFD700),
        titleHighlight = Color(0xFFFFE082)
    )

    val Fatima = PrayerThemeColors(
        step = DecadeStep.FATIMA,
        name = "Sacred Heart Crimson",
        primaryAccent = OxbloodRed,
        secondaryAccent = OxbloodBright,
        auraGlow = Color(0xFFFF8A80),
        vignetteBase = Color(0xFF190808),
        frescoTone = Color(0xFF8B1E1E),
        bannerGoldTrim = Color(0xFFE57373),
        titleHighlight = Color(0xFFFF8A80)
    )

    val HailHolyQueen = PrayerThemeColors(
        step = DecadeStep.DECADE_FINISHED,
        name = "Queen of Heaven Amethyst",
        primaryAccent = Color(0xFF5E2B97),
        secondaryAccent = Color(0xFF9C27B0),
        auraGlow = Color(0xFFE1BEE7),
        vignetteBase = Color(0xFF13091F),
        frescoTone = Color(0xFF4A148C),
        bannerGoldTrim = Color(0xFFFFD700),
        titleHighlight = Color(0xFFE1BEE7)
    )

    fun forStep(step: DecadeStep): PrayerThemeColors {
        return when (step) {
            DecadeStep.OUR_FATHER -> OurFather
            DecadeStep.HAIL_MARY -> HailMary
            DecadeStep.GLORY_BE -> GloryBe
            DecadeStep.FATIMA -> Fatima
            DecadeStep.DECADE_FINISHED -> HailHolyQueen
        }
    }
}
