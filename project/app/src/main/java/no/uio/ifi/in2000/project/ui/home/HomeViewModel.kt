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
import no.uio.ifi.in2000.project.data.sunrise.SunriseRepository
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
import no.uio.ifi.in2000.project.model.sunrise.SolarMidnight
import no.uio.ifi.in2000.project.model.sunrise.SolarNoon
import no.uio.ifi.in2000.project.model.sunrise.Sunrise
import no.uio.ifi.in2000.project.model.sunrise.SunriseResponse
import no.uio.ifi.in2000.project.model.sunrise.Sunset
import no.uio.ifi.in2000.project.model.sunrise.When
import no.uio.ifi.in2000.project.model.sunrise.Geometry as SunriseGeometry
import no.uio.ifi.in2000.project.model.sunrise.Properties as SunriseProperties

class HomeViewModel : ViewModel(){
    private val locationForecastRep = LocationForecastRepository()
    private val metAlertsRep = MetAlertsRepository()
    private val sunriseRep = SunriseRepository()

    var responseStatus by mutableStateOf(false) //made mutableStateOf so HomeScreen updates if responseStatus changes. Without this it will NOT update since the combination of weatherData and alertsData in init takes too long.

    // Filled with placeholders. This is because we have to create an instance of the class.
    var weatherData: LocationForecastResponse? by mutableStateOf(
        LocationForecastResponse(
        String(),
            Geometry(
                String(),
                listOf()),
            Properties(
                Meta(String(),
                    Units(String(), String(), String(), String(), String(), String(), String())),
                listOf(
                    TimeSeries(
                    String(),
                        Data(
                            Instant(
                                Instant_Details(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f)
                            ),
                            NextHours(
                                Summary(String()),
                                NextHours_Details(0.0f)
                            ),
                            NextHours(
                                Summary(String()),
                                NextHours_Details(0.0f)),
                            NextHours(Summary(String()),
                                NextHours_Details(0.0f)
                            )
                        )
                    )
                )
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

    // Filled with placeholders. This is because we have to create an instance of the class.
    var sunrise: SunriseResponse? by mutableStateOf(
        SunriseResponse(
            String(),
            SunriseGeometry(
                String(),
                listOf()
            ),
            When(
                listOf()
            ),
            SunriseProperties(
                String(),
                Sunrise(
                    String(),
                    0.0
                ),
                Sunset(
                    String(),
                    0.0
                ),
                SolarNoon(
                    String(),
                    0.0,
                    false
                ),
                SolarMidnight(
                    String(),
                    0.0,
                    false
                )
            )
        )
    )

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

            sunrise = sunriseRep.fetchSunrise(lat, lon)
            Log.d("VIEWMODEL_HOMESCREEN", "API-kall sunrise")

            responseStatus = true
            if (!initialized){
                initialized = true
            }
            //Log.i("HOMEVIEWMODEL INIT", "Initiated.")
            //weatherUiState = weatherUiState.copy(weather = weather) //same functionality as weatherData = locationForecastRep.fetchWeather(lat, lon)
        }
    }
}