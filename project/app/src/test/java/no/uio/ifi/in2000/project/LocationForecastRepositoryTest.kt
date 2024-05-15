package no.uio.ifi.in2000.project

import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.project.data.forecast.LocationForecastDataSource
import no.uio.ifi.in2000.project.data.forecast.LocationForecastRepository
import org.junit.Test

class LocationForecastRepositoryTest {
    private val lat = 58.7753
    private val lon = 5.9056
    private val source = LocationForecastDataSource()
    private val rep = LocationForecastRepository()

    @Test
    fun fetchCorrectData(){
        runBlocking {
            val result = rep.fetchWeather(lat, lon)
            assert(result == source.getWeather(lat, lon))
        }
    }
}
