package no.uio.ifi.in2000.byge.model.search

import kotlinx.serialization.Serializable

@Serializable
data class ReverseGeocodingResponse(
    val results: List<ApiProperties>
)