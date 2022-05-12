package com.example.travelplaner.core.ui.model

data class TpListItemUiModel(
    val id: Long,
    val imageUrl: String,
    val city: String,
    val country: String = "",
    val details: String
)

data class LandmarkUiModel(
    val id: Long,
    val imageUrl: String,
    val name: String,
    val details: String,
    val price: Float
)