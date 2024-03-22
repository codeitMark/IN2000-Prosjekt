package no.uio.ifi.in2000.project.ui.home

import android.location.Geocoder
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import kotlin.math.roundToInt

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreen(vm: HomeViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    val scrollStateVertical = rememberScrollState()

    val countyNumbers = mapOf(
        "Oslo" to "03",
        "Østfold" to "31",
        "Akershus" to "32",
        "Buskerud" to "33",
        "Innlandet" to "34",
        "Vestfold" to "39",
        "Telemark" to "40",
        "Agder" to "42",
        "Rogaland" to "11",
        "Vestland" to "46",
        "Møre og Romsdal" to "15",
        "Trøndelag" to "50",
        "Nordland" to "18",
        "Troms" to "55",
        "Finmark" to "56",
        "Svalbard" to "21",
        "Jan Mayen" to "22"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollStateVertical),
        //verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        //null check for null-safety
        if (vm.weatherData == null || vm.alertsData == null){
            Text("Unable to get data.", fontSize = 35.sp, fontWeight = Bold)
        } else {
            if (!vm.responseStatus) {
                Text(text = "Loading...", fontSize = 50.sp, fontWeight = Bold)
            } else {
                Text(text = "Været", fontSize = 60.sp, fontWeight = Bold)

                val geocoder = Geocoder(LocalContext.current)
                val sted = geocoder.getFromLocation(vm.lat, vm.lon, 1)
                //ignore deprecated. There is a new method but it requires API Level 33 and above, which does not align with our minimum (API Level 24).
                if (sted != null) {
                    var kommune = sted[0].subAdminArea + ", "
                    if (sted[0].subAdminArea == null) {
                        kommune = ""
                    }
                    var fylke = sted[0].adminArea + ", "
                    if (sted[0].adminArea == null) {
                        fylke = ""
                    }
                    val land = sted[0].countryName
                    Text(text = kommune + fylke + land, fontSize = 30.sp)
                }
                Text(
                    text = "${vm.weatherData!!.properties.timeseries[0].data.instant.details.air_temperature.roundToInt()}°C",
                    fontSize = 50.sp
                )

                val iconName = vm.weatherData!!.properties.timeseries[0].data.next_1_hours.summary.symbol_code

                val svgImageUrl = "https://raw.githubusercontent.com/metno/weathericons/89e3173756248b4696b9b10677b66c4ef435db53/weather/svg/$iconName.svg"

                    AsyncImage(
                        modifier = Modifier
                            .size(280.dp),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(svgImageUrl)
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        contentDescription = "Weather icon"
                    )

                // Will only show alerts and take up space on screen if there are any active alerts in the area
                if (vm.alertsData!!.features.isNotEmpty()) {
                    Text(
                        text = "Farevarsler",
                        fontSize = 30.sp,
                        fontWeight = Bold,
                        modifier = Modifier.padding(top = 30.dp)

                    )
                    Column(
                        modifier = Modifier
                            .padding(top = 30.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {
                        for (feature in vm.alertsData!!.features) {
                            Text(text = feature.properties.eventAwarenessName, fontWeight = Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = feature.properties.instruction)
                            Spacer(modifier = Modifier.height(20.dp))
                        }
                    }
                }

                Row(modifier = Modifier.horizontalScroll(scrollState)) {
                    for (i in 2..14) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.size(120.dp, 250.dp)
                        ) {
                            val time: String =
                                vm.weatherData!!.properties.timeseries[i].time.removeRange(0, 11)
                                    .removeRange(2, 9)
                            Text(text = "kl. $time", fontSize = 30.sp)
                            val smallIconName = vm.weatherData!!.properties.timeseries[i].data.next_1_hours.summary.symbol_code
                            val smallSvgImageUrl =
                                "https://raw.githubusercontent.com/metno/weathericons/89e3173756248b4696b9b10677b66c4ef435db53/weather/svg/$smallIconName.svg"
                            AsyncImage(
                                modifier = Modifier.size(70.dp),
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(smallSvgImageUrl)
                                    .decoderFactory(SvgDecoder.Factory())
                                    .build(),
                                contentDescription = "Weather icon",
                                )
                            Text(
                                text = "${vm.weatherData!!.properties.timeseries[i].data.instant.details.air_temperature.roundToInt()}°C",
                                fontSize = 30.sp,
                                fontWeight = Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

