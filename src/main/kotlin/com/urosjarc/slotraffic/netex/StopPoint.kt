package com.urosjarc.slotraffic.netex

import kotlinx.serialization.Serializable

@Serializable
data class StopPoint(
    val id: Id<StopPoint>, //ScheduledStopPoint
    val stopPlaceId: Id<StopPlace>, //StopPlace
    val quayId: Id<StopPlace.Quay>, // Quay
)
