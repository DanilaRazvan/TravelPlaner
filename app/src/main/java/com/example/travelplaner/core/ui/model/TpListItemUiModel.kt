package com.example.travelplaner.core.ui.model

import kotlinx.serialization.Serializable

@Serializable
data class TpListItemUiModel(
    val id: Long,
    val imageUrl: String,
    val city: String,
    val country: String = "",
    val details: String,
    val from: Long? = null,
    val to: Long? = null
)

data class LandmarkUiModel(
    val id: Long,
    val imageUrl: String,
    val name: String,
    val details: String,
    val price: Float
)