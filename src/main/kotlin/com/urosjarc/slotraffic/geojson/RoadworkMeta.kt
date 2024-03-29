package com.urosjarc.slotraffic.geojson

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class RoadworkMeta(
    val LastUpdateTS: Instant,
    val PeriodFrom: Instant,
    val PeriodTo: Instant,
    val Language: String
)
