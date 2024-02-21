package com.urosjarc.slotraffic.geojson

import kotlinx.serialization.Serializable

@Serializable
data class CameraProps(
    val group: CameraGroup,
    val items: List<CameraItem>
) {
    @Serializable
    data class CameraGroup(
        val LegacyId: Int,
        val EntityId: String,
        val name: String,
        val title_slo: String,
        val title_eng: String,
        val priority: Int?,
        val KameraOwner: String?
    )

    @Serializable
    data class CameraItem(
        val LegacyId: Int,
        val EntityId: String,
        val x: Float,
        val y: Float,
        val name: String,
        val region: String,
        val title_slo: String,
        val title_eng: String,
        val text_slo: String,
        val text_eng: String,
        val image: String,
        val active: Int,
        val priority: Int?,
        val odsek: String?,
        val stacionaza: Float?,
        val KameraOwner: String?,
    )
}
