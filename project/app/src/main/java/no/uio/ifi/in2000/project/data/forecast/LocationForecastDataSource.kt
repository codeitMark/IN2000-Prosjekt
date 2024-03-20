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
import no.uio.ifi.in2000.project.model.forecast.Data
import no.uio.ifi.in2000.project.model.forecast.Geometry
import no.uio.ifi.in2000.project.model.forecast.Instant
import no.uio.ifi.in2000.project.model.forecast.Instant_Details
import no.uio.ifi.in2000.project.model.forecast.LocationForecastResponse
import no.uio.ifi.in2000.project.model.forecast.Meta
import no.uio.ifi.in2000.project.model.forecast.NextHours
import no.uio.ifi.in2000.project.model.forecast.NextHours_Details
import no.uio.ifi.in2000.project.model.forecast.Properties
import no.uio.ifi.in2000.project.model.forecast.Summary
import no.uio.ifi.in2000.project.model.forecast.TimeSeries
import no.uio.ifi.in2000.project.model.forecast.Units


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

    private val emptyResponse = LocationForecastResponse("empty", Geometry("empty", emptyList()), Properties(
        Meta("empty", Units("empty", "empty", "empty", "empty", "empty", "empty", "empty")),
        listOf(
            TimeSeries("empty", Data(
                Instant(Instant_Details(0.toFloat(), 0.toFloat(), 0.toFloat(), 0.toFloat(), 0.toFloat(), 0.toFloat())), NextHours(
            Summary("empty"), NextHours_Details(0.toFloat())
        ), NextHours(Summary("empty"), NextHours_Details(0.toFloat())), NextHours(Summary("empty"), NextHours_Details(0.toFloat()))
            )
            )
        )
    )
    )

    suspend fun getWeather(lat: Double, lon: Double): LocationForecastResponse {
        return try {
            val httpResponse = client.get("weatherapi/locationforecast/2.0/compact?lat=$lat&lon=$lon")
            Log.i("LocationForecastDataSource", "response ${httpResponse.status.value}")
            //val response = httpResponse.body<LocationForecastResponse>()
            //return response
            httpResponse.body<LocationForecastResponse>() //Samme som det kommentert over
        } catch (e: Exception){
            Log.w("LocationForecastDataSource", "Error getting weather data! No internet connection?") //i = info, w = warning, e = error
            Log.e("LocationForecastDataSource", "$e")
            emptyResponse
        }
    }
}