package no.uio.ifi.in2000.Byge.datasource

import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.Byge.data.alerts.MetAlertsDataSource
import org.junit.Test

class MetAlertsDataSourceTest {
    private val lang = "no"
    private val lat = 79.4
    private val lon = 5.0
    private val source = MetAlertsDataSource()
    @Test
    fun test_getAlertsConnection(){
        runBlocking {
            val alertsData = source.getAlerts(lang, lat, lon)
            println(alertsData)
            assert(source.connected)
        }
    }

    @Test
    fun test_getAlertsAccess(){
        runBlocking {
            val alertsData = source.getAlerts(lang, lat, lon)
            println(alertsData)
            assert(source.authorized)
        }
    }
}