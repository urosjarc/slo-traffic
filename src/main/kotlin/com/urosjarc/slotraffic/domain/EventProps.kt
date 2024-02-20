package com.urosjarc.slotraffic.domain

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class EventProps(
    val updated: Instant,
    val x: Double,
    val y: Double,
    val id: Int,
    val EntityId: String,
    val kategorija: String,
    val cesta: String,
    val vzrok: String,
    val opis: String,
    val dodatnoPojasnilo: String,
    val IsConfirmed: Boolean
)
