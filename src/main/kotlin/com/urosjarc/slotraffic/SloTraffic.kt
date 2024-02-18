package com.urosjarc.slotraffic

import com.urosjarc.slotraffic.domain.*
import com.urosjarc.slotraffic.domain.traffic.TrafficConcentration
import com.urosjarc.slotraffic.domain.traffic.TrafficHeadway
import com.urosjarc.slotraffic.domain.traffic.TraficFlow
import com.urosjarc.slotraffic.domain.traffic.TraficSpeed
import com.urosjarc.slotraffic.domain.weather.*
import com.urosjarc.slotraffic.exceptions.ParserException
import kotlinx.datetime.toInstant
import kotlin.collections.set


class SloTraffic(
    username: String,
    password: String
) : SloTrafficUtils(
    username = username,
    password = password
) {

    private suspend fun getEvents(name: String): List<Event> {
        val events = mutableListOf<Event>()

        this.getXmlData(name = name) { doc ->
            doc.select("situation").forEach { sit ->
                val sr = sit.selectFirst("situationRecord")!!
                val type = Event.Type.valueOf(sr.attr("xsi:type"))
                val probability = Event.Probability.valueOf(sr.selectFirst("probabilityOfOccurrence")!!.text())
                val severity = Event.Severity.valueOf(sr.selectFirst("severity")!!.text())
                val startTime = sr.selectFirst("overallStartTime")!!.text().toInstant()
                val endTime = sr.selectFirst("overallEndTime")!!.text().toInstant()
                val capacityRemaining = sr.selectFirst("capacityRemaining")?.text()?.toInt() ?: 100
                val comment = sr.selectFirst("generalPublicComment")!!.select("value")
                    .associate { Lang.valueOf(it.attribute("lang").value) to it.text() }

                val locRef = sr.selectFirst("locationReference")!!
                val index = locRef.attr("xsi:type").split(":").first()
                val location = Location(
                    lat = locRef.selectFirst("$index|latitude")!!.text().toDouble(),
                    lon = locRef.selectFirst("$index|longitude")!!.text().toDouble()
                )

                events.add(
                    Event(
                        type = type,
                        location = location,
                        probability = probability,
                        severity = severity,
                        startInstant = startTime,
                        endInstant = endTime,
                        capacityRemaining = capacityRemaining,
                        comment = comment
                    )
                )
            }
        }

        return events
    }

    suspend fun getCameras(): List<Camera> {
        val cameras = mutableListOf<Camera>()

        this.getXmlData("b2b.cameras.datexii33") { doc ->
            doc.select("predefinedLocationReference").forEach { plr ->
                val lat = plr.selectFirst("latitude")!!.text().toDouble()
                val lon = plr.selectFirst("longitude")!!.text().toDouble()
                val img = plr.selectFirst("stillImageUrl")!!.text()

                //Title
                val lang_to_titles = mutableMapOf<Lang, String>()
                plr.selectFirst("cameraTitle")!!.select("value").forEach { value ->
                    lang_to_titles[Lang.valueOf(value.attr("lang"))] = value.text()
                }

                //Description
                val lang_to_description = mutableMapOf<Lang, String>()
                plr.selectFirst("cameraDescription")!!.select("value").forEach { value ->
                    lang_to_description[Lang.valueOf(value.attr("lang"))] = value.text()
                }

                //Group
                val lang_to_group = mutableMapOf<Lang, String>()
                plr.selectFirst("groupName")!!.select("value").forEach { value ->
                    lang_to_group[Lang.valueOf(value.attr("lang"))] = value.text()
                }

                //Region
                val lang_to_region = mutableMapOf<Lang, String>()
                plr.selectFirst("regionName")!!.select("value").forEach { value ->
                    lang_to_region[Lang.valueOf(value.attr("lang"))] = value.text()
                }

                cameras.add(
                    Camera(
                        location = Location(lat = lat, lon = lon),
                        imgUrl = img,
                        title = lang_to_titles,
                        description = lang_to_description,
                        group = lang_to_group,
                        region = lang_to_region
                    )
                )
            }
        }

        return cameras
    }

    suspend fun getWeather(): Weather {
        val weather = Weather()

        this.getXmlData("b2b.weather.dars.datexii33") { xml ->
            xml.select("physicalQuantity").forEach { pq ->
                val pl = pq.selectFirst("pertinentLocation")!!
                val index = pl.attr("xsi:type").split(":").first()
                val location = Location(
                    lat = pl.selectFirst("$index|latitude")!!.text().toDouble(),
                    lon = pl.selectFirst("$index|longitude")!!.text().toDouble()
                )
                val basicData = pq.selectFirst("basicData")!!
                val basicDataType = basicData.attr("xsi:type")
                val instant = basicData.selectFirst("measurementOrCalculationTime")!!.text().toInstant()

                when (basicDataType) {

                    "WindInformation" -> weather.wind.add(
                        Wind(
                            location = location,
                            instant = instant,
                            height = basicData.selectFirst("windMeasurementHeight")!!.text().toInt(),
                            speed = basicData.selectFirst("windSpeed")!!.text().toFloat(),
                            maxSpeed = basicData.selectFirst("maximumWindSpeed")!!.text().toFloat(),
                            direction = basicData.selectFirst("windDirectionBearing")?.text()?.toInt()
                        )
                    )

                    "TemperatureInformation" -> weather.temperature.add(
                        AirTemperature(
                            location = location,
                            instant = instant,
                            value = basicData.selectFirst("airTemperature")!!.text().toFloat(),
                            dewPoint = basicData.selectFirst("dewPointTemperature")!!.text().toFloat()
                        )

                    )

                    "HumidityInformation" -> weather.humidity.add(
                        AirHumidity(
                            location = location,
                            instant = instant,
                            percentage = basicData.selectFirst("relativeHumidity")!!.text().toInt()
                        )
                    )

                    "VisibilityInformation" -> weather.visibility.add(
                        AirVisibility(
                            location = location,
                            instant = instant,
                            distance = basicData.selectFirst("minimumVisibilityDistance")!!.text().toInt()
                        )
                    )

                    "RoadSurfaceConditionInformation" -> {
                        weather.roadSurface.add(
                            RoadSurface(
                                location = location,
                                instant = instant,
                                condition = RoadSurface.Condition.valueOf(basicData.selectFirst("weatherRelatedRoadConditionType")!!.text()),
                                temperature = basicData.selectFirst("roadSurfaceTemperature")!!.text().toFloat(),
                                waterThickness = basicData.selectFirst("waterFilmThickness")?.text()?.toFloat()
                            )
                        )
                    }

                    "PrecipitationInformation" -> {
                        weather.precipitation.add(
                            Precipitation(
                                location = location,
                                instant = instant,
                                type = Precipitation.Type.valueOf(basicData.selectFirst("precipitationType")!!.text())
                            )
                        )

                    }

                    else -> throw ParserException("Unknown weather type info: $basicDataType")
                }
            }
        }

        return weather
    }

    suspend fun getCounters(): List<Counter> {
        val counters = mutableListOf<Counter>()

        getXmlData("b2b.counters.datexii33") { doc ->
            doc.select("siteMeasurements").forEach { sm ->
                val pl = sm.selectFirst("pertinentLocation")!!
                val index = pl.attr("xsi:type").split(":").first()

                val trafficFlow = sm.selectFirst("basicData[xsi:type=TrafficFlow]")!!
                val trafficSpeed = sm.selectFirst("basicData[xsi:type=TrafficSpeed]")!!
                val trafficHeadway = sm.selectFirst("basicData[xsi:type=TrafficHeadway]")!!
                val trafficConcentration = sm.selectFirst("basicData[xsi:type=TrafficConcentration]")!!

                counters.add(
                    Counter(
                        location = Location(
                            lat = pl.selectFirst("$index|latitude")!!.text().toDouble(),
                            lon = pl.selectFirst("$index|longitude")!!.text().toDouble()
                        ),
                        trafficSpeed = TraficSpeed(
                            instant = trafficSpeed.selectFirst("timeValue")!!.text().toInstant(),
                            average = trafficSpeed.selectFirst("averageVehicleSpeed")!!.text().toFloat()
                        ),
                        trafficFlow = TraficFlow(
                            startInstant = trafficFlow.selectFirst("startOfPeriod")!!.text().toInstant(),
                            endInstant = trafficFlow.selectFirst("endOfPeriod")!!.text().toInstant(),
                            rate = trafficFlow.selectFirst("vehicleFlowRate")!!.text().toInt()
                        ),
                        trafficConcentration = TrafficConcentration(
                            instant = trafficConcentration.selectFirst("timeValue")!!.text().toInstant(),
                            density = trafficConcentration.selectFirst("densityOfVehicles")!!.text().toInt()
                        ),
                        trafficHeadway = TrafficHeadway(
                            instant = trafficConcentration.selectFirst("timeValue")!!.text().toInstant(),
                            averageDistance = trafficHeadway.selectFirst("averageDistanceHeadway")!!.text().toDouble(),
                            averageTime = trafficHeadway.selectFirst("averageTimeHeadway")!!.text().toFloat()
                        ),
                    )
                )
            }

        }

        return counters
    }

    suspend fun getEvents(): List<Event> = this.getEvents(name = "b2b.events.datexii33")

    suspend fun getRestAreas(): List<RestArea> {
        val restAreas = mutableListOf<RestArea>()

        getXmlData("b2b.restareas.datexii33") { doc ->
            doc.select("parkingTable").forEach { pt ->
                val heg = pt.selectFirst("hierarchyElementGeneral")!!

                val title = mutableMapOf<Lang, String>()
                for (value in heg.selectFirst("name")!!.select("value")) {
                    title[Lang.valueOf(value.attr("lang"))] = value.text()
                }

                val desc = mutableMapOf<Lang, String>()
                for (value in heg.selectFirst("description")!!.select("value")) {
                    desc[Lang.valueOf(value.attr("lang"))] = value.text()
                }

                val opHour = heg.selectFirst("operatingHours")!!.attr("xsi:type")
                val index = heg.selectFirst("locationReference")!!.attr("xsi:type").split(":").first()
                val location = Location(
                    lat = heg.selectFirst("$index|latitude")!!.text().toDouble(),
                    lon = heg.selectFirst("$index|longitude")!!.text().toDouble(),
                )

                val facilities = mutableListOf<RestArea.Facility>()
                for (fac in heg.select("supplementalFacility")) {
                    facilities.add(RestArea.Facility.valueOf(fac.text()))
                }

                restAreas.add(
                    RestArea(
                        location = location,
                        title = title,
                        description = desc,
                        facilities = facilities,
                        workingHours = RestArea.WorkingHours.valueOf(opHour),
                    )
                )
            }
        }


        return restAreas
    }

    suspend fun getRoadWorks(): List<Event> = this.getEvents(name = "b2b.roadworks.datexii33")

}
