package com.urosjarc.slotraffic.domain

import jakarta.xml.bind.annotation.XmlRootElement
import jakarta.xml.bind.annotation.XmlValue

@XmlRootElement(name = "publicationTime")
class PublicationTime {

    @XmlValue
    val data: String? = null
    override fun toString(): String {
        return "PublicationTime(data=$data)"
    }

}
