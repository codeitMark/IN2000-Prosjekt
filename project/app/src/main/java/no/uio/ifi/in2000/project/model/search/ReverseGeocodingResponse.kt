package no.uio.ifi.in2000.project.model.search

import kotlinx.serialization.Serializable

@Serializable
data class ReverseGeocodingResponse(
    val results: List<ApiProperties>
)