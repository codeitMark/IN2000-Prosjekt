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
    var expandedBy by remember {
        mutableStateOf(false)
    }
    var expandedSpråk by remember{
        mutableStateOf(false)
    }
    var valgtOmråde by remember {
        mutableStateOf("")
    }
    var valgtSpråk by remember{
        mutableStateOf("no") //Default value will be "no", norsk.
    }
    val geocoder = Geocoder(LocalContext.current, Locale.getDefault())
    var addressList: List<Address>?
    var address: Address?

    val locations = listOf("Oslo", "Trondheim", "Moss", "Ski", "Lillestrøm", "Gjesdal", "Drammen", "Bergen", "Finnmark", "Porsanger")
    val språk = listOf("no", "en")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollStateVertical),
        //verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "SEARCHBAR")
        ExposedDropdownMenuBox(expanded = expandedBy,
            onExpandedChange = { expandedBy = !expandedBy }) {
            TextField(
                modifier = Modifier
                    .menuAnchor()
                    .padding(top = 20.dp),
                value = valgtOmråde,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedBy) },
                label = { Text("Velg by!") })

            ExposedDropdownMenu(expanded = expandedBy,
                onDismissRequest = { expandedBy = false }) {
                locations.forEach { by ->
                    DropdownMenuItem(text = {Text(by)},
                        onClick = {
                            valgtOmråde = by
                            expandedBy = false
                            //Antar at vi er i Norge. Kom i Danmark med Strømmen... ???
                            addressList = geocoder.getFromLocationName("$valgtOmråde, Norway", 1) //deprecated in API Level 33.
                            if (addressList != null && addressList!!.isNotEmpty()){
                                address = addressList?.get(0)
                                vm.lat = address!!.latitude
                                vm.lon = address!!.longitude
                                vm.county = address!!.adminArea
                                vm.lang = valgtSpråk
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
        ExposedDropdownMenuBox(expanded = expandedSpråk,
            onExpandedChange = { expandedSpråk = !expandedSpråk }) {
            TextField(
                modifier = Modifier
                    .menuAnchor()
                    .padding(top = 20.dp),
                value = valgtSpråk,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedSpråk) },
                label = { Text("Velg språk!") })

            ExposedDropdownMenu(expanded = expandedSpråk,
                onDismissRequest = { expandedSpråk = false }) {
                språk.forEach {
                    DropdownMenuItem(text = {Text(it)}, //Står no "no" og "en", kan alltid lage noe Map for å skrive "Norsk" og "Engelsk" som valgene
                        onClick = {
                            valgtSpråk = it
                            expandedSpråk = false
                            vm.lang = it
                            vm.loadData(vm.lat, vm.lon, vm.county, vm.lang)
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
                    AsyncImage(
                        modifier = Modifier
                            .size(280.dp),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(vm.locationForecastIcons[0])
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        contentDescription = "Weather icon"
                    )

                    val sunriseTime = vm.sunriseTime
                    Text(text = "Soloppgang: $sunriseTime", fontSize = 20.sp)
                    val sunsetTime = vm.sunsetTime
                    Text(text = "Solnedgang: $sunsetTime", fontSize = 20.sp)
                    }
    
                    // Will only show alerts and take up space on screen if there are any active alerts in the area
                    if (vm.alertsData!!.features.isNotEmpty()) {
                        Text(
                            text = "Farevarsler",
                            fontSize = 20.sp,
                            fontWeight = Bold,
                            modifier = Modifier.padding(top = 30.dp)
    
                        )
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        )
                        {
                            var i = 0 //noe hardkodet teller for metAlertsIcons. Mulig med bedre løsning, men kan ta tid å finne.
                            // vm.sortedAlerts.forEach //Bruk denne for å fjerne duplikater. Problem: I tilfellet det er duplikater, vil vm.metAlertsIcons[i] vise feil ikoner. Det vil iterere over ikonene som om det ikke er duplikater = gust gust vs. gust flood (filtered). Den første vil vise riktige ikoner. Den andre vil vise gust gust fortsatt.
                            vm.alertsData!!.features.forEach{ //drop for løkke, make map then set of eventawarenessname and instruction. compare feature with feature? if already in there or smth like that. this processing should happen in ViewModel. Map is already unique by default :)
                                AsyncImage(
                                    modifier = Modifier
                                        .size(125.dp)
                                        .padding(top = 20.dp, bottom = 20.dp),
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(vm.metAlertsIcons!![i])
                                        .decoderFactory(SvgDecoder.Factory())
                                        .build(),
                                    contentDescription = "Icon for an alert."
                                )
                                Text(text = it.properties.eventAwarenessName, fontWeight = Bold) //Tidligere it.key (med vm.sortedAlerts.forEach)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = it.properties.instruction) //Tidligere it.value (med vm.sortedalerts.forEach)
                                Spacer(modifier = Modifier.height(20.dp))
                                i++
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
                                AsyncImage(
                                    modifier = Modifier.size(70.dp),
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(vm.locationForecastIcons[i])
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


