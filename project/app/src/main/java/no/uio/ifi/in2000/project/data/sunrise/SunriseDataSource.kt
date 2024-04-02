package no.uio.ifi.in2000.project.data.sunrise

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.gson.gson
import no.uio.ifi.in2000.project.model.sunrise.SunriseResponse
import java.text.SimpleDateFormat
import java.util.Date

data class SunriseDataSource(private val path: String  = "https://api.met.no/weatherapi/sunrise/3.0/") {

    private val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }

    suspend fun getSunrise(lat: Double, lon: Double): SunriseResponse? {
        val currentDate = getCurrentDate()

        return try {
            val httpResponse = client.get("https://api.met.no/weatherapi/sunrise/3.0/sun?/$lat&lon=$lon&date=$currentDate")
            httpResponse.body<SunriseResponse>()
        } catch (e: Exception) {
            null
        }
    }

    private fun getCurrentDate(): String {
        val currentDate = Date()
        val formatter = SimpleDateFormat("yyyy-mm-dd")
        return formatter.format(currentDate)
    }
}