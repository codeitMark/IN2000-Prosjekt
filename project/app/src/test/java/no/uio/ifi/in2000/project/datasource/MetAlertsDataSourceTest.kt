package no.uio.ifi.in2000.project.datasource

import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.project.data.alerts.MetAlertsDataSource
import no.uio.ifi.in2000.project.model.alerts.MetAlertsResponse
import org.junit.Test

class MetAlertsDataSourceTest {
    private val county = "03" // Oslo
    private val lang = "no"
    private val source = MetAlertsDataSource()
    @Test
    fun test_getAlertsConnection(){
        runBlocking {
            val alertsData = source.getAlerts(county, lang)
            println(alertsData)
            assert(source.connected)
        }
    }

    @Test
    fun test_getAlertsAccess(){
        runBlocking {
            val alertsData = source.getAlerts(county, lang)
            println(alertsData)
            assert(source.authorized)
        }
    }
}