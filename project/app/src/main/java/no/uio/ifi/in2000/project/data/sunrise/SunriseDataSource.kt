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

    suspend fun getSunrise(lat: Double, lon: Double, timeZone: String): SunriseResponse? {
        val currentDate = getCurrentDate()

        return try {
            val httpResponse = client.get("weatherapi/sunrise/3.0/sun?lat=$lat&lon=$lon&date=$currentDate&offset=$timeZone")
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

}