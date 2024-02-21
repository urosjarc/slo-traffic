package com.urosjarc.slotraffic.netex

import kotlinx.serialization.Serializable

@Serializable
data class Operator(
    val id: Id<Operator>,
    val name: String,
    val email: String?,
    val phone: String?,
    val url: String?,
    val type: Type
) {
    enum class Type { operator }
}
