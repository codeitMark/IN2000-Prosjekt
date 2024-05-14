package no.uio.ifi.in2000.project.data.forecast

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.gson.gson
import no.uio.ifi.in2000.project.model.forecast.LocationForecastResponse


data class LocationForecastDataSource(private val path: String = "https://gw-uio.intark.uh-it.no/in2000/") {
    var authorized = false
    var connected = false
    private val client = HttpClient(CIO){
        install(ContentNegotiation){
            gson()
        }
        defaultRequest { 
            url(path)
            header("X-Gravitee-Api-Key", "2da3279c-ee4c-4d21-955e-d13822ff578c")
        }
    }

    suspend fun getWeather(lat: Double, lon: Double): LocationForecastResponse? {
        return try {
            val httpResponse = client.get("weatherapi/locationforecast/2.0/complete?lat=$lat&lon=$lon")
            connected = true
            //Log.i("LocationForecastDataSource", "response ${httpResponse.status.value}")
            //val response = httpResponse.body<LocationForecastResponse>()
            //return response
            if (httpResponse.status.value == 200){
                authorized = true
                httpResponse.body<LocationForecastResponse>() //Samme som det kommentert over
            } else{
                null //Innholdet blir null uansett, bare at man får LocationForecastResponse objekt med parameterne null. Enklere å bare gjøre det til null tidlig nå, så slipper vi å lage en if-sjekk senere for om det det som er inni er null.
            }
        } catch (e: Exception){
            //No internet connection because other errors will return an empty list of the respective Response data class.
            //Log.w("LocationForecastDataSource", "Error getting weather data! No internet connection?") //i = info, w = warning, e = error
            //Log.e("LocationForecastDataSource", "$e")
            null
        }
    }
}