package com.urosjarc.slotraffic.domain.weather

import com.urosjarc.slotraffic.domain.Location

data class AirVisibility(
    val instant: String,
    val location: Location,
    val distance: Int
)
