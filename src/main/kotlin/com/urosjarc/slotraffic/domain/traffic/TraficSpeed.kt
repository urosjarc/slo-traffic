package com.urosjarc.slotraffic.domain.traffic

import kotlinx.datetime.Instant

data class TraficSpeed(
    val instant: Instant,
    val averageSpeed: Float
)
