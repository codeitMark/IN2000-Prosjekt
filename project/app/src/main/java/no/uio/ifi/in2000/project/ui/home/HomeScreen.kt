package no.uio.ifi.in2000.project.ui.home

import android.location.Address
import android.location.Geocoder
import android.util.Log
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
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreen(vm: HomeViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    val scrollStateVertical = rememberScrollState()
    var expanded by remember {
        mutableStateOf(false)
    }
    var valgtOmråde by remember {
        mutableStateOf("")
    }
    val geocoder = Geocoder(LocalContext.current, Locale.getDefault())
    var addressList: List<Address>?
    var address: Address?

    val locations = listOf("Oslo", "Trondheim", "Moss", "Ski", "Lillestrøm", "Gjesdal", "Drammen", "Bergen", "Finnmark", "Porsanger")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollStateVertical),
        //verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ExposedDropdownMenuBox(expanded = expanded,
            onExpandedChange = { expanded = !expanded }) {
            TextField(
                modifier = Modifier
                    .menuAnchor()
                    .padding(top = 20.dp),
                value = valgtOmråde,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                label = { Text("Velg by!") })

            ExposedDropdownMenu(expanded = expanded,
                onDismissRequest = { expanded = false }) {
                locations.forEach { by ->
                    DropdownMenuItem(text = { Text("$by") },
                        onClick = {
                            valgtOmråde = by //Antar at vi er i Norge. Kom i Danmark med Strømmen... ???
                            expanded = false
                            addressList = geocoder.getFromLocationName("$valgtOmråde, Norway", 1) //deprecated in API Level 33.
                            if (addressList != null && addressList!!.isNotEmpty()){
                                address = addressList?.get(0)
                                vm.lat = address!!.latitude
                                vm.lon = address!!.longitude
                                vm.county = address!!.adminArea
                                vm.lang = "no" //bare hardkodet inn, kan alltid legge til noe for å bytte mellom no og en. Kun egentlig for MetAlerts da.
                                vm.loadData(vm.lat, vm.lon, vm.county, vm.lang)
                                Log.i("HOMESCREEN", "addressList: $addressList")
                            } else{
                                Log.w("HOMESCREEN", "addressList is null or empty! addressList: $addressList")
                            }
                        }
                    )
                }
            }
        }
        if (vm.initialized) {
            //null check for null-safety
            if (vm.weatherData == null || vm.alertsData == null) {
                Text("Unable to get data.", fontSize = 35.sp, fontWeight = Bold)
            } else {
                if (!vm.responseStatus) {
                    Text(text = "Loading...", fontSize = 50.sp, fontWeight = Bold)
                } else {
                    Text(text = "Været", fontSize = 60.sp, fontWeight = Bold)
                    val sted = geocoder.getFromLocation(vm.lat, vm.lon, 1)
                    //ignore deprecated. There is a new method but it requires API Level 33 and above, which does not align with our minimum (API Level 24).
                    if (sted != null) {
                        var kommune = sted[0].subAdminArea + ", "
                        Log.i("KOMMUNE", kommune) //kaller på dette 3 ganger, interessant! Må løses :)
                        if (sted[0].subAdminArea == null) {
                            kommune = ""
                        }
                        var lokalBy = sted[0].locality + ", "
                        Log.i("LOKALBY", lokalBy)
                        if (sted[0].locality == null || sted[0].subAdminArea != null){
                            lokalBy = ""
                        }
                        var fylke = sted[0].adminArea + ", "
                        Log.i("FYLKE", fylke)
                        if (sted[0].adminArea == null || sted[0].subAdminArea != null || sted[0].locality != null) { //viser kun fylke om det er den eneste.
                            fylke = ""
                        }
                        val land = sted[0].countryName
                        Text(text = kommune + lokalBy + fylke + land, fontSize = 30.sp)
                    }
                    Text(
                        text = "${vm.weatherData!!.properties.timeseries[0].data.instant.details.air_temperature.roundToInt()}°C",
                        fontSize = 50.sp
                    )
    
                    val iconName =
                        vm.weatherData!!.properties.timeseries[0].data.next_1_hours.summary.symbol_code
    
                    val svgImageUrl =
                        "https://raw.githubusercontent.com/metno/weathericons/89e3173756248b4696b9b10677b66c4ef435db53/weather/svg/$iconName.svg"
    
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
                            vm.sortedAlerts.forEach { //drop for løkke, make map then set of eventawarenessname and instruction. compare feature with feature? if already in there or smth like that. this processing should happen in ViewModel. Map is already unique by default :)
                                Text(text = it.key, fontWeight = Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = it.value)
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
                                val smallIconName =
                                    vm.weatherData!!.properties.timeseries[i].data.next_1_hours.summary.symbol_code
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
}


