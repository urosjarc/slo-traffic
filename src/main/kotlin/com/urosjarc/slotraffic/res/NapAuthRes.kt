package com.urosjarc.slotraffic.res

import kotlinx.serialization.Serializable

@Serializable
data class NapAuthRes(
    val token_type: String,
    val access_token: String,
    val refresh_token: String,
    val expires_in: Int,
)
