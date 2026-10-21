package com.trivium.rosarium.data.model

data class SettingsState(
    val language: String = "en", // "en" or "ml"
    val tapToPrayEnabled: Boolean = false,
    val soundEffectsEnabled: Boolean = true,
    val ambientChantEnabled: Boolean = false,
    val hapticFeedbackEnabled: Boolean = true,
    val includeFatimaPrayer: Boolean = true,
    val prayerDurationSeconds: Int = 5, // minimum 5s for Hail Mary (10s for Our Father)
    val fontScale: Float = 1.0f // 0.85f to 1.3f
)
