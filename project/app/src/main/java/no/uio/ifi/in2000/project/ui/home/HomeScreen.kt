package no.uio.ifi.in2000.project.ui.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.zIndex
import no.uio.ifi.in2000.project.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun HomeScreen(lat: Double, lon: Double, vm: HomeViewModel) {

    val scrollState = rememberScrollState()
    val scrollStateVertical = rememberScrollState()

    if (lat != 0.0 && lon != 0.0 && vm.firstLoad) {
        vm.loadCurrentFromCoordinates(lat, lon)
        vm.firstLoad = false
    }

    //duplicate of same thing in SearchBar()
    val keyboardController = LocalSoftwareKeyboardController.current

    val allBoxesExpanded = remember { mutableStateOf(false) }
    val rotationState = animateFloatAsState(
        targetValue = if (allBoxesExpanded.value) 180f else 0f,
        label = ""
    )

    Column(
        modifier = Modifier
            .background(Color(0xFF272D34))
            .fillMaxSize()
            .verticalScroll(scrollStateVertical)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { //hides keyboard when clicking out
                    keyboardController?.hide()
                    vm.expanded = false //hides suggestions when clicking out.
                    vm.showSettings = false
                    //hides settings when user clicking out
                })
            },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Innstillingsboksen, åpnes øverst
        AnimatedVisibility(visible = vm.showSettings) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = LinearOutSlowInEasing
                        )
                    ),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF38424D))
            ) {

                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    var celsius by remember { mutableLongStateOf(0xFFFFFFFF) }
                    var fahrenheit by remember { mutableLongStateOf(0xFF8C9299) }
                    var checked by remember { mutableStateOf(false) }

                    Row (
                        modifier = Modifier.padding(vertical = 10.dp)
                    ) {
                        SettingsText(17, color = celsius, content = "Celsius", 10, 5, 0, 5)
                        SettingsText(17, color = 0xFFFFFFFF, content = " / ", 0, 5, 0, 5)
                        SettingsText(17, color = fahrenheit, content = "Fahrenheit", 0, 5, 50, 5)

                    Switch(
                        modifier = Modifier
                            .size(2.dp)
                            .padding(0.dp, 18.dp, 0.dp, 0.dp),
                        checked = checked,
                        onCheckedChange = {
                            checked = it
                            if (celsius == 0xFFFFFFFF) {
                                celsius = 0xFF8C9299
                                fahrenheit = 0xFFFFFFFF
                            } else {
                                celsius = 0xFFFFFFFF
                                fahrenheit = 0xFF8C9299
                            }
                            vm.valgtTemperatur = if (vm.valgtTemperatur == "Celsius") {
                                "Fahrenheit" //add this to viewmodel so we can process this in repo?
                            } else {
                                "Celsius"
                            } },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color(0xFF38424D),
                            checkedTrackColor = Color(0xFFFFFFFF),
                            uncheckedThumbColor = Color(0xFFFFFFFF),
                            uncheckedTrackColor = Color(0xFF38424D),
                            )
                    )
                    }
                    IconButton(
                        onClick = { vm.showSettings = false }, // Lukker boksen når klikket
                        modifier = Modifier
                            .size(50.dp),
                        colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close settings")
                    }
                }
            }
        }

        AnimatedVisibility(visible = vm.noResultsToast) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(
                        animationSpec = tween(
                            durationMillis = 300,
                            easing = LinearOutSlowInEasing
                        )
                    ),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF38424D))
            ) {

                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Text(text = "Søket ditt gav ingen treff", color = Color.White)
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SearchBar(vm)

            // Innstillingsikonet, kun synlig når boksen ikke er åpen
                IconButton(
                    onClick = { vm.showSettings = !vm.showSettings },
                    modifier = Modifier.size(60.dp),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
        }

        AnimatedVisibility(visible = vm.loadingScreen) {
            Column (modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier
                    .height(250.dp)
                    .fillMaxWidth())
                CircularProgressIndicator(
                    modifier = Modifier.width(90.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    )
                Spacer(modifier = Modifier
                    .height(800.dp)
                    .fillMaxWidth())
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
                    Text(text = "", fontSize = 0.sp, fontWeight = Bold)
                }

                    HeaderComponent(vm)

                        Row(
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .padding(start = 20.dp, end = 20.dp, bottom = 50.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            val iconid = LocalContext.current.resources.getIdentifier(vm.locationForecastIcons[0], "drawable", LocalContext.current.packageName) //ignore warning. It makes R.drawable dynamic instead of static, allowing us to apply variable names (since weather icons change a lot)
                            Image(
                                modifier = Modifier
                                    .size(110.dp)
                                    .weight(1f),
                                painter = painterResource(id = iconid),
                                contentDescription = "Weather icon"
                            )

                            VerticalLine()

                            Column(
                                modifier = Modifier
                                    .weight(1f)
                            ) {
                                val temperatureText = if (vm.valgtTemperatur == "Celsius") {
                                    "${vm.weatherData!!.properties.timeseries[0].data.instant.details.air_temperature.roundToInt()}°C"
                                } else {
                                    "${(vm.weatherData!!.properties.timeseries[0].data.instant.details.air_temperature * 1.8 + 32).roundToInt()}°F"
                                }

                                val currentWeatherDescription =
                                    when (vm.weatherData!!.properties.timeseries[0].data.next_1_hours.summary.symbol_code) {
                                        "clearsky_day", "clearsky_night", "clearsky_polartwilight" -> "er klar himmel"
                                        "fair_day", "fair_night", "fair_polartwilight" -> "er lettskyet"
                                        "partlycloudy_day", "partlycloudy_night", "partlycloudy_polartwilight" -> "er delvis skyet"
                                        "cloudy" -> "er overskyet"
                                        "rainshowers_day", "rainshowers_night", "rainshowers_polartwilight" -> "er regnbyger"
                                        "rainshowersandthunder_day", "rainshowersandthunder_night", "rainshowersandthunder_polartwilight" -> "er regnbyger og torden"
                                        "sleetshowers_day", "sleetshowers_night", "sleetshowers_polartwilight" -> "er sluddbyger"
                                        "snowshowers_day", "snowshowers_night", "snowshowers_polartwilight" -> "er snøbyger"
                                        "rain" -> "regner"
                                        "heavyrain" -> "er kraftig regn"
                                        "heavyrainandthunder" -> "er kraftig regn og torden"
                                        "sleet" -> "er sludd"
                                        "snow" -> "snør"
                                        "snowandthunder" -> "er snø og torden"
                                        "fog" -> "er tåkete"
                                        "sleetshowersandthunder_day", "sleetshowersandthunder_night", "sleetshowersandthunder_polartwilight" -> "er sluddbyger og torden"
                                        "snowshowersandthunder_day", "snowshowersandthunder_night", "snowshowersandthunder_polartwilight" -> "er snøbyger og torden"
                                        "rainandthunder" -> "er regn og torden"
                                        "sleetandthunder" -> "er sludd og torden"
                                        "lightrainshowersandthunder_day", "lightrainshowersandthunder_night", "lightrainshowersandthunder_polartwilight" -> "er lette regnbyger og torden"
                                        "heavyrainshowersandthunder_day", "heavyrainshowersandthunder_night", "heavyrainshowersandthunder_polartwilight" -> "er kraftige regnbyger og torden"
                                        "lightsleetshowersandthunder_day", "lightsleetshowersandthunder_night", "lightsleetshowersandthunder_polartwilight" -> "er lette sluddbyger og torden"
                                        "heavysleetshowersandthunder_day", "heavysleetshowersandthunder_night", "heavysleetshowersandthunder_polartwilight" -> "er kraftige sluddbyger og torden"
                                        "lightsnowshowersandthunder_day", "lightsnowshowersandthunder_night", "lightsnowshowersandthunder_polartwilight" -> "er lette snøbyger og torden"
                                        "heavysnowshowersandthunder_day", "heavysnowshowersandthunder_night", "heavysnowshowersandthunder_polartwilight" -> "er kraftige snøbyger og torden"
                                        "lightrainandthunder" -> "er lett regn og torden"
                                        "lightsleetandthunder" -> "er lett sludd og torden"
                                        "heavysleetandthunder" -> "er kraftig sludd og torden"
                                        "lightsnowandthunder" -> "er lett snø og torden"
                                        "heavysnowandthunder" -> "er kraftig snø og torden"
                                        "lightrainshowers_day", "lightrainshowers_night", "lightrainshowers_polartwilight" -> "er lette regnbyger"
                                        "heavyrainshowers_day", "heavyrainshowers_night", "heavyrainshowers_polartwilight" -> "er kraftige regnbyger"
                                        "lightsleetshowers_day", "lightsleetshowers_night", "lightsleetshowers_polartwilight" -> "er lette sluddbyger"
                                        "heavysleetshowers_day", "heavysleetshowers_night", "heavysleetshowers_polartwilight" -> "er kraftige sluddbyger"
                                        "lightsnowshowers_day", "lightsnowshowers_night", "lightsnowshowers_polartwilight" -> "er lette snøbyger"
                                        "heavysnowshowers_day", "heavysnowshowers_night", "heavysnowshowers_polartwilight" -> "er kraftige snøbyger"
                                        "lightrain" -> "er lett regn"
                                        "lightsleet" -> "er lett sludd"
                                        "heavysleet" -> "er kraftig sludd"
                                        "lightsnow" -> "er lett snø"
                                        "heavysnow" -> "snør kraftig"
                                        else -> null
                                    }

                                Text(
                                    modifier = Modifier.padding(vertical = 4.dp),
                                    text = temperatureText,
                                    fontSize = 50.sp,
                                    style = TextStyle(
                                        color = Color.White
                                    )
                                )

                                if (currentWeatherDescription != null) {
                                    val weatherSentence = "Det $currentWeatherDescription"
                                    Text(
                                        text = weatherSentence,
                                        fontSize = 18.sp,
                                        style = TextStyle(
                                            color = Color.White
                                        )
                                    )
                                }
                            }
                         }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 30.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Vind",
                                    fontSize = 18.sp,
                                    style = TextStyle(
                                        color = Color.White
                                    )
                                )
                                Image(
                                    painter = painterResource(id = R.drawable.windicon),
                                    contentDescription = "Wind Icon",
                                    modifier = Modifier
                                        .padding(start = 5.dp)
                                        .size(40.dp)
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Image(
                                    painter = painterResource(id = R.drawable.rainicon),
                                    contentDescription = "Rain Icon",
                                    modifier = Modifier
                                        .padding(end = 5.dp)
                                        .size(35.dp)
                                )
                                Text(
                                    text = "Nedbør",
                                    fontSize = 18.sp,
                                    style = TextStyle(
                                        color = Color.White
                                    )
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            val vind =
                                vm.weatherData!!.properties.timeseries[0].data.instant.details.wind_speed.roundToInt()
                            Text(
                                text = "$vind m/s",
                                fontSize = 18.sp,
                                style = TextStyle(
                                    color = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            val regn =
                                vm.weatherData!!.properties.timeseries[0].data.next_1_hours.details.precipitation_amount
                            Text(
                                text = "$regn mm",
                                fontSize = 18.sp,
                                style = TextStyle(
                                    color = Color.White
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(50.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.sunriseicon),
                                    contentDescription = "Sunrise Icon",
                                    modifier = Modifier
                                        .padding(end = 10.dp)
                                        .size(40.dp)
                                )
                                Text(
                                    text = "Soloppgang\n${vm.sunriseTime}",
                                    fontSize = 18.sp,
                                    style = TextStyle(
                                        color = Color.White
                                    ),
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "Solnedgang\n${vm.sunsetTime}",
                                    fontSize = 18.sp,
                                    style = TextStyle(
                                        color = Color.White,
                                        textAlign = TextAlign.End
                                    )
                                )
                                Image(
                                    painter = painterResource(id = R.drawable.sunseticon),
                                    contentDescription = "Sunset Icon",
                                    modifier = Modifier
                                        .padding(start = 10.dp)
                                        .size(40.dp)
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 70.dp, bottom = 20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.uvicon),
                            contentDescription = "UV Icon",
                            modifier = Modifier.size(27.dp)
                        )
                        Text(
                            text = "UV-indeks",
                            fontSize = 20.sp,
                            style = TextStyle(
                                color = Color.White
                            )
                        )
                    }

                    val uvNow =
                        vm.weatherData!!.properties.timeseries[0].data.instant.details.ultraviolet_index_clear_sky

                    val uvText = when {
                        vm.weatherData != null && uvNow < 3.0 -> "Lavt"
                        vm.weatherData != null && uvNow >= 3.0 && uvNow < 6.0 -> "Medium"
                        vm.weatherData != null && uvNow >= 6.0 && uvNow < 8.0 -> "Høyt"
                        else -> "Veldig høyt"
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        UVScale(uvIndex = uvNow)
                        Text(
                            modifier = Modifier
                                .padding(top = 10.dp),
                            text = "$uvNow - $uvText",
                            fontSize = 20.sp,
                            style = TextStyle(
                                color = Color.White
                            ),
                            textAlign = TextAlign.Center
                        )
                    }

                val alertIcons = vm.getAlertIcons()

                if (vm.alertsData!!.features.isNotEmpty()) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(start = 30.dp, end = 30.dp, top = 30.dp)
                    ) {
                        var i = 0

                        vm.alertsData!!.features.forEach {
                            val event = it.properties.event
                            val riskMatrixColor = it.properties.riskMatrixColor.lowercase()
                            val iconResourceName = "${alertIcons[event]}_$riskMatrixColor"

                            WarningBox(
                                headline = it.properties.eventAwarenessName,
                                subtitle = "",
                                info = it.properties.instruction,
                                iconResourceId = LocalContext.current.resources.getIdentifier(iconResourceName, "drawable", LocalContext.current.packageName)
                            )
                            i++
                        }
                    }
                }

                    Column(
                        modifier = Modifier
                            .padding(start = 30.dp, end = 30.dp, top = 30.dp, bottom = 30.dp)
                    ) {
                        if (uvNow >= 8.0) {
                            WarningBox(
                                headline = "Veldig høy\nUV-indeks!",
                                subtitle = "",
                                info = "Bruk solkrem med høy faktor flere ganger gjennom dagen. Søk etter skygge! Bruk klær, hodeplagg og solbriller. Husk å ta pauser fra sola ofte, spesielt under kl. 12-15.",
                                iconResourceId = R.drawable.icon_warning_generic_red
                            )
                        } else if (uvNow >= 6.0 && uvNow < 8) {
                            WarningBox(
                                headline = "Høy UV-indeks!",
                                subtitle = "",
                                info = "Husk å ta på solkrem med høy faktor! Bruk klær, hodeplagg og solbriller. Husk å ta pauser fra sola.",
                                iconResourceId = R.drawable.icon_warning_generic_orange
                            )
                        } else if (uvNow >= 3 && uvNow < 6) {
                            WarningBox(
                                headline = "Middels UV-indeks",
                                subtitle = "",
                                info = "Husk å ta på solkrem hvis du skal være ute lenge!",
                                iconResourceId = R.drawable.icon_warning_generic_yellow
                            )
                        }
                    }

                    Text(
                        text = "  I dag",
                        fontSize = 30.sp,
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .align(Alignment.Start),
                        style = TextStyle(
                            color = Color.White
                        )
                    )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                    ) {
                        Row(modifier = Modifier.horizontalScroll(scrollState)) {
                            for (i in 1..35) {
                                var time: Int =
                                    vm.weatherData!!.properties.timeseries[i].time.removeRange(
                                        0,
                                        11
                                    )
                                        .removeRange(2, 9)
                                        .toInt() + vm.offset // Lokal tid siden locationForecast er i UTC/STD.

                                if (time >= 24) { // Gjør time til lokal tid
                                    time -= 24
                                } else if (time < 0){ // For en eller annen grunn kan det bli negativt. Dette sørger for at det ikke skjer.
                                    time += 24
                                }

                                val formattedTime = String.format(
                                    "%02d:00",
                                    time
                                )// Formatere tiden til alltid å ha to sifre

                                BoxComponent(
                                    time = formattedTime, // Legger til tid som en parameter i BoxComponent
                                    temperature = if (vm.valgtTemperatur == "Celsius") {
                                        "${vm.weatherData!!.properties.timeseries[i].data.instant.details.air_temperature.roundToInt()}°C"
                                    } else {
                                        "${(vm.weatherData!!.properties.timeseries[i].data.instant.details.air_temperature * 1.8 + 32).roundToInt()}°F"
                                    },
                                    windSpeed = "${vm.weatherData!!.properties.timeseries[i].data.instant.details.wind_speed.roundToInt()} m/s",
                                    precipitation = "${vm.weatherData!!.properties.timeseries[i].data.next_1_hours.details.precipitation_amount} mm",
                                    uvIndex = vm.weatherData!!.properties.timeseries[i].data.instant.details.ultraviolet_index_clear_sky,
                                    width = 140,
                                    height = 278,
                                    expanded = allBoxesExpanded,
                                    weatherIcon = {
                                        val iconids = LocalContext.current.resources.getIdentifier(vm.locationForecastIcons[i], "drawable", LocalContext.current.packageName) //ignore warning. It makes R.drawable dynamic instead of static, allowing us to apply variable names (since weather icons change a lot)
                                        Image(
                                            modifier = Modifier
                                                .size(110.dp)
                                                .weight(1f),
                                            painter = painterResource(id = iconids),
                                            contentDescription = "Weather icon"
                                        )
                                    }
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                            }
                        }
                    }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            allBoxesExpanded.value = !allBoxesExpanded.value
                        },
                        modifier = Modifier.rotate(rotationState.value)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Drop-Down Arrow",
                            tint = Color.White,
                            modifier = Modifier.size(300.dp)
                        )
                    }
                    // Tekst som endres basert på om pilen er trykket på eller ikke
                    Text(
                        text = if (allBoxesExpanded.value) "Vis mindre" else "Vis mer",
                        color = Color.White,
                        modifier = Modifier
                            .clickable {
                                allBoxesExpanded.value = !allBoxesExpanded.value
                            }
                            .padding(start = 8.dp)
                    )
                }

                    val monthMap = mapOf(
                        1 to "januar",
                        2 to "februar",
                        3 to "mars",
                        4 to "april",
                        5 to "mai",
                        6 to "juni",
                        7 to "juli",
                        8 to "august",
                        9 to "september",
                        10 to "oktober",
                        11 to "november",
                        12 to "desember"
                    )

                    val dayOfWeekMap = mapOf(
                        Calendar.MONDAY to "Man",
                        Calendar.TUESDAY to "Tir",
                        Calendar.WEDNESDAY to "Ons",
                        Calendar.THURSDAY to "Tor",
                        Calendar.FRIDAY to "Fre",
                        Calendar.SATURDAY to "Lør",
                        Calendar.SUNDAY to "Søn"
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Kommende dager:",
                            fontSize = 30.sp,
                            modifier = Modifier
                                .padding(top = 10.dp, bottom = 25.dp),
                            style = TextStyle(
                                color = Color.White
                            )
                        )

                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .padding(start = 47.dp, end = 23.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Dato",
                                modifier = Modifier
                                    .weight(1f),
                                textAlign = TextAlign.Start,
                                fontWeight = Bold,
                                style = TextStyle(
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Høyest / Lavest",
                                modifier = Modifier
                                    .weight(1f),
                                textAlign = TextAlign.Center,
                                fontWeight = Bold,
                                style = TextStyle(
                                    color = Color.White
                                )
                            )
                            Text(
                                text = "Vær",
                                modifier = Modifier
                                    .padding(end = 18.dp)
                                    .weight(1f),
                                textAlign = TextAlign.End,
                                fontWeight = Bold,
                                style = TextStyle(
                                    color = Color.White
                                )
                            )
                        }

                        // Opprett en Calendar-instans
                        val calendar = Calendar.getInstance()

                        repeat(7) { index ->
                            // Beregn datoen for dagen
                            calendar.timeInMillis = System.currentTimeMillis()
                            calendar.add(Calendar.DATE, index + 1) // Legg til 1 dag til index

                            // Hent ukedagen
                            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
                            val dayOfWeekText = dayOfWeekMap[dayOfWeek]

                            // Hent måneden
                            val month = calendar.get(Calendar.MONTH) + 1
                            val monthText = monthMap[month]
                            val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

                            val date = SimpleDateFormat(
                                "yyyy-MM-dd",
                                Locale.getDefault()
                            ).format(calendar.time)

                            // Hent data for den aktuelle dagen fra API-et
                            val dayData = vm.getTemperatureForDay(vm.weatherData!!, date)
                            val dayDataDetails = vm.getDayDataDetails(date)

                            val maxTemp = dayData?.first?.roundToInt()
                            val minTemp = dayData?.second?.roundToInt()

                            // Formattert dato (ukedag, dato, måned)
                            val formattedDate = "$dayOfWeekText, $dayOfMonth. $monthText"

                            val symbolCode = vm.weatherData!!.properties.timeseries.find {
                                val time = it.time
                                time == "${date}T06:00:00Z"
                            }!!.data.next_12_hours.summary.symbol_code

                            Line()

                            // Vis maks- og minimumstemperaturene for dagen
                            DayTemperatureItem(
                                vm = vm,
                                id = index,
                                date = formattedDate,
                                maxTemperature = maxTemp!!,
                                minTemperature = minTemp!!,
                                valgtTemperatur = vm.valgtTemperatur,
                                weatherIcon = symbolCode
                            )
                            AnimatedVisibility(visible = vm.expandTable[index]) {
                                ExtendedTableItem(dayDataDetails)
                            }

                        }
                        Line()
                    }
                }
            }
    }
}

