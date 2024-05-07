package no.uio.ifi.in2000.project.ui.home

import android.util.Log
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.lifecycle.viewmodel.compose.viewModel
import no.uio.ifi.in2000.project.R
import no.uio.ifi.in2000.project.model.search.ApiProperties
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreen(vm: HomeViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    val scrollStateVertical = rememberScrollState()

    var expandedSpråk by remember {
        mutableStateOf(false)
    }

    var switchChecked by remember {
        mutableStateOf(false)
    }

    var valgtTemperatur by remember {
        mutableStateOf("Celsius") //Default value will be Celsius. Can choose Fahrenheit.
    }

    var expandedStateSettings by remember { mutableStateOf(false) }
    val rotationStateSettings by animateFloatAsState(
        targetValue = if (expandedStateSettings) 180f else 0f, label = ""
    )

    //duplicate of same thing in SearchBar()
    val keyboardController = LocalSoftwareKeyboardController.current

    val språk = LinkedHashMap<String, String>()
    språk["no"] = "Norsk"
    språk["en"] = "English"

    var valgtSpråk by remember {
        mutableStateOf("Norsk") //Default value will be "no", norsk.
    }
    var showSettings by remember {mutableStateOf(false)}
    var showSearchbar by remember{ mutableStateOf(false)}

    Column(
        modifier = Modifier
            .background(Color(0xFF272D34))
            .fillMaxSize()
            .verticalScroll(scrollStateVertical)
            .pointerInput(Unit) {
                detectTapGestures(onTap = { //hides keyboard when clicking out
                    keyboardController?.hide()
                    //vm.expanded = false //hides suggestions when clicking out.
                })
            },
        //verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Søkeikonet, alltid synlig
            IconButton(
                onClick = { showSearchbar = !showSearchbar },
                modifier = Modifier.size(50.dp),
                colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
            ) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }

            // Innstillingsikonet, kun synlig når boksen ikke er åpen
            AnimatedVisibility(visible = !showSettings) {
                IconButton(
                    onClick = { showSettings = true },
                    modifier = Modifier.size(50.dp),
                    colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                ) {
                    Icon(Icons.Default.Settings, contentDescription = "Settings")
                }
            }

            // Innstillingsboksen, åpnes på høyre hjørne
            AnimatedVisibility(visible = showSettings) {
                Card(
                    modifier = Modifier
                        .width(242.dp)
                        .animateContentSize(
                            animationSpec = tween(
                                durationMillis = 300,
                                easing = LinearOutSlowInEasing
                            )
                        ),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF38424D))
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Innstillingsikonet inne i boksen
                        IconButton(
                            onClick = { showSettings = false }, // Lukker boksen når klikket
                            modifier = Modifier
                                .size(50.dp),
                            colors = IconButtonDefaults.iconButtonColors(contentColor = Color.White)
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = "Close settings")
                        }

                        Row {
                            SettingsText(
                                14,
                                color = 0xFFFFFFFF,
                                content = "Posisjonsbasert værvarsel",
                                10,
                                5,
                                12,
                                5
                            )
                            SwitchButton()
                        }
                        Line()

                        Row {
                            var celsius by remember { mutableStateOf(0xFFFFFFFF) }
                            var fahrenheit by remember { mutableStateOf(0xFF8C9299) }
                            var checked by remember { mutableStateOf(false) }

                            SettingsText(14, color = celsius, content = "Celsius", 10, 5, 0, 5)
                            SettingsText(14, color = 0xFFFFFFFF, content = " / ", 0, 5, 0, 5)
                            SettingsText(14, color = fahrenheit, content = "Fahrenheit", 0, 5, 50, 5)

                            Switch(
                                modifier = Modifier
                                    .size(2.dp)
                                    .padding(25.dp, 15.dp, 0.dp, 0.dp),
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
                                    valgtTemperatur = if (valgtTemperatur == "Celsius") {
                                        "Fahrenheit" //add this to viewmodel so we can process this in repo?
                                    } else {
                                        "Celsius"
                                    }
                                    Log.i("TEMPERATUR", valgtTemperatur)

                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF38424D),
                                    checkedTrackColor = Color(0xFFFFFFFF),
                                    uncheckedThumbColor = Color(0xFFFFFFFF),
                                    uncheckedTrackColor = Color(0xFF38424D),
                                )
                            )
                        }
                        Line()

                        Row {
                            SettingsText(
                                14,
                                color = 0xFFFFFFFF,
                                content = "Varslinger",
                                10,
                                5,
                                110,
                                5
                            )
                            SwitchButton()
                        }

                    }
                    Line()

                    Row {


                        SettingsText(14, color = 0xFFFFFFFF, content = "Språk", 10, 5, 15, 5)
                        SettingsText(14, color = 0xFF8C9299, content = valgtSpråk, 10, 5, 20, 5)

                        IconButton(
                            modifier = Modifier
                                .padding(40.dp, 0.dp, 10.dp, 0.dp)
                                .rotate(rotationStateSettings),
                            onClick = {
                                expandedStateSettings = !expandedStateSettings

                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = Color(0xFFFFFFFF)
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = "Drop-Down Arrow"
                            )
                        }

                    }
                    if (expandedStateSettings) {
                        val notClicked = 0xFF272D34
                        val clicked = 0xFF586471

                        /* var nynorsk by remember{mutableStateOf(
                         if (chosenLanguage == "Norsk (nynorsk)") clicked
                         else notClicked
                     ) }*/
                        var bokmaal by remember {
                            mutableStateOf(
                                if (valgtSpråk == "Norsk") clicked
                                else notClicked
                            )
                        }
                        var engelsk by remember {
                            mutableStateOf(
                                if (valgtSpråk == "Engelsk") clicked
                                else notClicked
                            )
                        }

                        //In case we want to implement nynorsk:
                        /*
                                Box (modifier = Modifier
                                    .clickable(onClick = {
                                        nynorsk = clicked
                                        bokmaal = notClicked
                                        engelsk = notClicked

                                        chosenLanguage = "Norsk (nynorsk)"

                                    })
                                    .padding(5.dp, 3.dp)
                                    .fillMaxWidth()
                                    .background(color = Color(nynorsk))

                                ) {
                                    SettingsText(fontSize = 14, color = 0xFFFFFFFF, content = "Norsk (Nynorsk)", start = 10, top = 5, end = 5, bottom = 5)
                                }*/

                        Box(
                            modifier = Modifier
                                .clickable(onClick = {
                                    //nynorsk = notClicked
                                    bokmaal = clicked
                                    engelsk = notClicked

                                    valgtSpråk = "Norsk (bokmål)"
                                })
                                .padding(5.dp, 3.dp)
                                .fillMaxWidth()
                                .background(color = Color(bokmaal))

                        ) {
                            SettingsText(
                                fontSize = 14,
                                color = 0xFFFFFFFF,
                                content = "Norsk (Bokmål)",
                                start = 10,
                                top = 5,
                                end = 5,
                                bottom = 5
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clickable(onClick = {
                                    //nynorsk = notClicked
                                    bokmaal = notClicked
                                    engelsk = clicked

                                    valgtSpråk = "Engelsk"
                                })
                                .padding(5.dp, 3.dp)
                                .fillMaxWidth()
                                .background(color = Color(engelsk))

                        ) {
                            SettingsText(
                                fontSize = 14,
                                color = 0xFFFFFFFF,
                                content = "Engelsk",
                                start = 10,
                                top = 5,
                                end = 5,
                                bottom = 5
                            )
                        }
                    }
                }
            }
        }
        /*
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
                    DropdownMenuItem(text = { Text(it.value) },
                        onClick = {
                            valgtSpråk = it.value
                            expandedSpråk = false
                            vm.lang = it.key
                            vm.loadAlerts(vm.lang, vm.lat, vm.lon)
                        }
                    )
                }
            }
        }*/

        if(showSearchbar){
            SearchBar(vm)
        }

        /*
        For å vise resultater fra API kall dersom dropdown ikke kommer opp
        Column {
            DisplayItems(items = vm.suggestions)
        }
         */
        /*Row(modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.Start
            ) {
                Text(modifier = Modifier.padding(8.dp), text = "Celsius/Fahrenheit")
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .weight(1f),
                horizontalAlignment = Alignment.End
            ) {
                Switch(checked = switchChecked, onCheckedChange = {
                    switchChecked = !switchChecked
                    valgtTemperatur = if (valgtTemperatur == "Celsius") {
                        "Fahrenheit" //add this to viewmodel so we can process this in repo?
                    } else {
                        "Celsius"
                    }
                    Log.i("TEMPERATUR", valgtTemperatur)
                })
            }
        }
*/

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

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp, bottom = 20.dp),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "Idag",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(start = 15.dp),
                            style = TextStyle(
                                color = Color.White
                            )
                        )

                        Text(
                            text = vm.currentFormatted,
                            fontSize = 30.sp,
                            style = TextStyle(
                                color = Color.White
                            ),
                            modifier = Modifier.padding(start = 15.dp, bottom = 20.dp)
                        )
                    }



                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                                .padding(start = 25.dp, end = 40.dp),
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
                        // Kolonne for temperatur og værbeskrivelse
                        Column(
                            modifier = Modifier
                                .padding(bottom = 60.dp)
                        ) {
                            val temperatureText = if (valgtTemperatur == "Celsius") {
                                "  ${vm.weatherData!!.properties.timeseries[0].data.instant.details.air_temperature.roundToInt()}°C"
                            } else {
                                "  ${(vm.weatherData!!.properties.timeseries[0].data.instant.details.air_temperature * 1.8 + 32).roundToInt()}°F"
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
                                val weatherSentence = "  Det $currentWeatherDescription"
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
                            //Spacer(modifier = Modifier.weight(1f))

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
                        //Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 20.dp, end = 20.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            val vind =
                                vm.weatherData!!.properties.timeseries[0].data.instant.details.wind_speed
                            Text(
                                text = "${vind}m/s",
                                fontSize = 18.sp,
                                style = TextStyle(
                                    color = Color.White
                                )
                            )

                            Spacer(modifier = Modifier.weight(1f))

                            val nedbør =
                                vm.weatherData!!.properties.timeseries[0].data.next_1_hours.details.precipitation_amount
                            Text(
                                text = "${nedbør}mm",
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
                            modifier = Modifier
                                .padding(start = 8.dp),
                            style = TextStyle(
                                color = Color.White
                            )
                        )
                    }

                    val uvStyrkeNå =
                        vm.weatherData!!.properties.timeseries[0].data.instant.details.ultraviolet_index_clear_sky

                    val uvStyrkeTekst = when {
                        vm.weatherData != null && uvStyrkeNå < 3.0 -> "Lavt"
                        vm.weatherData != null && uvStyrkeNå >= 3.0 && uvStyrkeNå < 6.0 -> "Medium"
                        vm.weatherData != null && uvStyrkeNå >= 6.0 && uvStyrkeNå < 8.0 -> "Høyt"
                        else -> "Veldig høyt"
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        UVScale(uvIndex = uvStyrkeNå)
                        Text(
                            text = "$uvStyrkeNå - $uvStyrkeTekst",
                            fontSize = 20.sp,
                            modifier = Modifier.padding(top = 5.dp, bottom = 10.dp),
                            style = TextStyle(
                                color = Color.White
                            ),
                            textAlign = TextAlign.Center
                        )
                    }

                    // Will only show alerts and take up space on screen if there are any active alerts in the area
                    if (vm.alertsData!!.features.isNotEmpty()) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .padding(start = 30.dp, end = 30.dp, top = 30.dp)
                        )
                        {
                            var i =
                                0 //noe hardkodet teller for metAlertsIcons. Mulig med bedre løsning, men kan ta tid å finne.
                            // vm.sortedAlerts.forEach //Bruk denne for å fjerne duplikater.
                            // Problem: I tilfellet det er duplikater, vil vm.metAlertsIcons[i] vise feil ikoner.
                            // Det vil iterere over ikonene som om det ikke er duplikater = gust gust vs. gust flood (filtered).
                            // Den første vil vise riktige ikoner. Den andre vil vise gust gust fortsatt.
                            vm.alertsData!!.features.forEach {
                                //drop for løkke, make map then set of eventawarenessname and instruction.
                                // compare feature with feature? if already in there or smth like that.
                                // this processing should happen in ViewModel. Map is already unique by default :)
                                WarningBox(
                                    headline = it.properties.eventAwarenessName,
                                    subtitle = "",
                                    info = it.properties.instruction,
                                )
                                i++
                            }
                        }
                    }

                    /*
                        AsyncImage(
                            modifier = Modifier
                                .size(100.dp),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://raw.githubusercontent.com/nrkno/yr-warning-icons/master/design/svg/icon-warning-generic-red.svg")
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentDescription = "UV-strength icon"
                        )
                        Text(
                            text = "Bruk solkrem med høy faktor flere ganger gjennom dagen. Søk etter skygge! Bruk klær, hodeplagg og solbriller. Husk å ta pauser fra sola ofte, spesielt under kl. 12-15.",
                            modifier = Modifier.padding(14.dp)
                        )

                     */

                    Column(
                        modifier = Modifier
                            .padding(start = 30.dp, end = 30.dp, top = 30.dp, bottom = 30.dp)
                    ) {
                        if (uvStyrkeNå >= 8.0) {
                            WarningBox(
                                headline = "Veldig høy UV-indeks!",
                                subtitle = "",
                                info = "Bruk solkrem med høy faktor flere ganger gjennom dagen. Søk etter skygge! Bruk klær, hodeplagg og solbriller. Husk å ta pauser fra sola ofte, spesielt under kl. 12-15.",
                            )
                        } else if (uvStyrkeNå >= 6.0 && uvStyrkeNå < 8) {
                            WarningBox(
                                headline = "Høy UV-indeks!",
                                subtitle = "",
                                info = "Husk å ta på solkrem med høy faktor! Bruk klær, hodeplagg og solbriller. Husk å ta pauser fra sola.",
                            )
                        } else if (uvStyrkeNå >= 3 && uvStyrkeNå < 6) {
                            WarningBox(
                                headline = "Middels UV-indeks",
                                subtitle = "",
                                info = "Husk å ta på solkrem hvis du skal være ute lenge!",
                            )
                        }
                    }

                    Text(
                        text = "  Idag",
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
                            //.background(color = Color(0xFF272D34))
                    ) {
                        Row(modifier = Modifier.horizontalScroll(scrollState)) {
                            for (i in 1..13) {
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

                                var formattedTime = String.format(
                                    "%02d:00",
                                    time
                                )// Formatere tiden til alltid å ha to sifre

                                BoxComponent(
                                    time = formattedTime, // Legger til tid som en parameter i BoxComponent
                                    temperature = if (valgtTemperatur == "Celsius") {
                                        "${vm.weatherData!!.properties.timeseries[i].data.instant.details.air_temperature.roundToInt()}°C"
                                    } else {
                                        "${(vm.weatherData!!.properties.timeseries[i].data.instant.details.air_temperature * 1.8 + 32).roundToInt()}°F"
                                    },
                                    windSpeed = "${vm.weatherData!!.properties.timeseries[i].data.instant.details.wind_speed}m/s",
                                    precipitation = "${vm.weatherData!!.properties.timeseries[i].data.next_1_hours.details.precipitation_amount}mm",
                                    uvStyrke = when {
                                        vm.weatherData != null && vm.weatherData!!.properties.timeseries[i].data.instant.details.ultraviolet_index_clear_sky < 3.0 -> "${vm.weatherData!!.properties.timeseries[i].data.instant.details.ultraviolet_index_clear_sky} Lavt"
                                        vm.weatherData != null && vm.weatherData!!.properties.timeseries[i].data.instant.details.ultraviolet_index_clear_sky >= 3.0 && vm.weatherData!!.properties.timeseries[i].data.instant.details.ultraviolet_index_clear_sky < 6.0 -> "${vm.weatherData!!.properties.timeseries[i].data.instant.details.ultraviolet_index_clear_sky} Medium"
                                        vm.weatherData != null && vm.weatherData!!.properties.timeseries[i].data.instant.details.ultraviolet_index_clear_sky >= 6.0 && vm.weatherData!!.properties.timeseries[i].data.instant.details.ultraviolet_index_clear_sky < 8.0 -> "${vm.weatherData!!.properties.timeseries[i].data.instant.details.ultraviolet_index_clear_sky} Høyt"
                                        else -> "${vm.weatherData!!.properties.timeseries[i].data.instant.details.ultraviolet_index_clear_sky} Veldig høyt"
                                    },
                                    width = 130,
                                    height = 280,
                                    expanded = remember { mutableStateOf(false) },
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

                    //Small box component -> only time and icons
                    /*
                    Row(modifier = Modifier.horizontalScroll(scrollState)) {
                        for (i in 1..13) {
                            var time: Int =
                                vm.weatherData!!.properties.timeseries[i].time.removeRange(0, 11)
                                    .removeRange(2, 9)
                                    .toInt() + vm.offset // Lokal tid siden locationForecast er i UTC/STD.
                            var formattedTime = String.format(
                                "%02d:00",
                                time
                            ) // Formatere tiden til alltid å ha to sifre
                            if (time >= 24) {
                                time -= 24
                                formattedTime = String.format(
                                    "%02d:00",
                                    time
                                ) // Formatere tiden på nytt hvis den overstiger 24 timer
                            }
                            SmallBoxComponent(
                                time = formattedTime,
                                width = 100,
                                height = 150,
                                weatherIcon = {
                                    AsyncImage(
                                        modifier = Modifier.size(70.dp),
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data(vm.locationForecastIcons[i])
                                            .decoderFactory(SvgDecoder.Factory())
                                            .build(),
                                        contentDescription = "Weather icon"
                                    )
                                }
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                        }
                    }
                    */

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
                        Calendar.MONDAY to "man",
                        Calendar.TUESDAY to "tir",
                        Calendar.WEDNESDAY to "ons",
                        Calendar.THURSDAY to "tor",
                        Calendar.FRIDAY to "fre",
                        Calendar.SATURDAY to "lør",
                        Calendar.SUNDAY to "søn"
                    )

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Kommende dager:",
                            fontSize = 30.sp,
                            modifier = Modifier
                                .padding(top = 60.dp, bottom = 10.dp),
                            style = TextStyle(
                                color = Color.White
                            )
                        )

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

                            val maxTemp = dayData?.first?.roundToInt()
                            val minTemp = dayData?.second?.roundToInt()


                            // Formattert dato (ukedag, måned, dato)
                            val formattedDate = "$dayOfWeekText, $monthText $dayOfMonth"

                            Line()
                            // Vis maks- og minimumstemperaturene for dagen
                            DayTemperatureItem(
                                date = formattedDate,
                                maxTemperature = maxTemp!!,
                                minTemperature = minTemp!!,
                                valgtTemperatur = valgtTemperatur
                            )
                        }
                        Line()
                    }
                }
            }




                    /*
                    Row(modifier = Modifier.horizontalScroll(scrollState)) {
                        for (i in 1..13) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.size(120.dp, 280.dp)
                            ) {
                                var time: Int =
                                    vm.weatherData!!.properties.timeseries[i].time.removeRange(
                                        0,
                                        11
                                    )
                                        .removeRange(2, 9)
                                        .toInt()+vm.offset //local time since locationForecast is in UTC/STD.
                                if (time >= 24){
                                    time -= 24
                                }
                                Text(text = "kl. $time", fontSize = 30.sp)
                                AsyncImage(
                                    modifier = Modifier.size(70.dp),
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(vm.locationForecastIcons[i])
                                        .decoderFactory(SvgDecoder.Factory())
                                        .build(),
                                    contentDescription = "Weather icon",
                                )
                                if (valgtTemperatur == "Celsius") {
                                    Text(
                                        text = "${vm.weatherData!!.properties.timeseries[i].data.instant.details.air_temperature.roundToInt()}°C",
                                        fontSize = 30.sp,
                                        fontWeight = Bold
                                    )
                                } else if (valgtTemperatur == "Fahrenheit") {
                                    Text(
                                        text = "${(vm.weatherData!!.properties.timeseries[i].data.instant.details.air_temperature * 1.8 + 32).roundToInt()}°F",
                                        fontSize = 30.sp,
                                        fontWeight = Bold
                                    )
                                }
                                Text(
                                    text = "Vind: ${vm.weatherData!!.properties.timeseries[i].data.instant.details.wind_speed}m/s",
                                    fontSize = 15.sp
                                )

                                Text(
                                    text = "Nedbør: ${vm.weatherData!!.properties.timeseries[i].data.next_1_hours.details.precipitation_amount}mm",
                                    fontSize = 15.sp
                                )

                                if (vm.weatherData!!.properties.timeseries[i].data.instant.details.ultraviolet_index_clear_sky >= 3.0 && vm.weatherData!!.properties.timeseries[i].data.instant.details.ultraviolet_index_clear_sky < 6.0) {
                                    AsyncImage(
                                        modifier = Modifier
                                            .size(40.dp),
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data("https://raw.githubusercontent.com/nrkno/yr-warning-icons/master/design/svg/icon-warning-generic-yellow.svg")
                                            .decoderFactory(SvgDecoder.Factory())
                                            .build(),
                                        contentDescription = "UV-strength icon."
                                    )
                                    //Text(text = "Husk å ta på solkrem hvis du skal være ute lenge!", modifier = Modifier.padding(14.dp))
                                } else if (vm.weatherData!!.properties.timeseries[i].data.instant.details.ultraviolet_index_clear_sky >= 6.0 && vm.weatherData!!.properties.timeseries[i].data.instant.details.ultraviolet_index_clear_sky < 8.0) {
                                    AsyncImage(
                                        modifier = Modifier
                                            .size(40.dp),
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data("https://raw.githubusercontent.com/nrkno/yr-warning-icons/master/design/svg/icon-warning-generic-orange.svg")
                                            .decoderFactory(SvgDecoder.Factory())
                                            .build(),
                                        contentDescription = "UV-strength icon."
                                    )
                                    //Text(text = "Husk å ta på solkrem med høy faktor! Bruk klær, hodeplagg og solbriller. Husk å ta pauser fra sola.", modifier = Modifier.padding(14.dp))
                                } else if (vm.weatherData!!.properties.timeseries[i].data.instant.details.ultraviolet_index_clear_sky >= 8.0) {
                                    AsyncImage(
                                        modifier = Modifier
                                            .size(40.dp),
                                        model = ImageRequest.Builder(LocalContext.current)
                                            .data("https://raw.githubusercontent.com/nrkno/yr-warning-icons/master/design/svg/icon-warning-generic-red.svg")
                                            .decoderFactory(SvgDecoder.Factory())
                                            .build(),
                                        contentDescription = "UV-strength icon."
                                    )
                                    //Text(text = "Bruk solkrem med høy faktor flere ganger gjennom dagen. Søk etter skygge! Bruk klær, hodeplagg og solbriller. Husk å ta pauser fra sola ofte, spesielt under kl. 12-15.", modifier = Modifier.padding(14.dp))
                                }
                                Text(
                                    text = "UV styrke: ${vm.weatherData!!.properties.timeseries[i].data.instant.details.ultraviolet_index_clear_sky}",
                                    fontSize = 20.sp,
                                    modifier = Modifier.padding(start = 23.dp)
                                )
                            }
                        }
                    }

                     */
                }
            }

