package com.urosjarc.slotraffic.domain

data class RestArea(
    val location: Location,
    val title: MutableMap<Lang, String> = mutableMapOf(),
    val description: MutableMap<Lang, String> = mutableMapOf(),
    val facilities: List<Facility>,
    val workingHours: WorkingHours
) {
    enum class Facility {
        petrolStation,
        shop,
        toilet,
        publicPhone,
        waterSupply,
        picnicFacilities,
        cashMachine,
        cafe,
        playground,
        restaurant,
        internetWireless,
        shower
    }

    enum class WorkingHours {
        OpenAllHours,
        UnknownOperatingHours
    }
}
