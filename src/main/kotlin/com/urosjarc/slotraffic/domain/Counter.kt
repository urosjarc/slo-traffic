package com.urosjarc.slotraffic.domain

data class Counter(
    val location: Location,
    val trafficSpeed: TraficSpeed,
    val trafficFlow: TraficFlow,
    val trafficConcentration: TrafficConcentration,
    val trafficHeadway: TrafficHeadway
)
