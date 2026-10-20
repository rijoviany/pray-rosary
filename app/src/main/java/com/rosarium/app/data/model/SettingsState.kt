package com.rosarium.app.data.model

data class SettingsState(
    val language: String = "en", // "en" or "ml"
    val tapToPrayEnabled: Boolean = false,
    val soundEffectsEnabled: Boolean = true,
    val ambientChantEnabled: Boolean = false,
    val hapticFeedbackEnabled: Boolean = true,
    val includeFatimaPrayer: Boolean = true,
    val prayerDurationSeconds: Int = 16, // 10 to 30 seconds
    val fontScale: Float = 1.0f // 0.85f to 1.3f
)
