package com.urosjarc.slotraffic.domain

import com.urosjarc.slotraffic.domain.weather.*

data class Weather(
    val wind: MutableList<Wind> = mutableListOf(),
    val temperature: MutableList<AirTemperature> = mutableListOf(),
    val humidity: MutableList<AirHumidity> = mutableListOf(),
    val visibility: MutableList<AirVisibility> = mutableListOf(),
    val roadSurface: MutableList<RoadSurface> = mutableListOf()
)
