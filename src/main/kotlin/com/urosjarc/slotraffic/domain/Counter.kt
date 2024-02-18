package com.urosjarc.slotraffic.domain

import com.urosjarc.slotraffic.domain.traffic.TrafficConcentration
import com.urosjarc.slotraffic.domain.traffic.TrafficHeadway
import com.urosjarc.slotraffic.domain.traffic.TraficFlow
import com.urosjarc.slotraffic.domain.traffic.TraficSpeed
import kotlinx.serialization.Serializable

@Serializable
data class Counter(
    val location: Location,
    val trafficSpeed: TraficSpeed,
    val trafficFlow: TraficFlow,
    val trafficConcentration: TrafficConcentration,
    val trafficHeadway: TrafficHeadway
)
