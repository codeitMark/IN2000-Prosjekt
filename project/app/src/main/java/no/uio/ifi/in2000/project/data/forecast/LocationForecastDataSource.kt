package no.uio.ifi.in2000.project.data.forecast

import android.util.Log
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
    private val client = HttpClient(CIO){
        install(ContentNegotiation){
            gson()
        }
        defaultRequest { 
            url(path)
            header("X-Gravitee-Api-Key", "2da3279c-ee4c-4d21-955e-d13822ff578c")
        }
    }

    suspend fun getWeather(lat: Double, lon: Double): LocationForecastResponse? { //for try catch to work, LocationForecastResponse needs to be nullable (with a ? at the end). It is also edited in Repository's fetchWeather and HomeViewModel's init (!!).
        return try {
            val httpResponse = client.get("weatherapi/locationforecast/2.0/compact?lat=$lat&lon=$lon")
            Log.i("LocationForecastDataSource", "response ${httpResponse.status.value}")
            //val response = httpResponse.body<LocationForecastResponse>()
            //return response
            httpResponse.body<LocationForecastResponse>() //Samme som det kommentert over
        } catch (e: Exception){
            Log.w("LocationForecastDataSource", "No internet connection!") //i = info, w = warning, e = error
            Log.e("LocationForecastDataSource", "$e")
            null
            //could use something like emptyResponse like in homeViewModel, rather than null.
        }
    }
}

/*
*
* private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun getAlpacaParties(): List<PartyInfo> {
        return withContext(Dispatchers.IO) {
            try {
                val response: AlpacaPartiesResponse = client.get("https://www.uio.no/studier/emner/matnat/ifi/IN2000/v24/obligatoriske-oppgaver/alpacaparties.json").body()
                response.parties
            } catch (e: Exception) {
                println("No network connection")
                println(e)
                emptyList()
            }
        }
    }
*
* */