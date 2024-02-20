package com.urosjarc.slotraffic.res

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class GeoJson<T, P>(
    val type: Type,
    val crs: Crs,
    val features: List<Feature<T>>,
    val properties: P? = null
) {
    enum class Type { FeatureCollection }

    @Serializable
    data class Crs(
        val type: String,
        val properties: MutableMap<String, String>
    )

    @Serializable
    data class Feature<T>(
        val type: Type,
        val geometry: Geometry,
        val properties: T
    ) {
        enum class Type {
            Feature
        }

        @Serializable
        data class Geometry(
            val type: Type,
            val coordinates: List<Double>
        ) {
            enum class Type { Point }
        }
    }
}
