package com.urosjarc.slotraffic.domain.traffic

import kotlinx.datetime.Instant

data class TrafficHeadway(
    val instant: Instant,
    val averageDistance: Double,
    val averageTime: Float
)
