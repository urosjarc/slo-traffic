package com.urosjarc.slotraffic.domain.traffic

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class TraficFlow(
    val startInstant: Instant,
    val endInstant: Instant,
    val rate: Int
)