@Composable
fun DisplayItems(items: List<ApiProperties>?) {
    Column {
        items?.forEach { item ->
            Text(text = item.formatted)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(vm: HomeViewModel) {

    val items = vm.suggestions
    val sug = mutableListOf<String>()
    items.forEach { item ->
        sug.add(item.formatted)
    }

    var category by remember {
        mutableStateOf("")
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

    // Category Field
    Column(
        modifier = Modifier
            .padding(top = 30.dp)
            .padding(horizontal = 30.dp)
            .fillMaxWidth()
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
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(heightTextFields)
                        .clip(RoundedCornerShape(50.dp))
                        .border(
                            width = 1.8.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(50.dp)
                        )
                        .focusRequester(vm.focusRequester)
                        .onKeyEvent { event ->
                            if (event.type == KeyEventType.KeyUp && event.key == Key.Enter) {
                                vm.expanded = false
                                keyboardController?.hide()
                                true
                            } else {
                                false
                            }
                        }
                        .onGloballyPositioned { coordinates ->
                            textFieldSize = coordinates.size.toSize()
                        },
                    value = category,
                    onValueChange = {
                        category = it
                        vm.expanded = true
                        vm.loadSuggestions(it)
                    },
                    placeholder = { Text("Søk i lokasjoner") },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color(0xFFFFFFFF),
                        focusedIndicatorColor = Color(0xFFFFFFFF),
                        unfocusedIndicatorColor = Color(0xFFFFFFFF),
                        cursorColor = Color.Black
                    ),
                    textStyle = TextStyle(
                        color = Color.Black,
                        fontSize = 16.sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done
                    ),
                    singleLine = true,
                    trailingIcon = {
                            Row {
                                if (category.isNotEmpty()) {
                                    IconButton(onClick = { category = "" }) {
                                        Icon(
                                            imageVector = Icons.Rounded.Clear,
                                            contentDescription = "Clear",
                                            tint = Color.Black
                                        )
                                    }
                                }
                                IconButton(onClick = { vm.expanded = !vm.expanded }) {
                                    Icon(
                                        modifier = Modifier.size(24.dp),
                                        imageVector = Icons.Rounded.KeyboardArrowDown,
                                        contentDescription = "arrow",
                                        tint = Color.Black
                                    )
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

                        if (category.isNotEmpty()) {
                            items(
                                sug.filter {
                                    it.lowercase()
                                        .contains(category.lowercase()) || it.lowercase()
                                        .contains("others")
                                }
                                    .sorted()
                            ) {
                                DropdownRow(vm, focusManager, keyboardController, title = it) { title ->
                                    category = title
                                    vm.expanded = true
                                }
                            }
                        } else {
                            items(
                                sug.sorted()
                            ) {
                                DropdownRow(vm, focusManager, keyboardController, title = it) { title ->
                                    category = title
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
            }
            .padding(10.dp)
    ) {
        Text(text = title, fontSize = 16.sp)
    }

}

//Composable to make the warning box
@Composable
fun WarningBox(headline: String, subtitle: String, info: String) {
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f, label = ""
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFCF72),
        ),
        modifier = Modifier
            .clickable { expandedState = !expandedState }
            //.padding(2.dp)
            //.alpha(0.7f)
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
                    painter = painterResource(id = R.drawable.warningicon),
                    contentDescription = "Warning icon",
                    modifier = Modifier
                        .size(115.dp)
                        .padding(top = 20.dp, bottom = 20.dp)
                )
                /*
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    modifier = Modifier
                        .size(50.dp, 50.dp)
                        .padding(10.dp, 0.dp, 0.dp, 0.dp)
                )
*/

                Column {
                    Text(
                        text = headline,
                        style = TextStyle(
                            fontSize = 20.sp,
                            fontWeight = Bold,
                            color = Color(0xFF000000),
                        )
                    )
                    SettingsText(
                        fontSize = 13,
                        color = 0xFF000000,
                        content = subtitle,
                        start = 21,
                        top = 0,
                        end = 120,
                        bottom = 15
                    )
                }

                IconButton(
                    modifier = Modifier
                        .weight(1f)
                        //.alpha(0.2f)
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
                SettingsText(
                    fontSize = 15,
                    color = 0xFF000000,
                    content = info,
                    start = 10,
                    top = 5,
                    end = 5,
                    bottom = 20
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
        .padding(5.dp, 5.dp)
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
            .padding(start = 40.dp, end = 40.dp)
            .width(1.dp)
            .height(100.dp)
            .background(color = Color(0xFFFFFFFF))
    )
}

//Makes switchbuttons
@Composable
fun SwitchButton(){
    var checked by remember { mutableStateOf(true) }
    Switch(
        modifier = Modifier
            .size(2.dp)
            .padding(25.dp, 15.dp, 0.dp, 0.dp),
        checked = checked,
        onCheckedChange = {
            checked = it
        },
        colors = SwitchDefaults.colors(
            checkedThumbColor =  Color(0xFF38424D),
            checkedTrackColor = Color(0xFFFFFFFF),
            uncheckedThumbColor = Color(0xFFFFFFFF),
            uncheckedTrackColor = Color(0xFF38424D),
        )
    )
}

//Makes the settings card
@Composable
fun SettingsCard(){
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f, label = ""
    )

    var chosenLanguage by remember { mutableStateOf("Norsk (bokmål)") }

    var chosenTemperature by remember {
        mutableStateOf("Celsius") //Default value will be Celsius. Can choose Fahrenheit.
    }
    Card (modifier = Modifier
        .width(242.dp)
        .animateContentSize(
            animationSpec = tween(
                durationMillis = 300,
                easing = LinearOutSlowInEasing
            )
        ),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF38424D),
        )
    ) {
        Column (modifier = Modifier.fillMaxWidth()){
            //Settings icon
            IconButton(
                modifier = Modifier
                    .padding(190.dp, 0.dp, 0.dp, 0.dp)
                    .size(50.dp),
                onClick = {
                },

                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = Color(0xFFFFFFFF)
                )
            ){
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }

            Row {
                SettingsText(14, color = 0xFFFFFFFF, content = "Posisjonsbasert værvarsel", 10, 5, 12, 5)
                SwitchButton()
            }
            Line()

            Row {
                var celsius by remember { mutableStateOf(0xFFFFFFFF)}
                var fahrenheit by remember { mutableStateOf(0xFF8C9299)}
                var checked by remember { mutableStateOf(true) }

                SettingsText(14, color = celsius, content = "Celsius", 10, 5, 0, 5)
                SettingsText(14, color = 0xFFFFFFFF, content = " / ", 0, 5, 0, 5)
                SettingsText(14, color = fahrenheit, content = "Fahrenheit", 0, 5, 50, 5)

                Switch(
                    modifier = Modifier
                        .size(2.dp)
                        .padding(25.dp, 15.dp, 0.dp, 0.dp),
                    checked = checked,
                    onCheckedChange = {
                        checked = it
                        if (celsius == 0xFFFFFFFF) {
                            celsius = 0xFF8C9299
                            fahrenheit = 0xFFFFFFFF
                        }
                        else {
                            celsius = 0xFFFFFFFF
                            fahrenheit = 0xFF8C9299
                        }
                        chosenTemperature = if (chosenTemperature == "Celsius") {
                            "Fahrenheit" //add this to viewmodel so we can process this in repo?
                        } else {
                            "Celsius"
                        }
                        Log.i("TEMPERATUR", chosenTemperature)

                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor =  Color(0xFF38424D),
                        checkedTrackColor = Color(0xFFFFFFFF),
                        uncheckedThumbColor = Color(0xFFFFFFFF),
                        uncheckedTrackColor = Color(0xFF38424D),
                    )
                )
            }
            Line()

            Row {
                SettingsText(14, color = 0xFFFFFFFF, content = "Varslinger", 10, 5, 110, 5)
                SwitchButton()
            }

        }
        Line()

        Row {


            SettingsText(14, color = 0xFFFFFFFF, content = "Språk", 10, 5, 15, 5)
            SettingsText(14, color = 0xFF8C9299, content = chosenLanguage, 10, 5, 20, 5)

            IconButton(
                modifier = Modifier
                    .padding(0.dp, 0.dp, 10.dp, 0.dp)
                    .rotate(rotationState),
                onClick = {
                    expandedState = !expandedState

                },
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = Color(0xFFFFFFFF)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Drop-Down Arrow"
                )
            }

        }
        if (expandedState) {
            val notClicked = 0xFF272D34
            val clicked = 0xFF586471

           /* var nynorsk by remember{mutableStateOf(
                if (chosenLanguage == "Norsk (nynorsk)") clicked
                else notClicked
            ) }*/
            var bokmaal by remember{mutableStateOf(
                if (chosenLanguage == "Norsk (bokmål)") clicked
                else notClicked
            ) }
            var engelsk by remember{mutableStateOf(
                if (chosenLanguage == "Engelsk") clicked
                else notClicked
            ) }

            //In case we want to implement nynorsk:
/*
            Box (modifier = Modifier
                .clickable(onClick = {
                    nynorsk = clicked
                    bokmaal = notClicked
                    engelsk = notClicked

                    chosenLanguage = "Norsk (nynorsk)"

                })
                .padding(5.dp, 3.dp)
                .fillMaxWidth()
                .background(color = Color(nynorsk))

            ) {
                SettingsText(fontSize = 14, color = 0xFFFFFFFF, content = "Norsk (Nynorsk)", start = 10, top = 5, end = 5, bottom = 5)
            }*/

            Box (modifier = Modifier
                .clickable(onClick = {
                    //nynorsk = notClicked
                    bokmaal = clicked
                    engelsk = notClicked

                    chosenLanguage = "Norsk (bokmål)"
                })
                .padding(5.dp, 3.dp)
                .fillMaxWidth()
                .background(color = Color(bokmaal))

            ) {
                SettingsText(fontSize = 14, color = 0xFFFFFFFF, content = "Norsk (Bokmål)", start = 10, top = 5, end = 5, bottom = 5)
            }

            Box (modifier = Modifier
                .clickable(onClick = {
                    //nynorsk = notClicked
                    bokmaal = notClicked
                    engelsk = clicked

                    chosenLanguage = "Engelsk"
                })
                .padding(5.dp, 3.dp)
                .fillMaxWidth()
                .background(color = Color(engelsk))

            ) {
                SettingsText(fontSize = 14, color = 0xFFFFFFFF, content = "Engelsk", start = 10, top = 5, end = 5, bottom = 5)
            }
        }
    }
}

//This box component is for the hour by hour weather forecast - if you want to use it!
@Composable
fun BoxComponent(
    time: String,
    temperature: String,
    windSpeed: String,
    precipitation: String,
    uvStyrke: String,
    width: Int,
    height: Int,
    weatherIcon: @Composable () -> Unit,
    expanded: MutableState<Boolean>

) {
    val rotationState = animateFloatAsState(
        targetValue = if (expanded.value) 180f else 0f
    )

    Card(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Color(0xFFFFFFFF),
                shape = RoundedCornerShape(size = 20.dp)
            )
            .width(width.dp)
            .height(if (expanded.value) height.dp else 160.dp)
            //.background(color = Color(0xFF4A535D), shape = RoundedCornerShape(size = 20.dp))
            .clickable { expanded.value = !expanded.value },
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
                    fontWeight = FontWeight.Bold,
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
            IconButton(
                onClick = { expanded.value = !expanded.value },
                modifier = Modifier
                    .weight(1f)
                    .rotate(rotationState.value),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = Color(0xFFFFFFFF)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Drop-Down Arrow"
                )
            }
            if (expanded.value) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                Image(
                    painter = painterResource(id = R.drawable.uvicon),
                    contentDescription = "UV Icon",
                    modifier = Modifier.size(23.dp)
                )
                Text(
                    text = uvStyrke,
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
    }
}

@Composable
fun DayTemperatureItem(date: String, maxTemperature: Int, minTemperature: Int, valgtTemperatur: String) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(start = 25.dp, end = 25.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = date,
            modifier = Modifier.weight(1f),
            style = TextStyle(
                color = Color.White
            )
        )
        Spacer(modifier = Modifier.width(10.dp))
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
            textAlign = TextAlign.End,
            style = TextStyle(
                color = Color.White
            )

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
                )
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
            drawCircle(
                color = Color.White,
                center = Offset(uvIndexPosition, size.height / 2),
                radius = 8.dp.toPx()
            )
        }
    }
}