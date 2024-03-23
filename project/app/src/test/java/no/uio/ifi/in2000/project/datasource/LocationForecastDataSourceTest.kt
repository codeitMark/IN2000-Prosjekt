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
//Husk å lage flere tester: En for å teste internett. En for å teste om det ikke kommer noe! (responskode 401 (unauthorized) eller noe lignende.
class LocationForecastDataSourceTest {
    private val lat = 58.7753
    private val lon = 5.9056
    private val source = LocationForecastDataSource()
    @Test
    fun test_getWeatherConnection(){
        runBlocking {
            val weatherData = source.getWeather(lat, lon)
            println(weatherData)
            assert(source.connected)
        }
    }

    @Test
    fun test_getWeatherAccess(){
        runBlocking {
            val weatherData = source.getWeather(lat, lon)
            println(weatherData)
            assert(source.authorized)
        }
    }
}