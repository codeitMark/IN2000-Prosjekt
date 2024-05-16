package no.uio.ifi.in2000.byge.model.forecast

import kotlinx.serialization.Serializable

@Serializable
data class LocationForecastResponse(
    val type: String,
    val geometry: Geometry,
    val properties: Properties
)

@Serializable
data class Geometry(
    val type: String,
    val coordinates: List<Float>
)

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
    val air_temperature_max: String,
    val air_temperature_min: String,
    val air_temperature_percentile_10: String,
    val air_temperature_percentile_90: String,
    val cloud_area_fraction: String,
    val cloud_area_fraction_high: String,
    val cloud_area_fraction_low: String,
    val cloud_area_fraction_medium: String,
    val dew_point_temperature: String,
    val fog_area_fraction: String,
    val precipitation_amount: String,
    val precipitation_amount_max: String,
    val precipitation_amount_min: String,
    val probability_of_precipitation: String,
    val probability_of_thunder: String,
    val relative_humidity: String,
    val ultraviolet_index_clear_sky: String,
    val wind_from_direction: String,
    val wind_speed: String,
    val wind_speed_of_gust: String,
    val wind_speed_percentile_10: String,
    val wind_speed_percentile_90: String
)

@Serializable
data class TimeSeries(
    val time: String,
    val data: Data
)

@Serializable
data class Data(
    val instant: Instant,
    val next_12_hours: NextHours_12,
    val next_1_hours: NextHours_1,
    val next_6_hours: NextHours_6
)

@Serializable
data class Instant(
    val details: Instant_Details
)

@Serializable
data class Instant_Details(
    val air_pressure_at_sea_level: Float,
    val air_temperature: Float,
    val air_temperature_percentile_10: Float,
    val air_temperature_percentile_90: Float,
    val cloud_area_fraction: Float,
    val cloud_area_fraction_high: Float,
    val cloud_area_fraction_low: Float,
    val cloud_area_fraction_medium: Float,
    val dew_point_temperature: Float,
    val fog_area_fraction: Float,
    val relative_humidity: Float,
    val ultraviolet_index_clear_sky: Float,
    val wind_from_direction: Float,
    val wind_speed: Float,
    val wind_speed_of_gust: Float,
    val wind_speed_percentile_10: Float,
    val wind_speed_percentile_90: Float
)

@Serializable
data class NextHours_12(
    val summary: Summary_12,
    val details: NextHours_12_Details
)

@Serializable
data class NextHours_12_Details(
    val probability_of_precipitation: Float
)

@Serializable
data class NextHours_1(
    val summary: Summary,
    val details: NextHours_1_Details
)

@Serializable
data class NextHours_1_Details(
    val precipitation_amount: Float,
    val precipitation_amount_max: Float,
    val precipitation_amount_min: Float,
    val probability_of_precipitation: Float,
    val probability_of_thunder: Float
)

@Serializable
data class NextHours_6(
    val summary: Summary,
    val details: NextHours_6_Details
)

@Serializable
data class NextHours_6_Details(
    val air_temperature_max: Float,
    val air_temperature_min: Float,
    val precipitation_amount: Float,
    val precipitation_amount_max: Float,
    val precipitation_amount_min: Float,
    val probability_of_precipitation: Float
)

@Serializable
data class Summary(
    val symbol_code: String
)

@Serializable
data class Summary_12(
    val symbol_code: String,
    val symbol_confidence: String
)