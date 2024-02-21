package com.urosjarc.slotraffic.geojson

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class WindMeta(
    val title: String,
    val subtitle: String,
    val updated: Instant,
    val link: Link,
    val author: Author,
    val xmlns: String,
    val xmlns_georss: String,
    val xmlns_burja: String,
) {
    @Serializable
    data class Link(val href: String)

    @Serializable
    data class Author(val name: String, val email: String)
}
