package com.urosjarc.slotraffic.domain

import kotlinx.serialization.Serializable

@Serializable
data class Camera(
    val location: Location,
    val imgUrl: String,
    val title: Map<Lang, String>,
    val description: Map<Lang, String>,
    val group: Map<Lang, String>,
    val region: Map<Lang, String>,
)
