package com.urosjarc.slotraffic.domain

import jakarta.xml.bind.annotation.XmlAnyElement
import jakarta.xml.bind.annotation.XmlAttribute
import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "PredefinedLocationsPublication")
class PredefinedLocationsPublication {

    @XmlAttribute
    val lang: String? = null

    @XmlAttribute
    val modelBaseVersion: Int? = null

    @XmlElement(namespace = "http://datex2.eu/schema/3/common")
    val publicationTime: String? = null

    @XmlElement(namespace = "http://datex2.eu/schema/3/locationReferencing")
    val predefinedLocationReference: PredefinedLocationReference? = null

    @XmlAnyElement(lax = true)
    private val anything: List<Any>? = null
    override fun toString(): String {
        return "PredefinedLocationsPublication(lang=$lang, modelBaseVersion=$modelBaseVersion, publicationTime=$publicationTime, predefinedLocationReference=$predefinedLocationReference, anything=$anything)"
    }


}
