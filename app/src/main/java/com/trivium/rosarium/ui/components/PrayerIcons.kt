package com.trivium.rosarium.ui.components

import com.trivium.rosarium.R

object PrayerIcons {

    /**
     * Map individual prayer IDs to their corresponding custom vector/medallion icons.
     */
    fun getPrayerIcon(prayerId: String): Int {
        return when (prayerId) {
            "our_father" -> R.drawable.ic_our_father
            "hail_mary" -> R.drawable.ic_hail_mary
            "glory_be" -> R.drawable.ic_glory_be
            "fatima" -> R.drawable.ic_fatima_prayer
            "hail_holy_queen" -> R.drawable.ic_hail_holy_queen
            "eternal_father" -> R.drawable.ic_eternal_father
            "sorrowful_passion" -> R.drawable.ic_sorrowful_passion
            "holy_god" -> R.drawable.ic_holy_god
            "hail_joseph" -> R.drawable.ic_hail_joseph
            "sacred_heart_large" -> R.drawable.ic_sacred_heart_offering
            "sacred_heart_small" -> R.drawable.ic_sacred_heart_aspiration
            "requiem_aeternam" -> R.drawable.ic_eternal_rest
            "holy_wounds_father" -> R.drawable.ic_holy_wounds_offering
            "holy_wounds_bead" -> R.drawable.ic_holy_wounds_invocation
            "precious_blood_bead" -> R.drawable.ic_precious_blood
            "saint_anne_bead" -> R.drawable.ic_saint_anne_invocation
            else -> R.drawable.ic_our_father
        }
    }

    /**
     * Map Rosary mystery sets and Chaplets to their custom icons.
     */
    fun getMysterySetIcon(mysterySetId: String): Int {
        return when (mysterySetId) {
            "joyful" -> R.drawable.ic_joyful_mysteries
            "luminous" -> R.drawable.ic_luminous_mysteries
            "sorrowful" -> R.drawable.ic_sorrowful_mysteries
            "glorious" -> R.drawable.ic_glorious_mysteries
            "saint_michael" -> R.drawable.ic_saint_michael_chaplet
            "divine_mercy" -> R.drawable.ic_divine_mercy_chaplet
            "saint_joseph" -> R.drawable.ic_saint_joseph_rosary
            "seven_sorrows" -> R.drawable.ic_seven_sorrows
            "sacred_heart" -> R.drawable.ic_sacred_heart_chaplet
            "precious_blood" -> R.drawable.ic_precious_blood_chaplet
            "franciscan_crown" -> R.drawable.ic_franciscan_crown
            "holy_souls" -> R.drawable.ic_holy_souls_chaplet
            "holy_wounds" -> R.drawable.ic_holy_wounds_chaplet
            "saint_anne" -> R.drawable.ic_saint_anne_chaplet
            else -> R.drawable.ic_joyful_mysteries
        }
    }

    /**
     * Map 9-Day Catholic Novenas to their custom icons.
     */
    fun getNovenaIcon(novenaId: String): Int {
        return when (novenaId) {
            "st_jude" -> R.drawable.ic_novena_saint_jude
            "divine_mercy" -> R.drawable.ic_novena_divine_mercy
            "undoer_of_knots" -> R.drawable.ic_novena_undoer_knots
            "sacred_heart" -> R.drawable.ic_novena_sacred_heart
            "holy_spirit" -> R.drawable.ic_novena_holy_spirit
            "st_joseph" -> R.drawable.ic_novena_saint_joseph
            "miraculous_medal" -> R.drawable.ic_novena_miraculous_medal
            "st_therese" -> R.drawable.ic_novena_saint_therese
            "st_anthony" -> R.drawable.ic_novena_saint_anthony
            "perpetual_help" -> R.drawable.ic_novena_perpetual_help
            "infant_jesus" -> R.drawable.ic_novena_infant_jesus
            "st_francis_xavier" -> R.drawable.ic_novena_saint_francis_xavier
            "vailankanni" -> R.drawable.ic_novena_vailankanni
            "padre_pio" -> R.drawable.ic_novena_padre_pio
            "mount_carmel" -> R.drawable.ic_novena_mount_carmel
            else -> R.drawable.ic_novena_holy_spirit
        }
    }
}
