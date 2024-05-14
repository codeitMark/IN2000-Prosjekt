package no.uio.ifi.in2000.Byge.data.search

import no.uio.ifi.in2000.Byge.model.search.AutoCompleteResponse
import no.uio.ifi.in2000.Byge.model.search.ReverseGeocodingResponse

class SearchRepository {
    private val locationForecastSource = SearchDataSource()
    suspend fun fetchSuggestions(text: String, lang: String): AutoCompleteResponse? {
        return locationForecastSource.getSuggestions(text, lang)
    }

    suspend fun fetchUserLocationData(lat: Double, lon: Double, lang: String): ReverseGeocodingResponse? {
        return locationForecastSource.getUserLocation(lat, lon, lang)
    }
}
