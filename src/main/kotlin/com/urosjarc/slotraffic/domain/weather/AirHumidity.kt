package com.urosjarc.slotraffic.domain.weather

import com.urosjarc.slotraffic.domain.Location

data class AirHumidity(
    val instant: String,
    val location: Location,
    val percentage: Int
)
