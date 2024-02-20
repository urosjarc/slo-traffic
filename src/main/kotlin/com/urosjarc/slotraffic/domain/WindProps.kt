package com.urosjarc.slotraffic.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class WindProps(
    val id: Int,
    val title: String,
    val summary: String,
    val updated: Instant,
    val burja_veter: Float,
    val burja_sunki: Float
)
