package no.uio.ifi.in2000.project.ui.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.project.data.alerts.MetAlertsRepository
import no.uio.ifi.in2000.project.data.forecast.LocationForecastRepository
import no.uio.ifi.in2000.project.model.alerts.MetAlertsResponse
import no.uio.ifi.in2000.project.model.forecast.Data
import no.uio.ifi.in2000.project.model.forecast.Geometry
import no.uio.ifi.in2000.project.model.forecast.Instant
import no.uio.ifi.in2000.project.model.forecast.Instant_Details
import no.uio.ifi.in2000.project.model.forecast.LocationForecastResponse
import no.uio.ifi.in2000.project.model.forecast.Meta
import no.uio.ifi.in2000.project.model.forecast.NextHours
import no.uio.ifi.in2000.project.model.forecast.NextHours_Details
import no.uio.ifi.in2000.project.model.forecast.Properties
import no.uio.ifi.in2000.project.model.forecast.Summary
import no.uio.ifi.in2000.project.model.forecast.TimeSeries
import no.uio.ifi.in2000.project.model.forecast.Units


class HomeViewModel : ViewModel(){
    private val locationForecastRep = LocationForecastRepository()
    private val metAlertsRep = MetAlertsRepository()

    var responseStatus by mutableStateOf(false) //made mutableStateOf so HomeScreen updates if responseStatus changes. Without this it will NOT update since the combination of weatherData and alertsData in init takes too long.

    // Filled with placeholders. This is because we have to create an instance of the class.
    var weatherData: LocationForecastResponse? by mutableStateOf(
        LocationForecastResponse(
            type = String(), geometry = Geometry(
                type = String(),
                coordinates = listOf()
            ), properties = Properties(
                meta = Meta(
                    updated_at = String(), units = Units(
                        air_pressure_at_sea_level = String(),
                        air_temperature = String(),
                        air_temperature_max = String(),
                        air_temperature_min = String(),
                        air_temperature_percentile_10 = String(),
                        air_temperature_percentile_90 = String(),
                        cloud_area_fraction = String(),
                        cloud_area_fraction_high = String(),
                        cloud_area_fraction_low = String(),
                        cloud_area_fraction_medium = String(),
                        dew_point_temperature = String(),
                        fog_area_fraction = String(),
                        precipitation_amount = String(),
                        precipitation_amount_max = String(),
                        precipitation_amount_min = String(),
                        probability_of_precipitation = String(),
                        probability_of_thunder = String(),
                        relative_humidity = String(),
                        ultraviolet_index_clear_sky = String(),
                        wind_from_direction = String(),
                        wind_speed = String(),
                        wind_speed_of_gust = String(),
                        wind_speed_percentile_10 = String(),
                        wind_speed_percentile_90 = String()
                    )
                ), timeseries = listOf()
            )
        )
    )
        private set
    //private set gjør at variabelen kan kun endres inni klassen. Dette sørger for at det ikke kan endres av noe fra HomeScreen/utenfor HVM.

    var alertsData: MetAlertsResponse? by mutableStateOf(MetAlertsResponse(listOf(), String(), String(), String()))
        private set

    var sortedAlerts: LinkedHashMap<String, String> = LinkedHashMap()
        private set

    var locationForecastIcons: MutableList<String> = mutableListOf<String>()
        private set

    var metAlertsIcons: MutableList<String>? = mutableListOf<String>()
        private set

    // Placeholdere for innholdet. Disse må være initialisert, derfor er det placeholdere.
    //Parametere for LocationForecast
    var lat = 0.0
    var lon = 0.0

    //Parametere for MetAlerts
    var county = ""
    var lang = ""
    var initialized by mutableStateOf(false)

    fun loadData(lat: Double, lon: Double, county:String, lang: String){
        viewModelScope.launch(Dispatchers.IO){
            weatherData = locationForecastRep.fetchWeather(lat, lon)
            Log.d("VIEWMODEL_HOMESCREEN", "API-kall weather") //sjekker antall API-kall vi gjør gjennom ViewModel. Vi fetcher ikke flere ganger, så det gir mening.
            locationForecastIcons = locationForecastRep.fetchLocationForecastIcons(weatherData)
            alertsData = metAlertsRep.fetchAlerts(county, lang)
            Log.d("VIEWMODEL_HOMESCREEN", "API-kall alerts")
            sortedAlerts = metAlertsRep.sortAlerts(alertsData)
            metAlertsIcons = metAlertsRep.fetchAlertIcons(alertsData)
            responseStatus = true
            if (!initialized){
                initialized = true
            }
            //Log.i("HOMEVIEWMODEL INIT", "Initiated.")
            //weatherUiState = weatherUiState.copy(weather = weather) //same functionality as weatherData = locationForecastRep.fetchWeather(lat, lon)
        }
    }
}