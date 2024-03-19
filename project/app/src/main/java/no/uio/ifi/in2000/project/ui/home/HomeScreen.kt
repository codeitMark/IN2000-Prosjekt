package no.uio.ifi.in2000.project.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeScreen(vm: HomeViewModel = viewModel()){
    Column {
        Text("Hello. This is really just a quick test for checking logcat.")
        Text(text = vm.weatherData.properties.timeseries[0].data.instant.details.air_temperature.toString())
    }
}