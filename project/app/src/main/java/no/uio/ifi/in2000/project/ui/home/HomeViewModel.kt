package no.uio.ifi.in2000.project.ui.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.project.data.alerts.MetAlertsDataSource
import no.uio.ifi.in2000.project.data.alerts.MetAlertsRepository
import no.uio.ifi.in2000.project.data.forecast.LocationForecastDataSource
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

//data class weatherUiState(val data: LocationForecastResponse)

class HomeViewModel : ViewModel(){
    private val locationForecastRep = LocationForecastRepository(LocationForecastDataSource())
    private val metAlertsRep = MetAlertsRepository(MetAlertsDataSource())
    var responseStatus by mutableStateOf(false) //made mutableStateOf so HomeScreen updates if responseStatus changes. Without this it will NOT update since the combination of weatherData and alertsData in init takes too long.

    //var weatherUiState by mutableStateOf(weatherUiState) //the same as weatherData


    private val locationForecastEmptyResponse = LocationForecastResponse("empty", Geometry("empty", emptyList()), Properties(
        Meta("empty", Units("empty", "empty", "empty", "empty", "empty", "empty", "empty")),
        listOf(TimeSeries("empty", Data(Instant(Instant_Details(0.toFloat(), 0.toFloat(), 0.toFloat(), 0.toFloat(), 0.toFloat(), 0.toFloat())), NextHours(
            Summary("empty"), NextHours_Details(0.toFloat())
        ), NextHours(Summary("empty"), NextHours_Details(0.toFloat())), NextHours(Summary("empty"), NextHours_Details(0.toFloat())))))
    )) //Used as a placeholder for weatherData's content (mutableStateOf)

    private val metAlertsEmptyResponse = MetAlertsResponse(emptyList(), "empty", "empty", "empty") //Used as a placeholder for alertsData's content (mutableStateOf)

    var weatherData by mutableStateOf(locationForecastEmptyResponse)
        private set
    //private set gjør at variabelen kan kun endres inni klassen. Dette sørger for at det ikke kan endres av noe fra HomeScreen/utenfor HVM.

    var alertsData by mutableStateOf(metAlertsEmptyResponse)
        private set

    // bare midlertidige hardkodede koordinater for MVP-en
    val lat = 58.7753
    val lon = 5.90566
    val lang = "no"

    init {
        viewModelScope.launch(Dispatchers.IO){
            weatherData = locationForecastRep.fetchWeather(lat, lon)
            alertsData = metAlertsRep.fetchAlerts(lat, lon, lang)
            responseStatus = true
            //Log.i("HOMEVIEWMODEL INIT", "Initiated.")
            //weatherUiState = weatherUiState.copy(weather = weather) //same functionality as weatherData = locationForecastRep.fetchWeather(lat, lon)
        }
    }
}