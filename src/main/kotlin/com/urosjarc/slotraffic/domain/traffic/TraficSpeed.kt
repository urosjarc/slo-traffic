package com.urosjarc.slotraffic.domain.traffic

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class TraficSpeed(
    val instant: Instant,
    val average: Float
)
