package no.uio.ifi.in2000.project.ui.home

import android.icu.util.TimeZone
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.project.data.Constants
import no.uio.ifi.in2000.project.data.alerts.MetAlertsRepository
import no.uio.ifi.in2000.project.data.forecast.LocationForecastRepository
import no.uio.ifi.in2000.project.data.search.SearchRepository
import no.uio.ifi.in2000.project.data.streak.StreakRepository
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
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Date
import kotlin.math.roundToInt
import no.uio.ifi.in2000.project.model.sunrise.Geometry as SunriseGeometry
import no.uio.ifi.in2000.project.model.sunrise.Properties as SunriseProperties

@RequiresApi(Build.VERSION_CODES.O)
class HomeViewModel(
    private val streakRep: StreakRepository
) : ViewModel(){
    private val locationForecastRep = LocationForecastRepository()
    private val metAlertsRep = MetAlertsRepository()
    private val sunriseRep = SunriseRepository()
    private val searchRep = SearchRepository()


    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    private val today: LocalDate = LocalDate.now()
    // private val today = LocalDate.parse("2024-05-16", formatter)  // Use this to test if it works, manually take one day at a time

    var streak by mutableIntStateOf(0)

    var uvNow by mutableFloatStateOf(0.0F)

    var allBoxesExpanded by mutableStateOf(false)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            var storedData = streakRep.fetchInitialPreferences()
            Log.d("StreakTest", "Last date: ${storedData.lastDate}")
            Log.d("StreakTest", "Streak: ${storedData.streak}")

            if (storedData.lastDate == "9999-99-99") {
                streakRep.resetStreak(today.format(formatter))
            } else {
                val last = LocalDate.parse(storedData.lastDate, formatter)

                val periodLastToday = Period.between(last, today)
                if (periodLastToday.years == 0 && periodLastToday.months == 0 && periodLastToday.days == 1) {
                    streakRep.updateLastDate(today.format(formatter), (storedData.streak + 1))
                } else if (periodLastToday.years != 0 || periodLastToday.months != 0 || periodLastToday.days != 1) {
                    streakRep.resetStreak(today.format(formatter))
                }
                storedData = streakRep.fetchInitialPreferences()
                streak = storedData.streak
            }
        }

    }

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


    var loadingScreen by mutableStateOf(true)
    var loadingSearch by mutableStateOf(false)
    var noResultsToast by mutableStateOf(false)

    var alertsData: MetAlertsResponse? by mutableStateOf(MetAlertsResponse(listOf(), String(), String(), String()))
        private set

    private var sortedAlerts: LinkedHashMap<String, String> = LinkedHashMap()

    var locationForecastIcons: MutableList<String> = mutableListOf()
        private set

    private var metAlertsIcons: MutableList<String>? = mutableListOf()

    var firstLoad = true
    var showStreak by mutableStateOf(true)

    var searchField: String by mutableStateOf("")

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

    var valgtTemperatur by mutableStateOf("Celsius")

    var currentFormatted by mutableStateOf("")
        private set

    var expanded by mutableStateOf(false)
    var showSettings by mutableStateOf(false)
    var expandTable = mutableStateListOf(false, false, false, false, false, false, false)

    val focusRequester by mutableStateOf(FocusRequester())

    // Placeholdere for innholdet. Disse må være initialisert, derfor er det placeholdere.
    // Parametere for LocationForecast
    var lat = 0.0
    var lon = 0.0

    // Parametere for MetAlerts
    private var lang = "no"
    var initialized by mutableStateOf(false)

    // Parameter for Sunrise
    private var timeZone = "+00:00"
    var offset = 0
    private var name = "" //For timeZone objekt. Sjekker om stedet er i DST eller STD.
    private var dst = false

    private fun getTimeOnly(dateTimeString: String): String {
        // Litt risky måte å hente ut kun tidspunktet på
        return dateTimeString.substring(11, 16)
    }

    fun loadSuggestions(text: String) {

        viewModelScope.launch(Dispatchers.IO) {
            loadingSearch = true
            val response = searchRep.fetchSuggestions(text)
            val items = response?.features
            val list = mutableListOf<ApiProperties>()
            if (items != null) {
                for (item in items) {
                    list.add(item.properties)
                }
            }
            suggestions = list
            loadingSearch = false
            if (suggestions.isEmpty() && text.length > 3) {
                noResultsToast = true
                setTimerToast()
            }
        }
    }

    private suspend fun setTimerToast() {
        delay(3000)
        noResultsToast = false
    }

    //fun loadData(lat: Double, lon: Double, county:String, lang: String){
    //Tester uten alerts (Lite sannsynlig for at det er alerts)
    private fun loadData(lang: String, lat: Double, lon: Double, timeZone: String){
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
            loadingScreen = false
        }
    }

    fun loadCurrentFromCoordinates(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val response = searchRep.fetchUserLocationData(lat, lon)
            if (response != null) {
                val item = response.results[0]
                currentFormatted = item.formatted
                name = item.timezone.name
            sjekkDST()
            if (dst){
                timeZone = item.timezone.offset_DST //Daylight Saving Time. Sommertid.
                offset = item.timezone.offset_DST_seconds/60/60
            } else{
                timeZone = item.timezone.offset_STD //Standard Time. Norge er i DST, mens steder som New Zealand er i STD.
                offset = item.timezone.offset_STD_seconds/60/60
            }

            loadData(lang, lat, lon, timeZone)
            }
            //loadData(59.9133301, 10.7389701)
            timeoutStreak()
        }
    }

    fun loadCurrent(text: String) {
        viewModelScope.launch(Dispatchers.IO) {
            loadingScreen = true
            expandTable = mutableStateListOf(false, false, false, false, false, false, false)
            val response = searchRep.fetchSuggestions(text)
            val items = response?.features
            val list = mutableListOf<ApiProperties>()
            if (items != null) {
                for (item in items) {
                    list.add(item.properties)
                }
            }
            suggestions = list

            if (suggestions.isNotEmpty()) {
                currentFormatted = suggestions[0].formatted
                name = suggestions[0].timezone.name
                lat = suggestions[0].lat
                lon = suggestions[0].lon
                sjekkDST()
                if (dst) {
                    timeZone = suggestions[0].timezone.offset_DST //Daylight Saving Time. Sommertid.
                    offset = suggestions[0].timezone.offset_DST_seconds / 60 / 60
                } else {
                    timeZone =
                        suggestions[0].timezone.offset_STD //Standard Time. Norge er i DST, mens steder som New Zealand er i STD.
                    offset = suggestions[0].timezone.offset_STD_seconds / 60 / 60
                }
                Log.d("TestSearch1000", "LAT: $lat --- LON: $lon")
                Log.i("timeZonesjekk", timeZone)
                loadingScreen = false
                loadData(lang, lat, lon, timeZone)
                // loadData(59.9133301, 10.7389701)
            }
        }
    }

    //Seperat loadAlerts så vi slipper å kalle på LocationForecast på nytt hvis man bytter språk
    private fun loadAlerts(lang: String, lat: Double, lon: Double){
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

    private fun sjekkDST(){
        val tz = TimeZone.getTimeZone(name)
        val currentDate = Date()
        dst = tz.inDaylightTime(currentDate)
    }

    // Hjelpefunksjon for å hente ut maksimums- og minimumstemperaturene for en dag
    fun getTemperatureForDay(response: LocationForecastResponse, date: String): Pair<Double?, Double?> {
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

    fun getDayDataDetails(inDate: String): List<List<Any>> {
        val timeseries = weatherData?.properties?.timeseries

        val retList = mutableListOf<List<Any>>()

        if (timeseries != null) {
            for (item in timeseries) {
                val date = item.time.split("T")[0]
                val time = item.time.split("T")[1]// Hent bare dato-delen av tiden
                if (date == inDate) {
                    if (time == "00:00:00Z" ||
                        time == "06:00:00Z" ||
                        time == "12:00:00Z" ||
                        time == "18:00:00Z") {

                        val values = mutableListOf<Any>()
                        values.add(Constants.timeFormat[time].toString())


                        val temperatureText = if (valgtTemperatur == "Celsius") {
                            "${((item.data.next_6_hours.details.air_temperature_max + item.data.next_6_hours.details.air_temperature_min) / 2).roundToInt()}°C"
                        } else {
                            "${(((item.data.next_6_hours.details.air_temperature_max + item.data.next_6_hours.details.air_temperature_min) / 2) * 1.8 + 32).roundToInt()}°F"
                        }

                        values.add(temperatureText)
                        values.add(item.data.instant.details.wind_speed.roundToInt())
                        values.add(item.data.next_6_hours.details.precipitation_amount)
                        values.add(item.data.next_6_hours.summary.symbol_code)
                        retList.add(values)
                    }
                }
            }
        }

        return retList
    }
    // I ViewModel
    fun getAlertIcons(): Map<String, String> {
        return metAlertsRep.alertIcons
    }
    private suspend fun timeoutStreak() {
        delay(4000)
        showStreak = false
    }
}
