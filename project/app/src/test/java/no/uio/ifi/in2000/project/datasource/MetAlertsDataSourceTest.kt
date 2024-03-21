package no.uio.ifi.in2000.project.datasource

import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.project.data.alerts.MetAlertsDataSource
import no.uio.ifi.in2000.project.model.alerts.MetAlertsResponse
import org.junit.Test

class MetAlertsDataSourceTest {
    @Test
    fun test_getAlerts(){
        val lat = 58.7753
        val lon = 5.9056
        val lang = "no"
        val emptyResponseTest = MetAlertsResponse(emptyList(), "empty", "empty", "empty")
        runBlocking {
            val source = MetAlertsDataSource()
            val alertsData = source.getAlerts(lat, lon, lang)
            println(alertsData)
            assert(alertsData != emptyResponseTest)
        }
    }
}