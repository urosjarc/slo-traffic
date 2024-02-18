package com.urosjarc.slotraffic.domain.weather

import com.urosjarc.slotraffic.domain.Location
import kotlinx.datetime.Instant

data class AirVisibility(
    val instant: Instant,
    val location: Location,
    val distance: Int
)
