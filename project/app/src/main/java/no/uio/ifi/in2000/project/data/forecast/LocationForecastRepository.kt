package no.uio.ifi.in2000.project.data.forecast

import android.util.Log
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

class LocationForecastRepository(private val locationForecastSource: LocationForecastDataSource) {

    suspend fun fetchWeather(lat: Double, lon: Double): LocationForecastResponse? {
        return try {
            locationForecastSource.getWeather(lat, lon)
        } catch (e: Exception) {
            Log.e("LocationForecastRepository", "Error fetching weather", e)
            //Empty response if error fetching weather
            emptyResponse
        }
    }

    private val emptyResponse = LocationForecastResponse("empty", Geometry("empty", emptyList()), Properties(
        Meta("empty", Units("empty", "empty", "empty", "empty", "empty", "empty", "empty")),
        listOf(
            TimeSeries("empty", Data(
                Instant(Instant_Details(0.toFloat(), 0.toFloat(), 0.toFloat(), 0.toFloat(), 0.toFloat(), 0.toFloat())), NextHours(
            Summary("empty"), NextHours_Details(0.toFloat())
        ), NextHours(Summary("empty"), NextHours_Details(0.toFloat())), NextHours(Summary("empty"), NextHours_Details(0.toFloat()))
            ))
        )
    ))
}