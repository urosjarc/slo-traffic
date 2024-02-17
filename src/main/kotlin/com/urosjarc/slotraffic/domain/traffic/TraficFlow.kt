package com.urosjarc.slotraffic.domain.traffic

data class TraficFlow(
    val startInstant: String,
    val endInstant: String,
    val flowRate: Int
)
