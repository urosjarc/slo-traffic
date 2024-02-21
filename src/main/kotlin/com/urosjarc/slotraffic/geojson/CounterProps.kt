package com.urosjarc.slotraffic.geojson

import kotlinx.datetime.Instant
import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class CounterProps(
    val title: String,
    val id: String,
    val updated: Instant,
    val summary: String,
    val stevci_lokacija: Int,
    val stevci_lokacijaOpis: String,
    val stevci_cestaOpis: String,
    val stevci_odsek: String,
    val stevci_stacionaza: String,
    val stevci_smer: Int,
    val stevci_smerOpis: String,
    val stevci_pasOpis: String,
    val stevci_regija: String,
    val stevci_geoX: Int,
    val stevci_geoY: Int,
    val stevci_vmax: Int,
    val stevci_datum: String,
    val stevci_ura: LocalTime,
    val stevci_stev: Int,
    val stevci_hit: Int,
    val stevci_gap: Float,
    val stevci_occ: Int,
    val stevci_stat: Int,
    val stevci_statOpis: String,
)
