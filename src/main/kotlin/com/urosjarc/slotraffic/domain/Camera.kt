package com.urosjarc.slotraffic.domain

data class Camera(
    val location: Location,
    val imgUrl: String,
    val title: Map<String, String>,
    val desciption: Map<String, String>,
    val group: Map<String, String>,
    val region: Map<String, String>,
)
