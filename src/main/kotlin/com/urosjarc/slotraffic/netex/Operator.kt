package com.urosjarc.slotraffic.netex

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Operator(
    val id: String,
    val name: String,
    val email: String?,
    val phone: String?,
    val url: String?,
    val type: Type
) {
    enum class Type { operator }
}
