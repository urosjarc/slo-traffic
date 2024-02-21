package com.urosjarc.slotraffic.netex

import kotlinx.serialization.Serializable

@Serializable
data class Fare(
    val id: String,
    val name: String,
    val start: StopPlace,
    val end: StopPlace,
    val amount: Float
) {
    @Serializable
    data class StopPlace(
        val stopPlaceId: String,
        val quayId: String
    )
}
