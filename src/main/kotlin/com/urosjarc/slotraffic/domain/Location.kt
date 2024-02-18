package com.urosjarc.slotraffic.domain

import kotlinx.serialization.Serializable

@Serializable
data class Location(
    val lat: Double,
    val lon: Double
)
