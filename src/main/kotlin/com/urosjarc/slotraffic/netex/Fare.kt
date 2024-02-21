package com.urosjarc.slotraffic.netex

import kotlinx.serialization.Serializable

@Serializable
data class Fare(
    val id: Id<Fare>,
    val name: String,
    val start: StopPoint,
    val end: StopPoint,
    val amount: Float
)
