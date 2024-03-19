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
import kotlinx.serialization.Serializable
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

    suspend fun getWeather(): LocationForecastResponse { //add try catch for no internet connection?
        val httpResponse = client.get("weatherapi/locationforecast/2.0/compact?lat=58.775&lon=5.9")
        Log.i("LocationForecastDataSource", "response ${httpResponse.status.value}")
        //val response = httpResponse.body<LocationForecastResponse>()
        //return response
        val responseBody = httpResponse.body<LocationForecastResponse>()
        return responseBody
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