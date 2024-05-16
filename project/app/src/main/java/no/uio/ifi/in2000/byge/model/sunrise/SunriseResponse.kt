package no.uio.ifi.in2000.byge.model.sunrise

import com.google.gson.annotations.SerializedName

data class SunriseResponse (
    val type: String,
    val geometry: Geometry,
    @SerializedName("when")
    val time: When,
    val properties: Properties
)
