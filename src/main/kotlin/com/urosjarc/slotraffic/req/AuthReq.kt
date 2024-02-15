package com.urosjarc.slotraffic.req

import kotlinx.serialization.Serializable

@Serializable
data class AuthReq(
    val grant_type: String = "password",
    val username: String,
    val password: String
)
