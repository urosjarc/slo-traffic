package com.urosjarc.slotraffic.domain.weather

import com.urosjarc.slotraffic.domain.Location
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable


@Serializable
data class RoadSurface(
    val instant: Instant,
    val location: Location,
    val condition: Condition,
    val temperature: Float,
    val waterThickness: Float?
) {
    enum class Condition {
        dry, wet, other
    }
}
