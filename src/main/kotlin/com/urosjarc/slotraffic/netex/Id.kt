package com.urosjarc.slotraffic.netex

import kotlinx.serialization.Serializable

@JvmInline
@Serializable
value class Id<T>(val value: String)
