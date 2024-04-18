package no.uio.ifi.in2000.project.model.search

import kotlinx.serialization.Serializable

@Serializable
data class AutoCompleteResponse(
    val features: List<ApiGeoJsonFeature>,
    val query: ApiQuery
)

@Serializable
data class ApiQuery(
    val text: String,
    val parsed: ApiParsed
)

@Serializable
data class ApiParsed(
    val city: String,
    val expected_type: String
)

@Serializable
data class ApiGeoJsonFeature(
    val properties: ApiProperties,
    val geometry: ApiGeometry
)

@Serializable
data class ApiProperties(
    val formatted: String,
    val lon: Double,
    val lat: Double,
    val timezone: ApiTimezoneList
)

@Serializable
data class ApiTimezoneList(
    val name: String,
    val offset_STD: String,
    val offset_DST: String
)

@Serializable
data class ApiGeometry(
    val type: String,
    val coordinates: List<Double>
)