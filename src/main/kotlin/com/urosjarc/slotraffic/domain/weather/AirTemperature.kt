package com.urosjarc.slotraffic.domain.weather

import com.urosjarc.slotraffic.domain.Location
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class AirTemperature(
    val instant: Instant,
    val location: Location,
    val value: Float,
    val dewPoint: Float
)
