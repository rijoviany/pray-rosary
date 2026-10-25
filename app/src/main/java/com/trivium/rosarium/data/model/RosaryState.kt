package com.trivium.rosarium.data.model

enum class DecadeStep {
    OUR_FATHER,
    HAIL_MARY,
    GLORY_BE,
    FATIMA,
    DECADE_FINISHED
}

data class RosaryState(
    val currentMysterySet: MysterySet,
    val decadeIndex: Int = 0, // 0 to 4 (representing 1st through 5th decades)
    val currentStep: DecadeStep = DecadeStep.OUR_FATHER,
    val completedHailMarysInDecade: Int = 0, // 0 to 10
    val isOurFatherCompleted: Boolean = false,
    val isGloryBeCompleted: Boolean = false,
    val isFatimaCompleted: Boolean = false,
    val isRosaryFinished: Boolean = false,

    // Hold-to-pray / Active bead state
    val isPrayingActive: Boolean = false,
    val isReadyToRelease: Boolean = false,
    val prayerProgress: Float = 0f, // 0.0f to 1.0f
    val currentPhraseIndex: Int = 0,
    val isSpringSnapping: Boolean = false,
    val justCompletedBeadIndex: Int? = null, // 0..9 for blue beads, -1 for our father

    // Active prayer content
    val activePrayer: Prayer? = null
) {
    val currentMystery: Mystery?
        get() = currentMysterySet.mysteries.getOrNull(decadeIndex)

    val isWhiteBeadActive: Boolean
        get() = currentStep == DecadeStep.OUR_FATHER

    val isGloryBeActive: Boolean
        get() = currentStep == DecadeStep.GLORY_BE || currentStep == DecadeStep.FATIMA
}
