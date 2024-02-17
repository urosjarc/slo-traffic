package com.urosjarc.slotraffic.domain.traffic

data class TrafficHeadway(
    val instant: String,
    val averageDistance: Double,
    val averageTime: Float
)
