package com.urosjarc.slotraffic.netex

import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
data class Timetable(
    val id: Id<Timetable>, //ServiceJourney
    val name: String,
    val transport: Transport,
    val workingDays: List<DayType>, //DayType
    val journey: Journey, //ServiceJourneyPattern
    val operatorId: Id<Operator>, //Operator
    val schedule: List<Time>
) {

    @Serializable
    data class DayType(
        val id: Id<DayType>, //DayType
        val name: String,
        val days: List<Day>?
    ) {
        enum class Day {
            Monday, Tuesday, Wednesday, Thursday, Friday, Saturday, Sunday
        }
    }

    @Serializable
    data class Time(
        val arrival: LocalTime?,
        val departure: LocalTime?
    )

    @Serializable
    data class Journey(
        val id: Id<Journey>, // ServiceJourneyPattern
        val name: String,
        val privateCode: Int,
        val route: Route, //Route
        val stopPoints: List<StopPoint>, //ScheduledStopPoint
        val links: List<Link> // ServiceLink
    ) {
        @Serializable
        data class Link(
            val id: Id<Link>,//ServiceLink
            val distance: Double?,
            val vectors: List<Vector>?,
            val from: StopPoint, //ScheduledStopPoint
            val to: StopPoint, //ScheduledStopPoint
        )

        @Serializable
        data class Route(
            val id: Id<Route>, //Route
            val name: String,
            val info: Info,
            val points: List<Point> //PointOnRoute, RoutePoint
        ) {
            @Serializable
            data class Point(
                val id: Id<Point>, //RoutePoint
                val name: String,
                val vector: Vector,
                val stopPoint: StopPoint //ScheduledStopPoint
            )

            @Serializable
            data class Info(
                val id: Id<Info>, //Line
                val name: String,
                val transport: Transport,
                val publicCode: Int,
                val privateCode: Int,
                val operatorId: Id<Operator>
            )
        }
    }
}
