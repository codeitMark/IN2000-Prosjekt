package no.uio.ifi.in2000.project.ui.home

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest

@Preview
@Composable
fun HomeScreen(vm: HomeViewModel = viewModel()){
    val scrollState = rememberScrollState()

    Column (
        modifier = Modifier.fillMaxSize(),
        //verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Været", fontSize = 60.sp, fontWeight = FontWeight.Bold)
        Text(text = "Nå", fontSize = 30.sp)
        Text(text = vm.weatherData.properties.timeseries[0].data.instant.details.air_temperature.toString(), fontSize = 50.sp)

        //val iconName = vm.weatherData.properties.timeseries[0].data.next_1_hours.summary.symbol_code
        val svgImageUrl = "https://raw.githubusercontent.com/nrkno/yr-weather-symbols/master/symbols/lightmode/02d.svg"
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(svgImageUrl)
                .decoderFactory(SvgDecoder.Factory())
                .build(),
            contentDescription = "Weather icon"
        )

        Row (modifier = Modifier.horizontalScroll(scrollState)) {
            if (!vm.responseStatus) {
                Text(text = "Loading...", fontSize = 50.sp)
            } else {
                for (i in 2..14) {
                    Column (
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.size(120.dp, 150.dp)
                    ) {
                        val time: String = vm.weatherData.properties.timeseries[i].time.removeRange(0, 11).removeRange(2, 9)
                        Text(text = "kl. $time", fontSize = 30.sp)
                        Text(text = vm.weatherData.properties.timeseries[i].data.instant.details.air_temperature.toString(), fontSize = 50.sp)
                    }
                }
            }
        }
    }
}