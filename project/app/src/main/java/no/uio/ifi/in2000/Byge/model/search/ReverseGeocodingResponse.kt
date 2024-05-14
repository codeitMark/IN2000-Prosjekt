package no.uio.ifi.in2000.Byge.model.search

import kotlinx.serialization.Serializable

@Serializable
data class ReverseGeocodingResponse(
    val results: List<ApiProperties>
)