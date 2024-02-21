package com.urosjarc.slotraffic.geojson

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class BorderDelayProps(
    val BorderCrossingId: String,
    val BorderCrossingName: String,
    val BorderCrossingForeignName: String?,
    val Updated: Instant,
    val Description: String,
    val Description_i18n: DescriptionI18n,
    val Road: String,
    val RoadName: String,
    val InboundTruckDelaySeconds: Int?,
    val InboundTruckQueueMeters: Int?,
    val InboundBusDelaySeconds: Int?,
    val InboundBusQueueMeters: Int?,
    val InboundCarDelaySeconds: Int?,
    val InboundCarQueueMeters: Int?,
    val OutboundTruckDelaySeconds: Int?,
    val OutboundTruckQueueMeters: Int?,
    val OutboundBusDelaySeconds: Int?,
    val OutboundBusQueueMeters: Int?,
    val OutboundCarDelaySeconds: Int,
    val OutboundCarQueueMeters:Int?
) {
    @Serializable
    data class DescriptionI18n(
        val en_US: String,
        val sl_SI: String
    )
}
