package com.rosarium.app.data.model

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
    val mysteries: List<Mystery>
)

@Serializable
data class MysteryCatalog(
    val mysterySets: List<MysterySet>
)
