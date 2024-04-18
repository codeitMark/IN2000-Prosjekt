package no.uio.ifi.in2000.project.ui.home

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import no.uio.ifi.in2000.project.model.search.ApiProperties
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun HomeScreen(vm: HomeViewModel = viewModel()) {
    val scrollState = rememberScrollState()
    val scrollStateVertical = rememberScrollState()

    var expandedSpråk by remember{
        mutableStateOf(false)
    }

    var switchChecked by remember{
        mutableStateOf(false)
    }

    var valgtTemperatur by remember{
        mutableStateOf("Celsius") //Default value will be Celsius. Can choose Fahrenheit.
    }

    val språk = LinkedHashMap<String, String>()
    språk["no"] = "Norsk"
    språk["en"] = "English"

    var valgtSpråk by remember{
        mutableStateOf("Norsk") //Default value will be "no", norsk.
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollStateVertical),
        //verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                    DropdownMenuItem(text = {Text(it.value)},
                        onClick = {
                            valgtSpråk = it.value
                            expandedSpråk = false
                            vm.lang = it.key
                            vm.loadAlerts(vm.lang, vm.lat, vm.lon)
                        }
                    )
                }
            }
        }
        SearchBar(vm)

        /*
        For å vise resultater fra API kall dersom dropdown ikke kommer opp
        Column {
            DisplayItems(items = vm.suggestions)
        }
         */
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            Column(modifier = Modifier.fillMaxWidth().padding(8.dp).weight(1f), horizontalAlignment = Alignment.Start) {
                Text(modifier = Modifier.padding(8.dp), text = "Celsius/Fahrenheit")
            }
            Column(modifier = Modifier.fillMaxWidth().padding(8.dp).weight(1f), horizontalAlignment = Alignment.End) {
                Switch(checked = switchChecked, onCheckedChange = {switchChecked = !switchChecked
                    valgtTemperatur = if (valgtTemperatur == "Celsius"){
                        "Fahrenheit" //add this to viewmodel so we can process this in repo?
                    } else{
                        "Celsius"
                    }
                    Log.i("TEMPERATUR", valgtTemperatur)
                })
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

                    Text(text = vm.currentFormatted, fontSize = 30.sp)

                    if (valgtTemperatur == "Celsius"){
                        Text(
                            text = "${vm.weatherData!!.properties.timeseries[0].data.instant.details.air_temperature.roundToInt()}°C",
                            fontSize = 50.sp
                        )
                    } else if (valgtTemperatur == "Fahrenheit"){
                        Text(
                            text = "${(vm.weatherData!!.properties.timeseries[0].data.instant.details.air_temperature*1.8+32).roundToInt()}°F",
                            fontSize = 50.sp
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

                    val uvStyrkeNå = vm.weatherData!!.properties.timeseries[0].data.instant.details.ultraviolet_index_clear_sky
                    Text(
                        text = "UV styrke: $uvStyrkeNå",
                        fontSize = 20.sp,
                        modifier = Modifier.padding(top = 30.dp)
                    )

                    if (uvStyrkeNå >= 3.0 && uvStyrkeNå < 6.0){
                        AsyncImage(
                            modifier = Modifier
                                .size(140.dp),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://raw.githubusercontent.com/nrkno/yr-warning-icons/master/design/svg/icon-warning-generic-yellow.svg")
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentDescription = "UV-strength icon"
                        )
                        Text(text = "Husk å ta på solkrem hvis du skal være ute lenge!", modifier = Modifier.padding(14.dp))
                    } else if (uvStyrkeNå >= 6.0 && uvStyrkeNå < 8.0){
                        AsyncImage(
                            modifier = Modifier
                                .size(140.dp),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://raw.githubusercontent.com/nrkno/yr-warning-icons/master/design/svg/icon-warning-generic-orange.svg")
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentDescription = "UV-strength icon"
                        )
                        Text(text = "Husk å ta på solkrem med høy faktor! Bruk klær, hodeplagg og solbriller. Husk å ta pauser fra sola.", modifier = Modifier.padding(14.dp))
                    } else if (uvStyrkeNå >= 8.0){
                        AsyncImage(
                            modifier = Modifier
                                .size(140.dp),
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("https://raw.githubusercontent.com/nrkno/yr-warning-icons/master/design/svg/icon-warning-generic-red.svg")
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentDescription = "UV-strength icon"
                        )
                        Text(text = "Bruk solkrem med høy faktor flere ganger gjennom dagen. Søk etter skygge! Bruk klær, hodeplagg og solbriller. Husk å ta pauser fra sola ofte, spesielt under kl. 12-15.", modifier = Modifier.padding(14.dp))
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
                                Text(text = it.properties.instruction, modifier = Modifier.padding(14.dp)) //Tidligere it.value (med vm.sortedalerts.forEach)
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
                                modifier = Modifier.size(120.dp, 280.dp)
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
                                if (valgtTemperatur == "Celsius"){
                                    Text(
                                        text = "${vm.weatherData!!.properties.timeseries[i].data.instant.details.air_temperature.roundToInt()}°C",
                                        fontSize = 30.sp,
                                        fontWeight = Bold
                                    )
                                } else if (valgtTemperatur == "Fahrenheit"){
                                    Text(
                                        text = "${(vm.weatherData!!.properties.timeseries[i].data.instant.details.air_temperature*1.8+32).roundToInt()}°F",
                                        fontSize = 30.sp,
                                        fontWeight = Bold
                                    )
                                }
                                if (vm.weatherData!!.properties.timeseries[i].data.instant.details.ultraviolet_index_clear_sky >= 3.0 && vm.weatherData!!.properties.timeseries[i].data.instant.details.ultraviolet_index_clear_sky < 6.0){
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
                                } else if (vm.weatherData!!.properties.timeseries[i].data.instant.details.ultraviolet_index_clear_sky >= 6.0 && vm.weatherData!!.properties.timeseries[i].data.instant.details.ultraviolet_index_clear_sky < 8.0){
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
                                } else if (vm.weatherData!!.properties.timeseries[i].data.instant.details.ultraviolet_index_clear_sky >= 8.0){
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