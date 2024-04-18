package no.uio.ifi.in2000.project.ui.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.project.data.alerts.MetAlertsRepository
import no.uio.ifi.in2000.project.data.forecast.LocationForecastRepository
import no.uio.ifi.in2000.project.data.search.SearchRepository
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
import no.uio.ifi.in2000.project.model.search.ApiProperties
import no.uio.ifi.in2000.project.model.search.AutoCompleteResponse


class HomeViewModel : ViewModel(){
    private val locationForecastRep = LocationForecastRepository()
    private val metAlertsRep = MetAlertsRepository()
    private val searchRep = SearchRepository()

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

    var suggestions by mutableStateOf<List<ApiProperties>>(emptyList())
        private set

    var currentFormatted by mutableStateOf("")
        private set

    var currentLat by mutableStateOf(0.0)
        private set

    var currentLon by mutableStateOf(0.0)
        private set

    var expanded by mutableStateOf(false)

    val focusRequester by mutableStateOf(FocusRequester())


    // Placeholdere for innholdet. Disse må være initialisert, derfor er det placeholdere.
    //Parametere for LocationForecast
    var lat = 0.0
    var lon = 0.0

    //Parametere for MetAlerts
    var lang = "no"
    var initialized by mutableStateOf(false)

    fun loadSuggestions(text: String) {

        viewModelScope.launch(Dispatchers.IO) {
            val response = searchRep.fetchSuggestions(text)
            var items = response?.features
            val list = mutableListOf<ApiProperties>()
            if (items != null) {
                for (item in items) {
                    list.add(item.properties)
                }
            }
            suggestions = list
        }
    }

    //fun loadData(lat: Double, lon: Double, county:String, lang: String){
    //Tester uten alerts
    fun loadData(lang: String, lat: Double, lon: Double){
        viewModelScope.launch(Dispatchers.IO){
            weatherData = locationForecastRep.fetchWeather(lat, lon)
            Log.d("VIEWMODEL_HOMESCREEN", "API-kall weather") //sjekker antall API-kall vi gjør gjennom ViewModel. Vi fetcher ikke flere ganger, så det gir mening.
            locationForecastIcons = locationForecastRep.fetchLocationForecastIcons(weatherData)
            loadAlerts(lang, lat, lon)
            if (!initialized){
                initialized = true
            }
            //Log.i("HOMEVIEWMODEL INIT", "Initiated.")
            //weatherUiState = weatherUiState.copy(weather = weather) //same functionality as weatherData = locationForecastRep.fetchWeather(lat, lon)
        }
    }

    fun loadCurrent(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = searchRep.fetchSuggestions(text)
            var items = response?.features
            val list = mutableListOf<ApiProperties>()
            if (items != null) {
                for (item in items) {
                    list.add(item.properties)
                }
            }
            suggestions = list

            currentFormatted = suggestions[0].formatted
            lat = suggestions[0].lat
            lon = suggestions[0].lon
            Log.d("TestSearch1000", "LAT: $lat --- LON: $lon")

            loadData(lang, lat, lon)
            //loadData(59.9133301, 10.7389701)
        }
    }

    //Seperat loadAlerts så vi slipper å kalle på LocationForecast på nytt hvis man bytter språk
    fun loadAlerts(lang: String, lat: Double, lon: Double){
        viewModelScope.launch(Dispatchers.IO){
            //Prevents app from crashing. There are no locations chosen.
            if (!initialized){
                return@launch
            }
            alertsData = metAlertsRep.fetchAlerts(lang, lat, lon)
            Log.d("VIEWMODEL_HOMESCREEN", "API-kall alerts")
            sortedAlerts = metAlertsRep.sortAlerts(alertsData)
            metAlertsIcons = metAlertsRep.fetchAlertIcons(alertsData)
            responseStatus = true
        }
    }
}