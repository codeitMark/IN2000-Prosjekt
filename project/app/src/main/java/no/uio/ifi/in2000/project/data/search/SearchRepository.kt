package no.uio.ifi.in2000.project.data.search

import android.util.Log
import no.uio.ifi.in2000.project.model.search.AutoCompleteResponse
import no.uio.ifi.in2000.project.model.search.ReverseGeocodingResponse

class SearchRepository() {
    private val locationForecastSource = SearchDataSource()
    suspend fun fetchSuggestions(text: String): AutoCompleteResponse? {
        return locationForecastSource.getSuggestions(text)
    }

    suspend fun fetchUserLocationData(lat: Double, lon: Double): ReverseGeocodingResponse? {
        return locationForecastSource.getUserLocation(lat, lon)
    }
}
