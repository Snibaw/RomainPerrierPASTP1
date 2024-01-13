package com.romainperrier.tp1.list

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Task(
    @SerialName("id")
    val id: String,
    @SerialName("content")
    val title: String = "Default title",
    @SerialName("description")
    val description: String = "Default description"
) : java.io.Serializable {

}
