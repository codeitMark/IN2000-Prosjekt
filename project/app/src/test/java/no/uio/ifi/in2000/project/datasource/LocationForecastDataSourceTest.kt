package no.uio.ifi.in2000.project.datasource

import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.project.data.forecast.LocationForecastDataSource
import org.junit.Test

class LocationForecastDataSourceTest {
    @Test
    fun test_getWeather(){
        runBlocking {
            val source = LocationForecastDataSource()
            val weatherData = source.getWeather()
            println(weatherData)
            assert(weatherData != null)
        }
    }
}