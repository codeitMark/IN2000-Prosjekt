package no.uio.ifi.in2000.byge.datasource

import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.byge.data.sunrise.SunriseDataSource
import org.junit.Test

class SunriseDataSourceTest {
    private val lat = 58.7753 //Gjesdal
    private val lon = 5.9056

    //37.7607, -122.4434 //failed. San Fransisco. Weird, works in postman.
    private val timeZone = "+01:00"
    private val source = SunriseDataSource()

    @Test
    fun test_getAlertsConnection() {
        runBlocking {
            val alertsData = source.getSunrise(lat, lon, timeZone)
            println(alertsData)
            assert(source.connected)
        }
    }

    @Test
    fun test_getAlertsAccess() {
        runBlocking {
            val alertsData = source.getSunrise(lat, lon, timeZone)
            println(alertsData)
            assert(source.authorized)
        }
    }
}