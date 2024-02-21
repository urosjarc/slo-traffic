package com.urosjarc.slotraffic.netex

import kotlinx.serialization.Serializable

@Serializable
data class Vector(
    val lat: Double,
    val lon: Double
)
