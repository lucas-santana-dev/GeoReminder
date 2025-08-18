package br.com.plussapps.georeminder.domain.model

data class Location (
    val latitude: Double,
    val longitude: Double,
    val name: String? = null,
    val radius: Float = 100f // Default radius in meters
)