@Composable
fun ExtendedTableItem(data: List<List<Any>>) {
    Row (
        modifier = Modifier
            .fillMaxWidth()

    ) {

        LazyColumn(
            userScrollEnabled = false,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF38424D))
                .height(209.dp)) {
            item {

                Line()
                Row (modifier = Modifier
                    .fillMaxWidth()
                    .height(37.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                    ) {
                    Spacer(modifier = Modifier.width(50.dp))
                    Image(
                        painter = painterResource(id = R.drawable.temperature),
                        contentDescription = "Temperature Icon",
                        modifier = Modifier.size(23.dp)
                    )

                    Image(
                        painter = painterResource(id = R.drawable.windicon),
                        contentDescription = "Wind Icon",
                        modifier = Modifier.size(23.dp)
                    )
                    Image(
                        painter = painterResource(id = R.drawable.rainicon),
                        contentDescription = "Rain Icon",
                        modifier = Modifier.size(23.dp)
                    )
                    Spacer(modifier = Modifier.width(50.dp))
                }
                Spacer(modifier = Modifier
                    .padding(5.dp, 5.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFF999999))
                )
            }
            items(data) {
                val lists = it
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(5.dp, 8.dp),
                    horizontalArrangement = Arrangement.SpaceAround

                ) {
                    Text(text = lists[0].toString(), color = Color.White)
                    Text(text = lists[1].toString(), color = Color.White)
                    Text(text = "${lists[2]} m/s", color = Color.White)
                    Text(text = "${lists[3]} mm", color = Color.White)
                    Image(
                        painter = painterResource(id = LocalContext.current.resources.getIdentifier(lists[4].toString(), "drawable", LocalContext.current.packageName)),
                        contentDescription = "Weather icon",
                        modifier = Modifier
                            .size(23.dp)
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(vm: HomeViewModel) {

    val items = vm.suggestions
    val sug = mutableListOf<String>()
    items.forEach { item ->
        sug.add(item.formatted)
    }

    val heightTextFields by remember {
        mutableStateOf(55.dp)
    }

    var textFieldSize by remember {
        mutableStateOf(Size.Zero)
    }


    val interactionSource = remember {
        MutableInteractionSource()
    }

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val configuration = LocalConfiguration.current

    // Category Field
    Column(
        modifier = Modifier
            .padding(top = 5.dp)
            .padding(horizontal = 10.dp)
            .width((configuration.screenWidthDp - 75).dp)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = {
                    vm.expanded = false
                }
            )
    ) {


        Column(modifier = Modifier.fillMaxWidth()) {

            Row(modifier = Modifier.fillMaxWidth()) {
                val containerColor = Color(0xFF272D34)
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(heightTextFields)
                        .clip(RoundedCornerShape(30.dp))
                        .border(
                            width = 2.dp,
                            color = Color.White,
                            shape = RoundedCornerShape(30.dp)
                        )
                        .focusRequester(vm.focusRequester)
                        .onKeyEvent { event ->
                            if (event.type == KeyEventType.KeyUp && event.key == Key.Enter) {
                                //vm.expanded = false
                                keyboardController?.hide()
                                true
                            } else {
                                false
                            }
                        }
                        .onGloballyPositioned { coordinates ->
                            textFieldSize = coordinates.size.toSize()
                        },
                    value = vm.searchField,
                    onValueChange = {
                        vm.searchField = it
                        vm.expanded = true
                        vm.loadSuggestions(it)
                    },
                    placeholder = { Text("Søk på sted", color = Color(0xFF999999)) },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = containerColor,
                        unfocusedContainerColor = containerColor,
                        disabledContainerColor = containerColor,
                        cursorColor = Color.White,
                        focusedIndicatorColor = Color(0xFF272D34),
                        unfocusedIndicatorColor = Color(0xFF272D34),
                    ),
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = 18.sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    trailingIcon = {
                        Row (verticalAlignment = Alignment.CenterVertically) {
                            if (vm.loadingSearch) {
                                CircularProgressIndicator(
                                    modifier = Modifier
                                        .width(20.dp)
                                        .padding(0.dp, 16.dp, 0.dp, 0.dp),
                                    color = MaterialTheme.colorScheme.secondary,
                                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            }
                            if (vm.searchField.isNotEmpty()) {
                                IconButton(onClick = { vm.searchField = "" }) {
                                    Icon(
                                        imageVector = Icons.Rounded.Clear,
                                        contentDescription = "Clear",
                                        tint = Color.White
                                    )
                                }
                            }
                        }
                    }
                )
            }

            AnimatedVisibility(visible = vm.expanded) {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 5.dp)
                        .width(textFieldSize.width.dp),
                    shape = RoundedCornerShape(10.dp)
                ) {

                    LazyColumn(
                        modifier = Modifier.heightIn(max = 850.dp),
                    ) {

                        if (vm.searchField.isNotEmpty()) {
                            items(
                                sug.filter {
                                    it.lowercase()
                                        .contains(vm.searchField.lowercase()) || it.lowercase()
                                        .contains("others")
                                }
                                    .sorted()
                            ) {
                                DropdownRow(vm, focusManager, keyboardController, title = it) { title ->
                                    vm.searchField = title
                                    vm.expanded = true
                                }
                            }
                        } else {
                            items(
                                sug.sorted()
                            ) {
                                DropdownRow(vm, focusManager, keyboardController, title = it) { title ->
                                    vm.searchField = title
                                    vm.expanded = true
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HeaderComponent(vm: HomeViewModel) {

    val streak = vm.streak
    val streakText = if (streak == 1) {
        "$streak dag streak"
    } else {
        "$streak dager streak"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 50.dp, top = 15.dp, end = 20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row (
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "I dag",
                fontSize = 20.sp,
                modifier = Modifier.padding(15.dp),
                style = TextStyle(
                    color = Color.White
                )
            )

            Row (
                horizontalArrangement = Arrangement.spacedBy((-23).dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.streakfire),
                    contentDescription = "Warning icon",
                    modifier = Modifier
                        .size(37.dp)
                        .zIndex(1f)
                        .clickable { vm.showStreak = !vm.showStreak }

                )
                AnimatedVisibility(visible = vm.showStreak) {
                    Box (
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFFF6D00))
                    ) {
                        Text(
                            text = streakText,
                            fontSize = 20.sp,
                            modifier = Modifier
                                .padding(start = 30.dp, top = 4.dp, end = 10.dp, bottom = 4.dp),
                            style = TextStyle(color = Color.White)
                        )
                    }
                }
            }
        }

        Text(
            text = vm.currentFormatted,
            fontSize = 30.sp,
            style = TextStyle(
                color = Color.White
            ),
            modifier = Modifier.padding(start = 15.dp, bottom = 20.dp)
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun DropdownRow(
    vm: HomeViewModel,
    fm: FocusManager,
    kb: SoftwareKeyboardController?,
    title: String,
    onSelect: (String) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onSelect(title)
                vm.loadCurrent(title)
                vm.expanded = false
                kb?.hide()
                fm.clearFocus()
                vm.searchField = ""
            }
            .padding(10.dp)
    ) {

        Text(text = title, fontSize = 16.sp)
    }
}

//Composable to make the warning box
@Composable
fun WarningBox(headline: String, subtitle: String, info: String,  iconResourceId: Int) {
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f, label = ""
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFCF72),
        ),
        modifier = Modifier
            .padding(top = 10.dp)
            .clickable { expandedState = !expandedState }
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 300,
                    easing = LinearOutSlowInEasing
                )
            ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = iconResourceId),
                    contentDescription = "Warning icon",
                    modifier = Modifier
                        .size(120.dp)
                        .padding(start = 20.dp, top = 20.dp, end = 10.dp)
                )

                Column {
                    Text(
                        text = headline,
                        modifier = Modifier
                            .padding(top = 10.dp),
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = Bold,
                            color = Color(0xFF000000),
                        )
                    )
                    Text(
                        text = subtitle,
                        style = TextStyle(
                            fontSize = 13.sp,
                            color = Color(0xFF000000),
                        ),
                        modifier = Modifier
                            .padding(bottom = 15.dp)
                    )
                }

                IconButton(
                    modifier = Modifier
                        .weight(1f)
                        .rotate(rotationState),
                    onClick = {
                        expandedState = !expandedState
                    },
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = Color(0xFF000000)
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Drop-Down Arrow"
                    )
                }
            }
            if (expandedState) {
                Text(
                    text = info,
                    style = TextStyle(
                        fontSize = 15.sp,
                        color = Color(0xFF000000),
                    ),
                    modifier = Modifier
                        .padding(start = 20.dp, end = 20.dp, bottom = 20.dp)
                )
            }
        }
    }
}

