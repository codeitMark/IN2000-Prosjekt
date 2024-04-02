package no.uio.ifi.in2000.project.data.sunrise

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.gson.gson
import no.uio.ifi.in2000.project.model.sunrise.SunriseResponse

data class SunriseDataSource(private val path: String  = "https://api.met.no/weatherapi/sunrise/3.0/") {

    private val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }

    suspend fun getSunrise(lat: Double, lon: Double): SunriseResponse? {
        return try {
            val httpResponse = client.get("https://api.met.no/weatherapi/sunrise/3.0/sun?/$lat&lon=$lon")
            httpResponse.body<SunriseResponse>()
        } catch (e: Exception) {
            null
        }

    }
}