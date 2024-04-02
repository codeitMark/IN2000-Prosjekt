package no.uio.ifi.in2000.project.data.sunrise

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.gson.gson
import no.uio.ifi.in2000.project.model.sunrise.SunriseResponse
import java.text.SimpleDateFormat
import java.util.Date

data class SunriseDataSource(private var path: String = "") {

    private val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
    }

    suspend fun getSunrise(lat: Double, lon: Double): SunriseResponse? {
        val currentDate = getCurrentDate()

        return try {
            // kan fikse på tidssoner senere, satte den til norsk tid inntil videre
            val httpResponse = client.get("https://api.met.no/weatherapi/sunrise/3.0/sun?lat=$lat&lon=$lon&date=$currentDate&offset=+02:00")
            httpResponse.body<SunriseResponse>()
        } catch (e: Exception) {
            null
        }
    }

    private fun getCurrentDate(): String {
        val currentDate = Date()
        val formatter = SimpleDateFormat("yyyy-MM-dd")
        return formatter.format(currentDate)
    }
}