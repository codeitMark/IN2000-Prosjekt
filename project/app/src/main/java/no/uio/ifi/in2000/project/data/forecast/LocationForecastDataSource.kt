package no.uio.ifi.in2000.project.data.forecast

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.gson.gson

data class LocationForecastDataSource(private val path: String = "https://gw-uio.intark.uh-it.no/in2000/") {
    private val client = HttpClient(){
        install(ContentNegotiation){
            gson()
        }
    }

    suspend fun getObject(){
        val klienten = HttpClient(CIO){
            defaultRequest {
                url(path)
                header("X-Gravitee-Api-Key", "2da3279c-ee4c-4d21-955e-d13822ff578c")
            }
        }
        val response = client.get("weatherapi/locationforecast/2.0/compact?lat=60&lon=11")
        Log.i("LocationForecastDataSource", "response ${response.status.value}")
    }
}