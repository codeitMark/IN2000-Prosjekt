package no.uio.ifi.in2000.project.data.sunrise

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.gson.gson
import no.uio.ifi.in2000.project.model.sunrise.SunriseResponse
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

data class SunriseDataSource(private var path: String = "https://gw-uio.intark.uh-it.no/in2000/") {
    var authorized = false
    var connected = false

    private val client = HttpClient {
        install(ContentNegotiation) {
            gson()
        }
        defaultRequest {
            url(path)
            header("X-Gravitee-Api-Key", "2da3279c-ee4c-4d21-955e-d13822ff578c")
        }
    }

    suspend fun getSunrise(lat: Double, lon: Double): SunriseResponse? {
        val currentDate = getCurrentDate()
        val timeZone = getTimeZone(lon)

        return try {
            // kan fikse på tidssoner senere, satte den til norsk tid inntil videre
            val httpResponse = client.get("weatherapi/sunrise/3.0/sun?lat=$lat&lon=$lon&date=$currentDate&offset=+$timeZone")
            connected = true //test connection
            if (httpResponse.status.value == 200) {
                authorized = true //test authorized (something would be wrong with Api-key if not)
                httpResponse.body<SunriseResponse>()
            } else{
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun getCurrentDate(): String {
        //val currentDate = LocalDateTime.now().format(formatter) //Used method below due to this being API-level 26+. Our minimum is API-level 24.
        val currentDate = Date() //gets current date
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()) //formats date.
        return formatter.format(currentDate)
    }

    private fun getTimeZone(lon: Double): String {
        val timeZoneHours = ceil(lon / 15).toInt() //Longtitude delt på 15 gir antall timer forskjell fra GMT/UTC

        // Formater timer på riktig måte
        val formattedHours = String.format("%02d", timeZoneHours)
        return "$formattedHours:00" // Returnerer timer med minutter satt til 00
    }
}