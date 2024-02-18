package com.urosjarc.slotraffic.domain.traffic

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class TrafficHeadway(
    val instant: Instant,
    val averageDistance: Double,
    val averageTime: Float
)
