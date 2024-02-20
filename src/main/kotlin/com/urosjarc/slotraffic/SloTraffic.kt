package com.urosjarc.slotraffic

import com.urosjarc.slotraffic.domain.*
import com.urosjarc.slotraffic.res.GeoJson


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

}
