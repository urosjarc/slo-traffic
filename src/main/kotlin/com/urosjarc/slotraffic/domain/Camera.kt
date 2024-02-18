package com.urosjarc.slotraffic.domain

data class Camera(
    val location: Location,
    val imgUrl: String,
    val title: Map<Lang, String>,
    val desciption: Map<Lang, String>,
    val group: Map<Lang, String>,
    val region: Map<Lang, String>,
)
