package com.urosjarc.slotraffic.domain.traffic

import kotlinx.datetime.Instant

data class TrafficConcentration(
    val instant: Instant,
    val density: Int
)
