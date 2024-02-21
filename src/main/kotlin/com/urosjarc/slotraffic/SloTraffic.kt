package com.urosjarc.slotraffic

import com.urosjarc.slotraffic.geojson.*
import com.urosjarc.slotraffic.netex.*
import com.urosjarc.slotraffic.res.GeoJson
import kotlinx.datetime.toLocalTime
import org.jsoup.Jsoup
import org.jsoup.parser.Parser


class SloTraffic(
    napUsername: String,
    napPassword: String
) {
    val napApi = NapApi(username = napUsername, password = napPassword)
    suspend fun getCameras(): GeoJson<CameraProps, Unit> = this.napApi.getGeoJson(name = "b2b.cameras.geojson")
    suspend fun getRoadworks(): GeoJson<RoadworkProps, RoadworkMeta> = this.napApi.getGeoJson(name = "b2b.roadworks.geojson.sl_SI")
    suspend fun getRestAreas(): GeoJson<RestAreaProps, Unit> = this.napApi.getGeoJson(name = "b2b.restareas.geojson")
    suspend fun getEvents(): GeoJson<EventProps, EventMeta> = this.napApi.getGeoJson(name = "b2b.events.geojson.sl_SI")
    suspend fun getCounters(): GeoJson<CounterProps, CounterMeta> = this.napApi.getGeoJson(name = "b2b.counters.geojson.sl_SI")
    suspend fun getWinds(): GeoJson<WindProps, WindMeta> = this.napApi.getGeoJson(name = "b2b.wind.geojson")
    suspend fun getBorderDelays(): GeoJson<BorderDelayProps, BorderDelayMeta> = this.napApi.getGeoJson(name = "b2b.borderdelays.geojson")
    suspend fun getStopPlaces(): MutableMap<Id<StopPlace>, StopPlace> {
        val id_to_stopPlace = mutableMapOf<Id<StopPlace>, StopPlace>()

        this.napApi.getZipFile(name = "b2b.netex.stopplaces") { zEntry, zStream ->
            val xml = Jsoup.parse(zStream, null, "", Parser.xmlParser())
            for (sp in xml.select("stopPlace")) {

                /**
                 * Stop place
                 */
                val centroid = sp.selectFirst("Centroid")!!
                val postalAddress = sp.selectFirst("PostalAddress")!!
                val stopPlace = StopPlace(
                    id = Id(sp.attr("id")),
                    name = sp.selectFirst("Name")!!.text(),
                    privateCode = sp.selectFirst("PrivateCode")!!.text().toInt(),
                    vector = Vector(
                        lon = centroid.selectFirst("Longitude")!!.text().toDouble(),
                        lat = centroid.selectFirst("Latitude")!!.text().toDouble(),
                    ),
                    country = postalAddress.selectFirst("CountryRef")!!.attr("ref"),
                    town = postalAddress.selectFirst("Town")!!.text(),
                    suburb = postalAddress.selectFirst("Suburb")!!.text(),
                    transport = Transport.valueOf(sp.selectFirst("TransportMode")!!.text()),
                    type = StopPlace.Type.valueOf(sp.selectFirst("StopPlaceType")!!.text()),
                )

                /**
                 * Quays
                 */
                for (q in sp.select("Quay")) {
                    val qc = sp.selectFirst("Centroid")!!
                    val quay = StopPlace.Quay(
                        id = Id(q.attr("id")),
                        name = q.selectFirst("Name")!!.text(),
                        privateCode = q.selectFirst("PrivateCode")!!.text().toInt(),
                        vector = Vector(
                            lon = qc.selectFirst("Longitude")!!.text().toDouble(),
                            lat = qc.selectFirst("Latitude")!!.text().toDouble(),
                        ),
                        covered = StopPlace.Quay.Covered.valueOf(q.selectFirst("Covered")!!.text()),
                        type = StopPlace.Quay.Type.valueOf(q.selectFirst("QuayType")!!.text()),
                    )
                    stopPlace.quays.add(quay)
                }

                id_to_stopPlace[stopPlace.id] = stopPlace
            }
        }

        return id_to_stopPlace
    }

    suspend fun getOperators(): Map<Id<Operator>, Operator> {
        val operators = mutableMapOf<Id<Operator>, Operator>()

        this.napApi.getZipFile(name = "b2b.netex.operators") { zEntry, zStream ->
            val xml = Jsoup.parse(zStream, null, "", Parser.xmlParser())
            for (o in xml.select("Operator")) {
                val operator = Operator(
                    id = Id(o.attr("id")),
                    name = o.selectFirst("Name")!!.text(),
                    email = o.selectFirst("Email")!!.text(),
                    phone = o.selectFirst("Phone")!!.text(),
                    url = o.selectFirst("Url")!!.text(),
                    type = Operator.Type.valueOf(o.selectFirst("OrganisationType")!!.text())
                )
                operators[operator.id] = operator
            }
        }

        return operators
    }

    suspend fun getFares(): List<Fare> {
        val fares = mutableListOf<Fare>()

        this.napApi.getZipFile(name = "b2b.netex.fares") { zEntry, zStream ->
            val xml = Jsoup.parse(zStream, null, "", Parser.xmlParser())

            // Stop point to its informations
            val id_to_stopPlace = mutableMapOf<Id<StopPoint>, StopPoint>()
            for (psa in xml.select("PassengerStopAssignment")) {
                val stopPoint = StopPoint(
                    id = Id(psa.selectFirst("ScheduledStopPointRef")!!.attr("ref")),
                    stopPlaceId = Id(psa.selectFirst("StopPlaceRef")!!.attr("ref")),
                    quayId = Id(psa.selectFirst("QuayRef")!!.attr("ref")),
                )
                id_to_stopPlace[stopPoint.id] = stopPoint
            }


            // Matrix element to price
            val fareId_to_amount = mutableMapOf<Id<Fare>, Float>()
            for (dmep in xml.select("DistanceMatrixElementPrice")) {
                val amount = dmep.selectFirst("Amount")!!.text().toFloat()
                val fareId = Id<Fare>(dmep.selectFirst("DistanceMatrixElementRef")!!.attr("ref"))
                fareId_to_amount[fareId] = amount
            }

            // Matrix element
            for (dme in xml.select("DistanceMatrixElement")) {
                val fareId = Id<Fare>(dme.attr("id"))
                val startStopId = Id<StopPoint>(dme.selectFirst("StartStopPointRef")!!.attr("ref"))
                val endStopId = Id<StopPoint>(dme.selectFirst("EndStopPointRef")!!.attr("ref"))
                val fare = Fare(
                    id = fareId,
                    name = dme.selectFirst("Name")!!.text(),
                    start = id_to_stopPlace[startStopId]!!,
                    end = id_to_stopPlace[endStopId]!!,
                    amount = fareId_to_amount[fareId]!!
                )
                fares.add(fare)
            }
        }

        return fares
    }

    suspend fun getTimetables(): List<Timetable> {
        val timetables = mutableListOf<Timetable>()

        this.napApi.getZipFile(name = "b2b.netex.lines") { zEntry, zStream ->
            val xml = Jsoup.parse(zStream, null, "", Parser.xmlParser())

            // DayTypes
            val id_to_dayType = mutableMapOf<Id<Timetable.DayType>, Timetable.DayType>()
            for (dt in xml.select("DayType")) {
                val dayType = Timetable.DayType(
                    id = Id(dt.attr("id")),
                    name = dt.selectFirst("Name")!!.text(),
                    days = dt.selectFirst("DaysOfWeek")?.text()?.split(" ")?.map { Timetable.DayType.Day.valueOf(it) }
                )
                id_to_dayType[dayType.id] = dayType
            }

            // Stop point to its informations
            val id_to_stopPoint = mutableMapOf<Id<StopPoint>, StopPoint>()
            for (psa in xml.select("PassengerStopAssignment")) {
                val stopPoint = StopPoint(
                    id = Id(psa.selectFirst("ScheduledStopPointRef")!!.attr("ref")),
                    stopPlaceId = Id(psa.selectFirst("StopPlaceRef")!!.attr("ref")),
                    quayId = Id(psa.selectFirst("QuayRef")!!.attr("ref")),
                )
                id_to_stopPoint[stopPoint.id] = stopPoint
            }

            // ServiceLinks
            val id_to_serviceLink = mutableMapOf<Id<Timetable.Journey.Link>, Timetable.Journey.Link>()
            for (sl in xml.select("ServiceLink")) {
                val link = Timetable.Journey.Link(
                    id = Id(sl.attr("id")),
                    distance = sl.selectFirst("Distance")?.text()?.toDouble(),
                    vectors = sl.selectFirst("LineString")?.children()?.map {
                        val c = it.text().split(" ")
                        Vector(
                            lon = c.first().toDouble(),
                            lat = c.last().toDouble()
                        )
                    },
                    from = id_to_stopPoint[Id(sl.selectFirst("FromPointRef")!!.attr("ref"))]!!,
                    to = id_to_stopPoint[Id(sl.selectFirst("ToPointRef")!!.attr("ref"))]!!,
                )
                id_to_serviceLink[link.id] = link
            }

            // Line
            val id_to_routeInfo = mutableMapOf<Id<Timetable.Journey.Route.Info>, Timetable.Journey.Route.Info>()
            for (l in xml.select("Line")) {
                val routeInfo = Timetable.Journey.Route.Info(
                    id = Id(l.attr("id")),
                    name = l.selectFirst("Name")!!.text(),
                    transport = Transport.valueOf(l.selectFirst("TransportMode")!!.text()),
                    publicCode = l.selectFirst("PublicCode")!!.text().toInt(),
                    privateCode = l.selectFirst("PrivateCode")!!.text().toInt(),
                    operatorId = Id(l.selectFirst("OperatorRef")!!.attr("ref"))
                )
                id_to_routeInfo[routeInfo.id] = routeInfo
            }

            // Routepoints
            val id_to_routePoint = mutableMapOf<Id<Timetable.Journey.Route.Point>, Timetable.Journey.Route.Point>()
            for (rp in xml.select("RoutePoint")) {
                val routePoint = Timetable.Journey.Route.Point(
                    id = Id(rp.attr("id")),
                    name = rp.selectFirst("Name")!!.text(),
                    vector = Vector(
                        lat = rp.selectFirst("Latitude")!!.text().toDouble(),
                        lon = rp.selectFirst("Longitude")!!.text().toDouble(),
                    ),
                    stopPoint = id_to_stopPoint[Id(rp.selectFirst("ProjectToPointRef")!!.attr("ref"))]!!
                )
                id_to_routePoint[routePoint.id] = routePoint
            }

            // Route
            val id_to_route = mutableMapOf<Id<Timetable.Journey.Route>, Timetable.Journey.Route>()
            for (r in xml.select("Route")) {
                val route = Timetable.Journey.Route(
                    id = Id(r.attr("id")),
                    name = r.selectFirst("Name")!!.text(),
                    info = id_to_routeInfo[Id(r.selectFirst("LineRef")!!.attr("ref"))]!!,
                    points = r.select("RoutePointRef").map { id_to_routePoint[Id(it.attr("ref"))]!! }
                )
                id_to_route[route.id] = route
            }

            // ServiceJourneyPattern
            val id_to_journey = mutableMapOf<Id<Timetable.Journey>, Timetable.Journey>()
            for (sjp in xml.select("ServiceJourneyPattern")) {
                val journey = Timetable.Journey(
                    id = Id(sjp.attr("id")),
                    name = sjp.selectFirst("Name")!!.text(),
                    privateCode = sjp.selectFirst("PrivateCode")!!.text().toInt(),
                    route = id_to_route[Id(sjp.selectFirst("RouteRef")!!.attr("ref"))]!!,
                    stopPoints = sjp.select("ScheduledStopPointRef").map { id_to_stopPoint[Id(it.attr("ref"))]!! },
                    links = sjp.select("ServiceLinkRef").map { id_to_serviceLink[Id(it.attr("ref"))]!! }
                )
                id_to_journey[journey.id] = journey
            }


            // ServiceJourneyPattern
            for (sj in xml.select("ServiceJourney")) {
                val s = Timetable(
                    id = Id(sj.attr("id")),
                    name = sj.selectFirst("Name")!!.text(),
                    transport = Transport.valueOf(sj.selectFirst("TransportMode")!!.text()),
                    workingDays = sj.select("DayTypeRef").map { id_to_dayType[Id(it.attr("ref"))]!! },
                    journey = id_to_journey[Id(sj.selectFirst("ServiceJourneyPatternRef")!!.attr("ref"))]!!,
                    operatorId = Id(sj.selectFirst("OperatorRef")!!.attr("ref")),
                    schedule = sj.select("TimetabledPassingTime").map {
                        Timetable.Time(
                            arrival = it.selectFirst("ArrivalTime")?.text()?.toLocalTime(),
                            departure = it.selectFirst("DepartureTime")?.text()?.toLocalTime(),
                        )
                    }
                )

                timetables.add(s)
            }
        }

        return timetables
    }

}
