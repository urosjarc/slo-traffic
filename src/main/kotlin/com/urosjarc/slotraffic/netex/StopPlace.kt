package com.urosjarc.slotraffic.netex

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class StopPlace(
    val id: String,
    val name: String,
    val code: Int,
    val lon: Double,
    val lat: Double,
    val addressId: String,
    val country: String,
    val town: String,
    val suburb: String,
    val transportMode: Transport,
    val type: Type,
    val quays: MutableList<Quay> = mutableListOf()
) {
    enum class Type {
        busStation, railStation
    }

    @Serializable
    data class Quay(
        val id: String,
        val created: Instant,
        val name: String,
        val code: Int,
        val lat: Double,
        val lon: Double,
        val covered: Covered,
        val type: Type,
    ) {
        enum class Covered { unknown, covered }
        enum class Type { busStop, railPlatform }

    }
}
