package com.urosjarc.slotraffic.domain

data class TraficFlow(
    val startInstant: String,
    val endInstant: String,
    val flowRate: Int
)
