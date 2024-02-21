package com.urosjarc.slotraffic.netex

import kotlinx.serialization.Serializable

@Serializable
data class StopPlace(
    val id: Id<StopPlace>,
    val name: String,
    val privateCode: Int,
    val vector: Vector,
    val country: String,
    val town: String,
    val suburb: String,
    val transport: Transport,
    val type: Type,
    val quays: MutableList<Quay> = mutableListOf()
) {
    enum class Type {
        busStation, railStation
    }

    @Serializable
    data class Quay(
        val id: Id<Quay>,
        val name: String,
        val privateCode: Int,
        val vector: Vector,
        val covered: Covered,
        val type: Type,
    ) {
        enum class Covered { unknown, covered }
        enum class Type { busStop, railPlatform }

    }
}
