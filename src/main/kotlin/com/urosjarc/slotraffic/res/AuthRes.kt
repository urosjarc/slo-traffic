package com.urosjarc.slotraffic.res

import kotlinx.serialization.Serializable

@Serializable
data class AuthRes(
    var access_token: String,
    var refresh_token: String,
    var expires_in: Int,
)
