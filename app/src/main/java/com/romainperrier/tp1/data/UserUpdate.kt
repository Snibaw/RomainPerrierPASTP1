package com.romainperrier.tp1.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class UserUpdate(
    @SerialName("full_name")
    val name: String?,
    @SerialName("email")
    val email: String?
)
