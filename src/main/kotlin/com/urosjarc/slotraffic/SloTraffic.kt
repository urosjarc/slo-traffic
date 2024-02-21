package com.urosjarc.slotraffic

import com.urosjarc.slotraffic.geojson.*
import com.urosjarc.slotraffic.netex.*
import com.urosjarc.slotraffic.res.GeoJson
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalTime
import org.jsoup.Jsoup
import org.jsoup.parser.Parser


class SloTraffic(
    username: String,
    password: String
) : SloTrafficUtils(
    username = username,
    password = password
) {
    suspend fun getCameras(): GeoJson<CameraProps, Unit> = this.getGeoJson(name = "b2b.cameras.geojson")
    suspend fun getRoadworks(): GeoJson<RoadworkProps, RoadworkMeta> = this.getGeoJson(name = "b2b.roadworks.geojson.sl_SI")
    suspend fun getRestAreas(): GeoJson<RestAreaProps, Unit> = this.getGeoJson(name = "b2b.restareas.geojson")
    suspend fun getEvents(): GeoJson<EventProps, EventMeta> = this.getGeoJson(name = "b2b.events.geojson.sl_SI")
    suspend fun getCounters(): GeoJson<CounterProps, CounterMeta> = this.getGeoJson(name = "b2b.counters.geojson.sl_SI")
    suspend fun getWinds(): GeoJson<WindProps, WindMeta> = this.getGeoJson(name = "b2b.wind.geojson")
    suspend fun getBorderDelays(): GeoJson<BorderDelayProps, BorderDelayMeta> = this.getGeoJson(name = "b2b.borderdelays.geojson")
    suspend fun getStopPlaces(): Map<String, StopPlace> {
        val stopPlaces = mutableMapOf<String, StopPlace>()

        this.getZipFile(name = "b2b.netex.stopplaces") { zEntry, zStream ->
            val xml = Jsoup.parse(zStream, null, "", Parser.xmlParser())
            for (stopPlace in xml.select("stopPlace")) {

                /**
                 * Stop place
                 */
                val centroid = stopPlace.selectFirst("Centroid")!!
                val postalAddress = stopPlace.selectFirst("PostalAddress")!!
                val sp = StopPlace(
                    id = stopPlace.attr("id"),
                    name = stopPlace.selectFirst("Name")!!.text(),
                    code = stopPlace.selectFirst("PrivateCode")!!.text().toInt(),
                    lon = centroid.selectFirst("Longitude")!!.text().toDouble(),
                    lat = centroid.selectFirst("Latitude")!!.text().toDouble(),
                    addressId = postalAddress.attr("id"),
                    country = postalAddress.selectFirst("CountryRef")!!.attr("ref"),
                    town = postalAddress.selectFirst("Town")!!.text(),
                    suburb = postalAddress.selectFirst("Suburb")!!.text(),
                    transportMode = Transport.valueOf(stopPlace.selectFirst("TransportMode")!!.text()),
                    type = StopPlace.Type.valueOf(stopPlace.selectFirst("StopPlaceType")!!.text()),
                )

                /**
                 * Quays
                 */
                for (quay in stopPlace.select("Quay")) {
                    val qCentroid = stopPlace.selectFirst("Centroid")!!
                    val q = StopPlace.Quay(
                        id = quay.attr("id"),
                        created = quay.attr("created").toInstant(),
                        name = quay.selectFirst("Name")!!.text(),
                        code = quay.selectFirst("PrivateCode")!!.text().toInt(),
                        lat = qCentroid.selectFirst("Latitude")!!.text().toDouble(),
                        lon = qCentroid.selectFirst("Longitude")!!.text().toDouble(),
                        covered = StopPlace.Quay.Covered.valueOf(quay.selectFirst("Covered")!!.text()),
                        type = StopPlace.Quay.Type.valueOf(quay.selectFirst("QuayType")!!.text()),
                    )
                    sp.quays.add(q)
                }

                stopPlaces[sp.id] = sp
            }
        }

        return stopPlaces
    }

    suspend fun getOperators(): Map<String, Operator> {
        val operators = mutableMapOf<String, Operator>()

        this.getZipFile(name = "b2b.netex.operators") { zEntry, zStream ->
            val xml = Jsoup.parse(zStream, null, "", Parser.xmlParser())
            for (operator in xml.select("Operator")) {
                val o = Operator(
                    id = operator.attr("id"),
                    name = operator.selectFirst("Name")!!.text(),
                    email = operator.selectFirst("Email")!!.text(),
                    phone = operator.selectFirst("Phone")!!.text(),
                    url = operator.selectFirst("Url")!!.text(),
                    type = Operator.Type.valueOf(operator.selectFirst("OrganisationType")!!.text())
                )
                operators[o.id] = o
            }
        }

        return operators
    }

    suspend fun getFares(): Map<String, Fare> {
        val fares = mutableMapOf<String, Fare>()

        this.getZipFile(name = "b2b.netex.fares") { zEntry, zStream ->
            val xml = Jsoup.parse(zStream, null, "", Parser.xmlParser())

            // Stop point to its informations
            val stopPointId_to_fareStopPlace = mutableMapOf<String, Fare.StopPlace>()
            for (pStopAss in xml.select("PassengerStopAssignment")) {
                val scheduledStopPointRef = pStopAss.selectFirst("ScheduledStopPointRef")!!.attr("ref")
                val fareStopPlace = Fare.StopPlace(
                    stopPlaceId = pStopAss.selectFirst("StopPlaceRef")!!.attr("ref"),
                    quayId = pStopAss.selectFirst("QuayRef")!!.attr("ref"),
                )
                stopPointId_to_fareStopPlace[scheduledStopPointRef] = fareStopPlace
            }


            // Matrix element to price
            val eleId_to_amount = mutableMapOf<String, Float>()
            for (dmElePrice in xml.select("DistanceMatrixElementPrice")) {
                val amount = dmElePrice.selectFirst("Amount")!!.text().toFloat()
                val elementRef = dmElePrice.selectFirst("DistanceMatrixElementRef")!!.attr("ref")
                eleId_to_amount[elementRef] = amount
            }

            // Matrix element
            for (ele in xml.select("DistanceMatrixElement")) {
                val id = ele.attr("id")
                val startStopId = ele.selectFirst("StartStopPointRef")!!.attr("ref")
                val endStopId = ele.selectFirst("EndStopPointRef")!!.attr("ref")
                val fare = Fare(
                    id = id,
                    name = ele.selectFirst("Name")!!.text(),
                    start = stopPointId_to_fareStopPlace[startStopId]!!,
                    end = stopPointId_to_fareStopPlace[endStopId]!!,
                    amount = eleId_to_amount[id]!!
                )
                fares[fare.id] = fare
            }
        }

        return fares
    }

    suspend fun getTimetables(): Map<String, Timetable> {
        val timetables = mutableMapOf<String, Timetable>()

        this.getZipFile(name = "b2b.netex.lines") { zEntry, zStream ->
            val xml = Jsoup.parse(zStream, null, "", Parser.xmlParser())

            // DayTypes
            val dayTypeId_to_day = mutableMapOf<String, Timetable.DayType>()
            for (dayType in xml.select("DayType")) {
                val d = Timetable.DayType(
                    id = dayType.attr("id"),
                    name = dayType.selectFirst("Name")!!.text(),
                    days = dayType.selectFirst("DaysOfWeek")?.text()?.split(" ")?.map { Timetable.DayType.Day.valueOf(it) }
                )
                dayTypeId_to_day[d.id] = d
            }

            // Stop point to its informations
            val scheduledStopPointId_to_scheduledStopPoint = mutableMapOf<String, Timetable.StopPoint>()
            for (pStopAss in xml.select("PassengerStopAssignment")) {
                val s = Timetable.StopPoint(
                    id = pStopAss.selectFirst("ScheduledStopPointRef")!!.attr("ref"),
                    stopPlaceId = pStopAss.selectFirst("StopPlaceRef")!!.attr("ref"),
                    quayId = pStopAss.selectFirst("QuayRef")!!.attr("ref"),
                )
                scheduledStopPointId_to_scheduledStopPoint[s.id] = s
            }

            // ServiceLinks
            val serviceLinkId_to_serviceLink = mutableMapOf<String, Timetable.Journey.Link>()
            for (serviceLink in xml.select("ServiceLink")) {
                val sl = Timetable.Journey.Link(
                    id = serviceLink.attr("id"),
                    distance = serviceLink.selectFirst("Distance")?.text()?.toDouble(),
                    points = serviceLink.selectFirst("LineString")?.children()?.map {
                        val c = it.text().split(" ")
                        c.first().toDouble() to c.last().toDouble()
                    },
                    from = scheduledStopPointId_to_scheduledStopPoint[serviceLink.selectFirst("FromPointRef")!!.attr("ref")]!!,
                    to = scheduledStopPointId_to_scheduledStopPoint[serviceLink.selectFirst("ToPointRef")!!.attr("ref")]!!,
                )
                serviceLinkId_to_serviceLink[sl.id] = sl
            }

            // Line
            val lineId_to_line = mutableMapOf<String, Timetable.Journey.Route.Info>()
            for (line in xml.select("Line")) {
                val l = Timetable.Journey.Route.Info(
                    id = line.attr("id"),
                    name = line.selectFirst("Name")!!.text(),
                    transport = Transport.valueOf(line.selectFirst("TransportMode")!!.text()),
                    publicCode = line.selectFirst("PublicCode")!!.text().toInt(),
                    privateCode = line.selectFirst("PrivateCode")!!.text().toInt(),
                    operatorRef = line.selectFirst("OperatorRef")!!.attr("ref")
                )
                lineId_to_line[l.id] = l
            }

            // Routepoints
            val routePointId_to_routePoint = mutableMapOf<String, Timetable.Journey.Route.Point>()
            for (routePoint in xml.select("RoutePoint")) {
                val rp = Timetable.Journey.Route.Point(
                    id = routePoint.attr("id"),
                    name = routePoint.selectFirst("Name")!!.text(),
                    lat = routePoint.selectFirst("Latitude")!!.text().toDouble(),
                    lon = routePoint.selectFirst("Longitude")!!.text().toDouble(),
                    stopPoint = scheduledStopPointId_to_scheduledStopPoint[routePoint.selectFirst("ProjectToPointRef")!!.attr("ref")]!!
                )
                routePointId_to_routePoint[rp.id] = rp
            }

            // Route
            val routeId_to_route = mutableMapOf<String, Timetable.Journey.Route>()
            for (route in xml.select("Route")) {
                val lineId = route.selectFirst("LineRef")!!.attr("ref")
                val r = Timetable.Journey.Route(
                    id = route.attr("id"),
                    name = route.selectFirst("Name")!!.text(),
                    info = lineId_to_line[lineId]!!,
                    points = route.select("RoutePointRef").map { routePointId_to_routePoint[it.attr("ref")]!! }
                )
                routeId_to_route[r.id] = r
            }

            // ServiceJourneyPattern
            val serviceJourneyPatternId_to_serviceJourneyPattern = mutableMapOf<String, Timetable.Journey>()
            for (sjp in xml.select("ServiceJourneyPattern")) {
                val routeId = sjp.selectFirst("RouteRef")!!.attr("ref")
                val s = Timetable.Journey(
                    id = sjp.attr("id"),
                    name = sjp.selectFirst("Name")!!.text(),
                    privateCode = sjp.selectFirst("PrivateCode")!!.text().toInt(),
                    route = routeId_to_route[routeId]!!,
                    stopPoints = sjp.select("ScheduledStopPointRef").map { scheduledStopPointId_to_scheduledStopPoint[it.attr("ref")]!! },
                    links = sjp.select("ServiceLinkRef").map { serviceLinkId_to_serviceLink[it.attr("ref")]!! }
                )
                serviceJourneyPatternId_to_serviceJourneyPattern[s.id] = s
            }


            // ServiceJourneyPattern
            for (sj in xml.select("ServiceJourney")) {
                val serviceJourneyPatternId = sj.selectFirst("ServiceJourneyPatternRef")!!.attr("ref")
                val s = Timetable(
                    id = sj.attr("id"),
                    name = sj.selectFirst("Name")!!.text(),
                    transport = Transport.valueOf(sj.selectFirst("TransportMode")!!.text()),
                    operating = sj.select("DayTypeRef").map { dayTypeId_to_day[it.attr("ref")]!! },
                    journey = serviceJourneyPatternId_to_serviceJourneyPattern[serviceJourneyPatternId]!!,
                    operatorId = sj.selectFirst("OperatorRef")!!.attr("ref"),
                    schedule = sj.select("TimetabledPassingTime").map {
                        Timetable.Time(
                            arrival = it.selectFirst("ArrivalTime")?.text()?.toLocalTime(),
                            departure = it.selectFirst("DepartureTime")?.text()?.toLocalTime(),
                        )
                    }
                )

                timetables[s.id] = s
            }
        }

        return timetables
    }

}
