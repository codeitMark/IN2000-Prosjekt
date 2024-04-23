package no.uio.ifi.in2000.project.ui.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowColumnScopeInstance.align
import androidx.compose.foundation.layout.FlowRowScopeInstance.align
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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Size
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
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import no.uio.ifi.in2000.project.model.search.ApiProperties
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
        Row {
            IconButton(
                modifier = Modifier
                    //.padding(190.dp, 0.dp, 0.dp, 0.dp)
                    .size(50.dp)
                    //.align(Alignment.Start),

                , onClick = {
                    showSearchbar = !showSearchbar
                },


                /*colors = IconButtonDefaults.iconButtonColors(
                contentColor = Color(0xFFFFFFFF)
            )*/
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Settings"
                )
            }
            IconButton(
                modifier = Modifier
                    //.padding(190.dp, 0.dp, 0.dp, 0.dp)
                    .size(50.dp)
                    //.align(Alignment.End),

                , onClick = {
                    showSettings = !showSettings
                },


                /*colors = IconButtonDefaults.iconButtonColors(
                contentColor = Color(0xFFFFFFFF)
            )*/
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            }
            //settingsBox
            if (showSettings) {
                //var chosenLanguage by remember { mutableStateOf("Norsk (bokmål)") }

                //var chosenTemperature by remember {
                //   mutableStateOf("Celsius") //Default value will be Celsius. Can choose Fahrenheit.

                Card(
                    modifier = Modifier
                        //.align(Alignment.End)
                        .align(Alignment.Top)
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
                    Column(modifier = Modifier.fillMaxWidth()) {
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
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings"
                            )
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
                            var checked by remember { mutableStateOf(true) }

                            SettingsText(14, color = celsius, content = "Celsius", 10, 5, 0, 5)
                            SettingsText(14, color = 0xFFFFFFFF, content = " / ", 0, 5, 0, 5)
                            SettingsText(
                                14,
                                color = fahrenheit,
                                content = "Fahrenheit",
                                0,
                                5,
                                50,
                                5
                            )

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
                    Text(text = "Været", fontSize = 60.sp, fontWeight = Bold)

                    Text(text = vm.currentFormatted, fontSize = 30.sp)

                    if (valgtTemperatur == "Celsius") {
                        Text(
                            text = "${vm.weatherData!!.properties.timeseries[0].data.instant.details.air_temperature.roundToInt()}°C",
                            fontSize = 50.sp
                        )
                    } else if (valgtTemperatur == "Fahrenheit") {
                        Text(
                            text = "${(vm.weatherData!!.properties.timeseries[0].data.instant.details.air_temperature * 1.8 + 32).roundToInt()}°F",
                            fontSize = 50.sp
                        )
                    }

                    val currentWeatherDescription = when (vm.weatherData!!.properties.timeseries[0].data.next_1_hours.summary.symbol_code) {
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

                    if (currentWeatherDescription != null) {
                        val weatherSentence = "Det $currentWeatherDescription"
                        Text(
                            text = weatherSentence,
                            fontSize = 20.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    AsyncImage(
                        modifier = Modifier
                            .size(280.dp),
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(vm.locationForecastIcons[0])
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        contentDescription = "Weather icon"
                    )

                    val vind  = vm.weatherData!!.properties.timeseries[0].data.instant.details.wind_speed
                    Text(
                        text = "Vind: $vind m/s",
                        fontSize = 20.sp
                    )

                    val nedbør = vm.weatherData!!.properties.timeseries[0].data.next_1_hours.details.precipitation_amount
                    Text(
                        text = "Nedbør: $nedbør mm",
                        fontSize = 20.sp
                    )


                    val uvStyrkeNå =
                        vm.weatherData!!.properties.timeseries[0].data.instant.details.ultraviolet_index_clear_sky
                    Text(
                        text = "UV styrke: $uvStyrkeNå",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 30.dp)
                    )

                    if (uvStyrkeNå >= 3.0 && uvStyrkeNå < 6.0) {
                        AsyncImage(
                            modifier = Modifier
                                .size(140.dp),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://raw.githubusercontent.com/nrkno/yr-warning-icons/master/design/svg/icon-warning-generic-yellow.svg")
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentDescription = "UV-strength icon"
                        )
                        Text(
                            text = "Husk å ta på solkrem hvis du skal være ute lenge!",
                            modifier = Modifier.padding(14.dp)
                        )
                    } else if (uvStyrkeNå >= 6.0 && uvStyrkeNå < 8.0) {
                        AsyncImage(
                            modifier = Modifier
                                .size(140.dp),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://raw.githubusercontent.com/nrkno/yr-warning-icons/master/design/svg/icon-warning-generic-orange.svg")
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentDescription = "UV-strength icon"
                        )
                        Text(
                            text = "Husk å ta på solkrem med høy faktor! Bruk klær, hodeplagg og solbriller. Husk å ta pauser fra sola.",
                            modifier = Modifier.padding(14.dp)
                        )
                    } else if (uvStyrkeNå >= 8.0) {
                        AsyncImage(
                            modifier = Modifier
                                .size(140.dp),
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
                    }

                    val sunriseTime = vm.sunriseTime
                    Text(text = "Soloppgang: kl. $sunriseTime", fontSize = 20.sp)
                    val sunsetTime = vm.sunsetTime
                    Text(text = "Solnedgang: kl. $sunsetTime", fontSize = 20.sp)

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
                                WarningBox(headline = it.properties.eventAwarenessName, subtitle = "", info = it.properties.instruction, img = vm.metAlertsIcons!![i])
                                Spacer(modifier = Modifier.height(20.dp))
                                i++
                            }
                        }
                    }

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
                }
            }
        }
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
                        .border(
                            width = 1.8.dp,
                            color = Color.Black,
                            shape = RoundedCornerShape(15.dp)
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
                    placeholder = { Text("Enter any Location") },
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
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
                        IconButton(onClick = { vm.expanded = !vm.expanded }) {
                            Icon(
                                modifier = Modifier.size(24.dp),
                                imageVector = Icons.Rounded.KeyboardArrowDown,
                                contentDescription = "arrow",
                                tint = Color.Black
                            )
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
fun WarningBox(headline: String, subtitle: String, info: String, img: String) {
    var expandedState by remember { mutableStateOf(false) }
    val rotationState by animateFloatAsState(
        targetValue = if (expandedState) 180f else 0f, label = ""
    )

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFFCF72),
        ),
        modifier = Modifier
            .padding(2.dp)
            .alpha(0.7f)
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
                AsyncImage(
                    modifier = Modifier
                        .size(125.dp)
                        .padding(top = 20.dp, bottom = 20.dp),
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(img)
                        .decoderFactory(SvgDecoder.Factory())
                        .build(),
                    contentDescription = "Icon for an alert."
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
                        modifier = Modifier.padding(20.dp, 15.dp, 0.dp),
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
fun BoxComponent(content: String, width: Int, height: Int){
    Card(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = Color(0xFFFFFFFF),
                shape = RoundedCornerShape(size = 20.dp)
            )
            .width(width.dp)
            .height(height.dp)
            .background(color = Color(0xFF4A535D), shape = RoundedCornerShape(size = 20.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF4A535D)
        ),
        shape = RoundedCornerShape(size = 20.dp)
    ){
        Text(
            text = content,
            style = TextStyle(
                fontSize = 15.sp,
                //fontFamily = FontFamily(Font(R.font.inter)),
                fontWeight = FontWeight(300),
                color = Color(0xFFFFFFFF),

                ),
            modifier = Modifier
                .padding(16.dp),
            textAlign = TextAlign.Center)
    }

}



