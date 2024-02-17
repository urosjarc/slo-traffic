package com.urosjarc.slotraffic.domain.weather

import com.urosjarc.slotraffic.domain.Location


data class RoadSurface(
    val instant: String,
    val location: Location,
    val condition: String,
    val temperature: Int,
    val waterThickness: Float
)
