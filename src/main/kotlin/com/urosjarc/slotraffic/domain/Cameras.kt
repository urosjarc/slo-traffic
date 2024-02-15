package com.urosjarc.slotraffic.domain

import javax.xml.bind.annotation.XmlAnyElement
import javax.xml.bind.annotation.XmlAttribute
import javax.xml.bind.annotation.XmlRootElement


@XmlRootElement(name = "PredefinedLocationsPublication")
class Cameras {
    @XmlAttribute
    val lang: String? = null

    @XmlAnyElement(lax = true)
    private val anything: List<Any>? = null
}
