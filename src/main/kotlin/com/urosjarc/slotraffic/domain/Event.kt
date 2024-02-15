package com.urosjarc.slotraffic.domain

data class Event(
    val lat: Double,
    val lon: Double,
    val probability: String,
    val severity: String,
    val startTime: String,
    val endTime: String,
    val comment: Map<String, String>
)
