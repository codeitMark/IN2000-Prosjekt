package no.uio.ifi.in2000.project.ui.home

import android.icu.util.TimeZone
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.project.data.alerts.MetAlertsRepository
import no.uio.ifi.in2000.project.data.forecast.LocationForecastRepository
import no.uio.ifi.in2000.project.data.search.SearchRepository
import no.uio.ifi.in2000.project.data.sunrise.SunriseRepository
import no.uio.ifi.in2000.project.model.alerts.MetAlertsResponse
import no.uio.ifi.in2000.project.model.forecast.Geometry
import no.uio.ifi.in2000.project.model.forecast.LocationForecastResponse
import no.uio.ifi.in2000.project.model.forecast.Meta
import no.uio.ifi.in2000.project.model.forecast.Properties
import no.uio.ifi.in2000.project.model.forecast.Units
import no.uio.ifi.in2000.project.model.search.ApiProperties
import no.uio.ifi.in2000.project.model.sunrise.SolarMidnight
import no.uio.ifi.in2000.project.model.sunrise.SolarNoon
import no.uio.ifi.in2000.project.model.sunrise.Sunrise
import no.uio.ifi.in2000.project.model.sunrise.SunriseResponse
import no.uio.ifi.in2000.project.model.sunrise.Sunset
import no.uio.ifi.in2000.project.model.sunrise.When
import java.util.Date
import no.uio.ifi.in2000.project.model.sunrise.Geometry as SunriseGeometry
import no.uio.ifi.in2000.project.model.sunrise.Properties as SunriseProperties


class HomeViewModel : ViewModel(){
    private val locationForecastRep = LocationForecastRepository()
    private val metAlertsRep = MetAlertsRepository()
    private val sunriseRep = SunriseRepository()
    private val searchRep = SearchRepository()

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
        private set

    var sunriseTime by mutableStateOf("")
        private set

    var sunsetTime by mutableStateOf("")
        private set
    var suggestions by mutableStateOf<List<ApiProperties>>(emptyList())
        private set

    var currentFormatted by mutableStateOf("")
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

    //Parameter for Sunrise
    var timeZone = "+00:00"
    var offset = 0
    private var name = "" //For timeZone objekt. Sjekker om stedet er i DST eller STD.
    private var dst = false

    private fun getTimeOnly(dateTimeString: String): String {
        // Litt risky måte å hente ut kun tidspunktet på
        return dateTimeString.substring(11, 16)
    }

    fun loadSuggestions(text: String) {

        viewModelScope.launch(Dispatchers.IO) {
            val response = searchRep.fetchSuggestions(text)
            val items = response?.features
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
    //Tester uten alerts (Lite sannsynlig for at det er alerts)
    fun loadData(lang: String, lat: Double, lon: Double, timeZone: String){
        viewModelScope.launch(Dispatchers.IO){
            weatherData = locationForecastRep.fetchWeather(lat, lon)
            Log.d("VIEWMODEL_HOMESCREEN", "API-kall weather") //sjekker antall API-kall vi gjør gjennom ViewModel. Vi fetcher ikke flere ganger, så det gir mening.
            locationForecastIcons = locationForecastRep.fetchLocationForecastIcons(weatherData)
            sunrise = sunriseRep.fetchSunrise(lat, lon, timeZone)
            sunriseTime = sunriseRep.fetchSunriseTime(sunrise)?.let { getTimeOnly(it) }.toString()
            sunsetTime = sunriseRep.fetchSunsetTime(sunrise)?.let { getTimeOnly(it) }.toString()
            Log.d("VIEWMODEL_HOMESCREEN", "API-kall sunrise")

            responseStatus = true
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
            val items = response?.features
            val list = mutableListOf<ApiProperties>()
            if (items != null) {
                for (item in items) {
                    list.add(item.properties)
                }
            }
            suggestions = list

            //kræsjer hawaiian isles. Trenger vi null-safety sjekk?
            currentFormatted = suggestions[0].formatted
            name = suggestions[0].timezone.name
            lat = suggestions[0].lat
            lon = suggestions[0].lon
            sjekkDST(name)
            if (dst){
                timeZone = suggestions[0].timezone.offset_DST //Daylight Saving Time. Sommertid.
                offset = suggestions[0].timezone.offset_DST_seconds/60/60
            } else{
                timeZone = suggestions[0].timezone.offset_STD //Standard Time. Norge er i DST, mens steder som New Zealand er i STD.
                offset = suggestions[0].timezone.offset_STD_seconds/60/60
            }
            Log.d("TestSearch1000", "LAT: $lat --- LON: $lon")
            Log.i("timeZonesjekk", timeZone)

            loadData(lang, lat, lon, timeZone)
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

    private fun sjekkDST(sted: String){
        val tz = TimeZone.getTimeZone(name)
        val currentDate = Date()
        dst = tz.inDaylightTime(currentDate)
    }

    // Hjelpefunksjon for å hente ut maksimums- og minimumstemperaturene for en dag
    fun getTemperatureForDay(response: LocationForecastResponse, date: String): Pair<Double?, Double?>? {
        val timeseries = response.properties.timeseries

        var maxTemp: Double? = null
        var minTemp: Double? = null

        for (item in timeseries) {
            val dateTime = item.time.split("T")[0] // Hent bare dato-delen av tiden
            if (dateTime == date) {
                val next6Hours = item.data.next_6_hours
                if (next6Hours != null) { //next_6_hours can be null, ignore warning!
                    val tempMax = next6Hours.details.air_temperature_max.toDouble()
                    val tempMin = next6Hours.details.air_temperature_min.toDouble()

                    // Oppdater maksimumstemperaturen hvis den er høyere enn den nåværende maksimumstemperaturen
                    if (maxTemp == null || tempMax > maxTemp) {
                        maxTemp = tempMax
                    }

                    // Oppdater minimumstemperaturen hvis den er lavere enn den nåværende minimumstemperaturen
                    if (minTemp == null || tempMin < minTemp) {
                        minTemp = tempMin
                    }
                }
            }
        }
        return Pair(maxTemp, minTemp)
    }
}