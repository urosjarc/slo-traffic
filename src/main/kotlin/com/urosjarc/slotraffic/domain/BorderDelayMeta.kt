package com.urosjarc.slotraffic.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class BorderDelayMeta(
    val title: String,
    val subtitle: String,
    val link: Link,
    val updated: Instant,
    val author: Author,
    val id: String,
    val xmlns: String,
    val xmlns_georss: String,
    val xmlns_crs: String,
    val xmlns_stevci: String,
) {
    @Serializable
    data class Link(val href: String)

    @Serializable
    data class Author(val name: String, val email: String)
}
