package no.uio.ifi.in2000.project.model.sunrise

import com.google.gson.annotations.SerializedName

data class SunriseResponse (
    val type: String,
    val geometry: Geometry,
    @SerializedName("when")
    val time: When,
    val properties: Properties
)

data class When (
    val interval: List<String>
)

data class Geometry (
    val type: String,
    val coordinates: List<Float>
)

data class Properties (
    val body: String,
    val sunrise: Sunrise,
    val sunset: Sunset,
    val solarnoon: SolarNoon,
    val solarmidnight: SolarMidnight
)

data class Sunrise (
    val time: String,
    val azimuth: Double
)

data class Sunset (
    val time: String,
    val azimuth: Double
)

data class SolarNoon (
    val time: String,
    val discCentreElevation: Double,
    val visible: Boolean
)

data class SolarMidnight (
    val time: String,
    val discCentreElevation: Double,
    val visible: Boolean
)
