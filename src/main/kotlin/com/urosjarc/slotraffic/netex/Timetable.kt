package com.urosjarc.slotraffic.netex

import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class Timetable(
    val id: String, //ServiceJourney
    val name: String,
    val transport: Transport,
    val operating: List<DayType>, //DayType
    val journey: Journey, //ServiceJourneyPattern
    val operatorId: String, //Operator
    val schedule: List<Time>
) {

    @Serializable
    data class DayType(
        val id: String, //DayType
        val name: String,
        val days: List<Day>?
    ) {
        enum class Day {
            Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
        }
    }

    @Serializable
    data class StopPoint(
        val id: String, //ScheduledStopPoint
        val stopPlaceId: String, //StopPlace
        val quayId: String, // Quay
    )

    @Serializable
    data class Time(
        val arrival: LocalTime?,
        val departure: LocalTime?
    )

    @Serializable
    data class Journey(
        val id: String, // ServiceJourneyPattern
        val name: String,
        val privateCode: Int,
        val route: Route, //Route
        val stopPoints: List<StopPoint>, //ScheduledStopPoint
        val links: List<Link> // ServiceLink
    ) {
        @Serializable
        data class Link(
            val id: String,//ServiceLink
            val distance: Double?,
            val points: List<Pair<Double, Double>>?,
            val from: StopPoint, //ScheduledStopPoint
            val to: StopPoint, //ScheduledStopPoint
        )

        @Serializable
        data class Route(
            val id: String, //Route
            val name: String,
            val info: Info,
            val points: List<Point> //PointOnRoute, RoutePoint
        ) {
            @Serializable
            data class Point(
                val id: String, //RoutePoint
                val name: String,
                val lon: Double,
                val lat: Double,
                val stopPoint: StopPoint //ScheduledStopPoint
            )

            @Serializable
            data class Info(
                val id: String, //Line
                val name: String,
                val transport: Transport,
                val publicCode: Int,
                val privateCode: Int,
                val operatorRef: String
            )
        }
    }
}
