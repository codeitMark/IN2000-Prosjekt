package no.uio.ifi.in2000.project.model.forecast

import kotlinx.serialization.Serializable
import java.io.Serial

@Serializable
data class LocationForecastResponse(
    val type: String,
    val geometry: Geometry,
    val properties: Properties
)

@Serializable
data class Geometry(
    val type: String,
    val coordinates: List<Int>
)
//Comments Comments Comments
@Serializable
data class Properties(
    val meta: Meta,
    val timeseries: List<TimeSeries>
)

@Serializable
data class Meta(
    val updated_at: String,
    val units: Units
)

@Serializable
data class Units(
    val air_pressure_at_sea_level: String,
    val air_temperature: String,
    val cloud_area_fraction: String,
    val precipitation_amount: String,
    val relative_humidity: String,
    val wind_from_direction: String,
    val wind_speed: String
)

@Serializable
data class TimeSeries(
    val time: String,
    val data: Data
)

@Serializable
data class Data(
    val instant: Instant,
    val next_12_hours: NextHours,
    val next_1_hours: NextHours,
    val next_6_hours: NextHours
)

@Serializable
data class Instant(
    val details: Instant_Details
)

@Serializable
data class Instant_Details(
    val air_pressure_at_sea_level: Float,
    val air_temperature: Float,
    val cloud_area_fraction: Float,
    val relative_humidity: Float,
    val wind_from_direction: Float,
    val wind_speed: Float
)

@Serializable
data class NextHours(
    val summary: Summary,
    val details: NextHours_Details
)

@Serializable
data class Summary(
    val symbol_code: String
)

@Serializable
data class NextHours_Details(
    val precipitation_amount: Float
)