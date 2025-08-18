package br.com.plussapps.georeminder.domain.usecase

import br.com.plussapps.georeminder.domain.model.Location
import kotlin.math.*

class CheckProximityUseCase {
    operator fun invoke(
        userLat: Double,
        userLng: Double,
        target: Location
    ): Boolean {
        val earthRadius = 6371000.0 // metros
        val dLat = Math.toRadians(target.latitude - userLat)
        val dLon = Math.toRadians(target.longitude - userLng)

        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(userLat)) * cos(Math.toRadians(target.latitude)) *
                sin(dLon / 2).pow(2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = earthRadius * c
        return distance <= target.radius
    }
}