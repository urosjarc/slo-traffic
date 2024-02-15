package com.urosjarc.slotraffic.domain

import jakarta.xml.bind.annotation.XmlAnyElement
import jakarta.xml.bind.annotation.XmlAttribute
import jakarta.xml.bind.annotation.XmlElement
import jakarta.xml.bind.annotation.XmlRootElement

@XmlRootElement(name = "predefinedLocationReference")
class PredefinedLocationReference {

    @XmlAttribute
    val id: String? = null

    @XmlAttribute
    val version: String? = null

    @XmlAnyElement(lax = true)
    private val anything: List<Any>? = null
    override fun toString(): String {
        return "PredefinedLocationReference(id=$id, version=$version, anything=$anything)"
    }

}
