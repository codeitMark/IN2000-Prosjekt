package no.uio.ifi.in2000.project.datasource

import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.project.data.forecast.LocationForecastDataSource
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
import org.junit.Test

//NOTAT: Husk å kommentere vekk logcat! Du kan ikke teste med jUnit4 med Logcat i koden fordi det ikke er en native java metode.
class LocationForecastDataSourceTest {
    @Test
    fun test_getWeather(){
        val lat = 58.7753
        val lon = 5.9056
        val emptyResponseTest = LocationForecastResponse("empty", Geometry("empty", emptyList()), Properties(
            Meta("empty", Units("empty", "empty", "empty", "empty", "empty", "empty", "empty")),
            listOf(
                TimeSeries("empty", Data(
                    Instant(Instant_Details(0.toFloat(), 0.toFloat(), 0.toFloat(), 0.toFloat(), 0.toFloat(), 0.toFloat())), NextHours(
                        Summary("empty"), NextHours_Details(0.toFloat())
                    ), NextHours(Summary("empty"), NextHours_Details(0.toFloat())), NextHours(
                        Summary("empty"), NextHours_Details(0.toFloat())
                    )
                )
                )
            )
        )
        )
        runBlocking {
            val source = LocationForecastDataSource()
            val weatherData = source.getWeather(lat, lon)
            println(weatherData)
            assert(weatherData != emptyResponseTest)
        }
    }
}