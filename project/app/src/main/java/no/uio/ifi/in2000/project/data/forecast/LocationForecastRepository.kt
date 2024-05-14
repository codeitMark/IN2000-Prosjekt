package no.uio.ifi.in2000.project.data.forecast

import no.uio.ifi.in2000.project.model.forecast.LocationForecastResponse

class LocationForecastRepository {
    private val locationForecastSource = LocationForecastDataSource()
    suspend fun fetchWeather(lat: Double, lon: Double): LocationForecastResponse? {
        return locationForecastSource.getWeather(lat, lon)
    }

    fun fetchLocationForecastIcons (weather: LocationForecastResponse?): MutableList<String> {
        val icons = mutableListOf<String>()
        weather?.properties?.timeseries?.forEach{
            if (it.data.next_1_hours != null){ //ignore warning, it is not redundant. next_1_hours can be null!
                icons.add(it.data.next_1_hours.summary.symbol_code) //Own icons
            }
        }
        return icons
    }
}
