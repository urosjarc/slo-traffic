package com.urosjarc.slotraffic.domain.traffic

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class TrafficConcentration(
    val instant: Instant,
    val density: Int
)
