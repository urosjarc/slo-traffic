package com.urosjarc.slotraffic.domain.weather

import com.urosjarc.slotraffic.domain.Location
import kotlinx.datetime.Instant

data class Wind(
    val instant: Instant,
    val location: Location,
    val height: Int,
    val speed: Float,
    val maxSpeed: Float,
    val direction: Int?,
)