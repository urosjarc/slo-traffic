package com.urosjarc.slotraffic.domain.weather

import com.urosjarc.slotraffic.domain.Location
import kotlinx.datetime.Instant

data class AirHumidity(
    val instant: Instant,
    val location: Location,
    val percentage: Int
)
