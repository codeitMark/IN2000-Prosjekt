package no.uio.ifi.in2000.project.ui.home

import android.location.Geocoder
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
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
fun HomeScreen(vm: HomeViewModel = viewModel()){
    val scrollState = rememberScrollState()

    val weatherConstants = mapOf(
        "clearsky_day" to "01d",
        "clearsky_night" to "01n",
        "clearsky_polartwilight" to "01m",
        "fair_day" to "02d",
        "fair_night" to "02n",
        "fair_polartwilight" to "02m",
        "partlycloudy_day" to "03d",
        "partlycloudy_night" to "03n",
        "partlycloudy_polartwilight" to "03m",
        "cloudy" to "04",
        "rainshowers_day" to "05d",
        "rainshowers_night" to "05n",
        "rainshowers_polartwilight" to "05m",
        "rainshowersandthunder_day" to "06d",
        "rainshowersandthunder_night" to "06n",
        "rainshowersandthunder_polartwilight" to "06m",
        "sleetshowers_day" to "07d",
        "sleetshowers_night" to "07n",
        "sleetshowers_polartwilight" to "07m",
        "snowshowers_day" to "08d",
        "snowshowers_night" to "08n",
        "snowshowers_polartwilight" to "08m",
        "rain" to "09",
        "heavyrain" to "10",
        "heavyrainandthunder" to "11",
        "sleet" to "12",
        "snow" to "13",
        "snowandthunder" to "14",
        "fog" to "15",
        "sleetshowersandthunder_day" to "20d",
        "sleetshowersandthunder_night" to "20n",
        "sleetshowersandthunder_polartwilight" to "20m",
        "snowshowersandthunder_day" to "21d",
        "snowshowersandthunder_night" to "21n",
        "snowshowersandthunder_polartwilight" to "21m",
        "rainandthunder" to "22",
        "sleetandthunder" to "23",
        "lightrainshowersandthunder_day" to "24d",
        "lightrainshowersandthunder_night" to "24n",
        "lightrainshowersandthunder_polartwilight" to "24m",
        "heavyrainshowersandthunder_day" to "25d",
        "heavyrainshowersandthunder_night" to "25n",
        "heavyrainshowersandthunder_polartwilight" to "25m",
        "lightssleetshowersandthunder_day" to "26d",
        "lightssleetshowersandthunder_night" to "26n",
        "lightssleetshowersandthunder_polartwilight" to "26m",
        "heavysleetshowersandthunder_day" to "27d",
        "heavysleetshowersandthunder_night" to "27n",
        "heavysleetshowersandthunder_polartwilight" to "27m",
        "lightssnowshowersandthunder_day" to "28d",
        "lightssnowshowersandthunder_night" to "28n",
        "lightssnowshowersandthunder_polartwilight" to "28m",
        "heavysnowshowersandthunder_day" to "29d",
        "heavysnowshowersandthunder_night" to "29n",
        "heavysnowshowersandthunder_polartwilight" to "29m",
        "lightrainandthunder" to "30",
        "lightsleetandthunder" to "31",
        "heavysleetandthunder" to "32",
        "lightsnowandthunder" to "33",
        "heavysnowandthunder" to "34",
        "lightrainshowers_day" to "40d",
        "lightrainshowers_night" to "40n",
        "lightrainshowers_polartwilight" to "40m",
        "heavyrainshowers_day" to "41d",
        "heavyrainshowers_night" to "41n",
        "heavyrainshowers_polartwilight" to "41m",
        "lightsleetshowers_day" to "42d",
        "lightsleetshowers_night" to "42n",
        "lightsleetshowers_polartwilight" to "42m",
        "heavysleetshowers_day" to "43d",
        "heavysleetshowers_night" to "43n",
        "heavysleetshowers_polartwilight" to "43m",
        "lightsnowshowers_day" to "44d",
        "lightsnowshowers_night" to "44n",
        "lightsnowshowers_polartwilight" to "44m",
        "heavysnowshowers_day" to "45d",
        "heavysnowshowers_night" to "45n",
        "heavysnowshowers_polartwilight" to "45m",
        "lightrain" to "46",
        "lightsleet" to "47",
        "heavysleet" to "48",
        "lightsnow" to "49",
        "heavysnow" to "50"
    )

    Column (
        modifier = Modifier.fillMaxSize(),
        //verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val geocoder = Geocoder(LocalContext.current);
        val sted = geocoder.getFromLocation(vm.lat, vm.lon, 1);
        Text(text = "Været", fontSize = 60.sp, fontWeight = FontWeight.Bold)
        if (sted != null) {
            Text(text = sted[0].subAdminArea + ", "+sted[0].countryName, fontSize = 30.sp)
        }
        Text(text = vm.weatherData.properties.timeseries[0].data.instant.details.air_temperature.roundToInt().toString() + "°C", fontSize = 50.sp)

        val iconName = weatherConstants[vm.weatherData.properties.timeseries[0].data.next_1_hours.summary.symbol_code]
        val svgImageUrl = "https://raw.githubusercontent.com/nrkno/yr-weather-symbols/master/symbols/lightmode/$iconName.svg"
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
                        modifier = Modifier.size(120.dp, 250.dp)
                    ) {
                        val time: String = vm.weatherData.properties.timeseries[i].time.removeRange(0, 11).removeRange(2, 9)
                        Text(text = "kl. $time", fontSize = 30.sp)
                        AsyncImage(
                            modifier = Modifier
                                .size(70.dp),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(svgImageUrl)
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentDescription = "Weather icon"
                        )
                        Text(text = vm.weatherData.properties.timeseries[i].data.instant.details.air_temperature.roundToInt().toString() + "°C", fontSize = 30.sp, fontWeight = Bold)

                    }
                }
            }
        }
    }
}