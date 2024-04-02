package no.uio.ifi.in2000.project.data.forecast

import no.uio.ifi.in2000.project.model.forecast.LocationForecastResponse

class LocationForecastRepository() {
    private val locationForecastSource = LocationForecastDataSource()
    suspend fun fetchWeather(lat: Double, lon: Double): LocationForecastResponse? {
        return locationForecastSource.getWeather(lat, lon)
    }

    suspend fun fetchLocationForecastIcons(weather: LocationForecastResponse?): MutableList<String> {
        val icons = mutableListOf<String>()
        weather?.properties?.timeseries?.forEach{
            if (it.data.next_1_hours != null){ //ignore warning, it is not redundant. next_1_hours can be null!
                icons.add("https://raw.githubusercontent.com/metno/weathericons/89e3173756248b4696b9b10677b66c4ef435db53/weather/svg/${it.data.next_1_hours.summary.symbol_code}.svg")
            }
        }
        return icons
    }
}
