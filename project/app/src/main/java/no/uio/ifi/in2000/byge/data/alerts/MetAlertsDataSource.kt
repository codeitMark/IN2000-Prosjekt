package no.uio.ifi.in2000.byge.data.alerts

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.gson.gson
import no.uio.ifi.in2000.byge.model.alerts.MetAlertsResponse

data class MetAlertsDataSource(private val path: String = "https://gw-uio.intark.uh-it.no/in2000/") {
    var authorized = false
    var connected = false
    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            gson()
        }
        defaultRequest {
            url(path)
            header("X-Gravitee-Api-Key", "2da3279c-ee4c-4d21-955e-d13822ff578c")
        }
    }

    suspend fun getAlerts(
        lang: String,
        lat: Double,
        lon: Double
    ): MetAlertsResponse? { //finnes mange andre parametere, la til lang!
        return try {
            //val httpResponse = client.get("weatherapi/metalerts/2.0/current.json?lang=$lang") //Guaranteed to have data
            val httpResponse =
                client.get("weatherapi/metalerts/2.0/current.json?lang=$lang&lat=$lat&lon=$lon") //Most likely no warnings. (Minimal data)
            //Log.i("LocationForecastDataSource", "response ${httpResponse.status.value}")
            connected = true
            if (httpResponse.status.value == 200) {
                authorized = true
                httpResponse.body<MetAlertsResponse>()
            } else {
                null
            }
        } catch (e: Exception) {
            //Log.w("MetAlertsDataSource", "Error getting weather data! No internet connection?") //No internet connection because other errors will return an empty list of the respective Response data class.
            //Log.e("MetAlertsDataSource", "$e")
            null
        }
    }
}