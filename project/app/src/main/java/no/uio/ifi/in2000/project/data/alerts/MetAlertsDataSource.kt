package no.uio.ifi.in2000.project.data.alerts

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.gson.gson
import no.uio.ifi.in2000.project.model.alerts.MetAlertsResponse

data class MetAlertsDataSource(private val path: String = "https://gw-uio.intark.uh-it.no/in2000/") {
    private val client = HttpClient(CIO){
        install(ContentNegotiation){
            gson()
        }
        defaultRequest {
            url(path)
            header("X-Gravitee-Api-Key", "2da3279c-ee4c-4d21-955e-d13822ff578c")
        }
    }
    
    private val emptyResponse = MetAlertsResponse(emptyList(), "empty", "empty", "empty")

    suspend fun getAlerts(lat: Double, lon: Double, lang: String): MetAlertsResponse { //finnes mange andre parametere, la til lang!
        return try {
            //val httpResponse = client.get("weatherapi/metalerts/2.0/current.json") //Guaranteed to have data
            val httpResponse = client.get("weatherapi/metalerts/2.0/current.json?lat=$lat&lon=$lon&lang=$lang") //Most likely no warnings. (Minimal data)
            //Log.i("LocationForecastDataSource", "response ${httpResponse.status.value}")
            httpResponse.body<MetAlertsResponse>()
        } catch (e: Exception){
            //Log.w("MetAlertsDataSource", "Error getting weather data! No internet connection?")
            //Log.e("MetAlertsDataSource", "$e")
            emptyResponse
        }
    }
}