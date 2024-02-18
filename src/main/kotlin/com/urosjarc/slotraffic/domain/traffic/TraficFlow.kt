package com.urosjarc.slotraffic.domain.traffic

import kotlinx.datetime.Instant

data class TraficFlow(
    val startInstant: Instant,
    val endInstant: Instant,
    val flowRate: Int
)
