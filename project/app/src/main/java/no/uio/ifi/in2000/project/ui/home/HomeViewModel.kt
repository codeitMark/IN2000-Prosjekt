package no.uio.ifi.in2000.project.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import no.uio.ifi.in2000.project.data.forecast.LocationForecastRepository
import no.uio.ifi.in2000.project.model.forecast.LocationForecastResponse

data class weatherUiState(val data: LocationForecastResponse)

class HomeViewModel : ViewModel(){
    private val rep = LocationForecastRepository()

    //var weatherUiState by mutableStateOf(weatherUiState)

    init {
        viewModelScope.launch(Dispatchers.IO){
            val weather = rep.fetchWeather()
            //weatherUiState = weatherUiState.copy(weather = weather)
        }
    }
}