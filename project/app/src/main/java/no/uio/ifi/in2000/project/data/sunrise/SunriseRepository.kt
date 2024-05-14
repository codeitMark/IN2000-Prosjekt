package no.uio.ifi.in2000.project.data.sunrise

import android.util.Log
import no.uio.ifi.in2000.project.model.sunrise.SunriseResponse

class SunriseRepository {
    private val sunriseDataSource = SunriseDataSource()

    suspend fun fetchSunrise(lat: Double, lon: Double, timeZone: String): SunriseResponse? {
        return sunriseDataSource.getSunrise(lat, lon, timeZone)
    }

    fun fetchSunriseTime(response: SunriseResponse?): String? {
        val sunriseTime = response?.properties?.sunrise?.time
        Log.d("SUNRISE_REPOSITORY", "Sunrise time: $sunriseTime")
        return sunriseTime
    }

    fun fetchSunsetTime(response: SunriseResponse?): String? {
        val sunsetTime = response?.properties?.sunset?.time
        Log.d("SUNSET_REPOSITORY", "Sunset time: $sunsetTime")
        return sunsetTime
    }
}