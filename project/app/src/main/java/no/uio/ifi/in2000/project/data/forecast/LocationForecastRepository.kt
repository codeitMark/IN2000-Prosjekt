package no.uio.ifi.in2000.project.data.forecast

import no.uio.ifi.in2000.project.model.forecast.LocationForecastResponse

class LocationForecastRepository {
    private val LocationForecastSource = LocationForecastDataSource()

    suspend fun fetchWeather(): LocationForecastResponse{
        return LocationForecastSource.getWeather()
    }
}