package com.urosjarc.slotraffic.domain.weather

import com.urosjarc.slotraffic.domain.Location

data class Wind(
    val instant: String,
    val location: Location,
    val height: Int,
    val speed: Float,
    val maxSpeed: Float,
    val direction: Int,
)
