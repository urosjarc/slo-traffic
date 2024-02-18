package com.urosjarc.slotraffic.domain.weather

import com.urosjarc.slotraffic.domain.Location
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Precipitation(
    val instant: Instant,
    val location: Location,
    val type: Type
) {
    enum class Type {
        rain, drizzle
    }
}
