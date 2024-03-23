package no.uio.ifi.in2000.project.model.alerts

import com.google.gson.annotations.SerializedName

data class Feature(
    val geometry: Geometry,
    val properties: Properties,
    val type: String,
    @SerializedName("when")
    val time: When
)

