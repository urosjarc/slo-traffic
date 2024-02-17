package com.urosjarc.slotraffic.domain.weather

import com.urosjarc.slotraffic.domain.Location

data class AirTemperature(
    val instant: String,
    val location: Location,
    val air: Float,
    val dewPoint: Float
)
