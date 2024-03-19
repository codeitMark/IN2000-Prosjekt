package no.uio.ifi.in2000.project.data.alerts

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.gson.gson

data class MetAlertsDataSource(private val path: String = "https://gw-uio.intark.uh-it.no/in2000/") {
    private val client = HttpClient(){
        install(ContentNegotiation){
            gson()
        }
    }

    suspend fun getAlerts(){
        val klienten = HttpClient(CIO){
            defaultRequest {
                url(path)
                header("X-Gravitee-Api-Key", "2da3279c-ee4c-4d21-955e-d13822ff578c")
            }
        }
        val response = client.get("weatherapi/metalerts/2.0/current.json")
        Log.i("MetAlertsDataSource", "response ${response.status.value}")
    }
}