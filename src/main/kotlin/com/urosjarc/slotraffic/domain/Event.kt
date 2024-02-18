package com.urosjarc.slotraffic.domain

import kotlinx.datetime.Instant

data class Event(
    val type: Type,
    val location: Location,
    val probability: Probability,
    val severity: Severity,
    val startInstant: Instant,
    val endInstant: Instant,
    val comment: Map<Lang, String>,
    val capacityRemaining: Int
) {
    enum class Type {
        VehicleObstruction,
        Accident,
        MaintenanceWorks,
        RoadOrCarriagewayOrLaneManagement,
        GeneralObstruction,
        PoorEnvironmentConditions,
        GeneralNetworkManagement
    }

    enum class Probability {
        certain
    }
    enum class Severity {
        unknown
    }

    enum class ContrictionType {

    }

    enum class ObstructionType {

    }
}
