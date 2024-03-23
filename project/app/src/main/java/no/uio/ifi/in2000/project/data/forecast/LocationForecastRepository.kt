package no.uio.ifi.in2000.project.data.forecast

import no.uio.ifi.in2000.project.model.forecast.LocationForecastResponse

class LocationForecastRepository() {
    private val locationForecastSource = LocationForecastDataSource()
    suspend fun fetchWeather(lat: Double, lon: Double): LocationForecastResponse? {
        return locationForecastSource.getWeather(lat, lon)
    }
}
