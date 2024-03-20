package no.uio.ifi.in2000.project.ui.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.project.data.forecast.LocationForecastDataSource
import no.uio.ifi.in2000.project.data.forecast.LocationForecastRepository
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
    private val rep = LocationForecastRepository(LocationForecastDataSource())
    var responseStatus = false

    //var weatherUiState by mutableStateOf(weatherUiState) //the same as weatherData


    private val emptyResponse = LocationForecastResponse("empty", Geometry("empty", emptyList()), Properties(
        Meta("empty", Units("empty", "empty", "empty", "empty", "empty", "empty", "empty")),
        listOf(TimeSeries("empty", Data(Instant(Instant_Details(0.toFloat(), 0.toFloat(), 0.toFloat(), 0.toFloat(), 0.toFloat(), 0.toFloat())), NextHours(
            Summary("empty"), NextHours_Details(0.toFloat())
        ), NextHours(Summary("empty"), NextHours_Details(0.toFloat())), NextHours(Summary("empty"), NextHours_Details(0.toFloat())))))
    )) //Used as a placeholder for weatherData's content (mutableStateOf)
    var weatherData by mutableStateOf(emptyResponse)
        private set

    init {
        viewModelScope.launch(Dispatchers.IO){

            // bare midlertidige hardkodede koordinater for MVP-en
            val lat = 58.7753
            val lon = 5.90566

            weatherData = rep.fetchWeather(lat, lon)
            responseStatus = true
            //Log.i("HOMEVIEWMODEL INIT", "Initiated.")
            //weatherUiState = weatherUiState.copy(weather = weather) //same functionality as weatherData = rep.fetchWeather(lat, lon)
        }
    }
}