//Makes text with the same type of font
@Composable
fun SettingsText(fontSize: Int, color: Long, content: String, start: Int, top: Int, end: Int, bottom: Int ){
    Text(
        text = content ,
        style = TextStyle(
            fontSize = fontSize.sp,
            fontWeight = FontWeight(300),
            color = Color(color),
        ),
        modifier = Modifier
            .padding(start.dp, top.dp, end.dp, bottom.dp),
    )
}

//Makes a line (used in the settings card and long term weather forecast
@Composable
fun Line(){
    Spacer(modifier = Modifier
        .fillMaxWidth()
        .height(1.dp)
        .background(Color(0xFFFFFFFF))
    )
}

//Makes a vertical line (used between weather icon and degrees
@Composable
fun VerticalLine(){
    Spacer(
        Modifier
            .padding(start = 30.dp, end = 30.dp)
            .width(1.dp)
            .height(100.dp)
            .background(color = Color(0xFFFFFFFF))
    )
}

//This box component is for the hour by hour weather forecast - if you want to use it!
@Composable
fun BoxComponent(
    time: String,
    temperature: String,
    windSpeed: String,
    precipitation: String,
    uvIndex: Float,
    width: Int,
    height: Int,
    weatherIcon: @Composable () -> Unit,
    expanded: MutableState<Boolean>
) {
    val uvColor = when {
        uvIndex <= 2 -> Color(0xFF14FC00)
        uvIndex <= 4 -> Color(0xFFDEEF17)
        uvIndex <= 6 -> Color(0xFFFFAA06)
        uvIndex <= 8 -> Color(0xFFFD6C06)
        uvIndex <= 10 -> Color(0xFFFB0606)
        else -> Color(0xFF9E06FB)
    }

    Card(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Color(0xFFFFFFFF),
                shape = RoundedCornerShape(size = 20.dp)
            )
            .width(width.dp)
            .height(if (expanded.value) height.dp else 160.dp)
            .clickable { expanded.value = !expanded.value }, // Oppdaterer den felles expanded-state'en
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4A535D)
        ),
        shape = RoundedCornerShape(size = 20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = time,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = Bold,
                    color = Color.White,
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
            weatherIcon()
            Text(
                text = temperature,
                style = TextStyle(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Normal,
                    color = Color.White,
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(8.dp)
            )
            if (expanded.value) {
                Row(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.windicon),
                            contentDescription = "Wind Icon",
                            modifier = Modifier.size(23.dp)
                        )
                        Text(
                            text = windSpeed,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White,
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.rainicon),
                            contentDescription = "Rain Icon",
                            modifier = Modifier.size(23.dp)
                        )
                        Text(
                            text = precipitation,
                            style = TextStyle(
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White,
                            ),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.uvicon),
                        contentDescription = "UV Icon",
                        modifier = Modifier.size(23.dp)
                    )
                    Text(
                        text = "$uvIndex",
                        style = TextStyle(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White,
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(8.dp)
                    )
                    Box(
                        modifier = Modifier
                            .size(15.dp)
                            .background(
                                color = uvColor,
                                shape = CircleShape
                            )
                            .border(
                                width = 1.dp,
                                color = Color.White,
                                shape = CircleShape
                            )
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DayTemperatureItem(vm: HomeViewModel, id: Int, date: String, maxTemperature: Int, minTemperature: Int, valgtTemperatur: String, weatherIcon: String) {

    Row(
        modifier = Modifier
            .clickable(onClick = {
                for (i in 0..6) {
                    if (i == id) {
                        vm.expandTable[id] = !vm.expandTable[id]
                    } else {
                        vm.expandTable[i] = false
                    }
                }
            })
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 3.dp)
            .padding(start = 25.dp, end = 25.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = date,
            modifier = Modifier
                .weight(1f),
            style = TextStyle(
                color = Color.White
            )
        )
        val formattedMaxTemp = if (valgtTemperatur == "Celsius") {
            "$maxTemperature°C"
        } else {
            val fahrenheitMaxTemp = ((maxTemperature * 9 / 5) + 32)
            "$fahrenheitMaxTemp°F"
        }
        val formattedMinTemp = if (valgtTemperatur == "Celsius") {
            "$minTemperature°C"
        } else {
            val fahrenheitMinTemp = ((minTemperature * 9 / 5) + 32)
            "$fahrenheitMinTemp°F"
        }
        Text(
            text = "$formattedMaxTemp / $formattedMinTemp",
            modifier = Modifier.weight(1f),
            style = TextStyle(
                color = Color.White
            )
        )
        Image(
            painter = painterResource(id = LocalContext.current.resources.getIdentifier(weatherIcon, "drawable", LocalContext.current.packageName)),
            contentDescription = "Weather icon",
            modifier = Modifier
                .size(50.dp)
        )
    }
}

@Composable
fun UVScale(uvIndex: Float, modifier: Modifier = Modifier) {
    val gradientColors = listOf(
        Color(0xFF14FC00),
        Color(0xFFDEEF17),
        Color(0xFFFFAA06),
        Color(0xFFFD6C06),
        Color(0xFFFB0606),
        Color(0xFF9E06FB)
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 30.dp, end = 30.dp)
            .height(10.dp)
            .background(
                brush = Brush.horizontalGradient(
                    colors = gradientColors,
                ),
                shape = RoundedCornerShape(5.dp)
            )
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val uvIndexPosition = if (uvIndex <= 11f) {
                (uvIndex / 11f) * size.width
            } else {
                size.width // Hvis UV-indeksen er over 11, plasser prikken i enden av skalaen
            }
            drawOval(
                color = Color.White,
                topLeft = Offset(uvIndexPosition - (10.dp.toPx() / 2), (size.height / 2) - (16.dp.toPx() / 2)), // Top-left hjørne av ovalen
                size = Size(10.dp.toPx(), 16.dp.toPx()) // Størrelsen til ovalen
            )
        }
    }
}