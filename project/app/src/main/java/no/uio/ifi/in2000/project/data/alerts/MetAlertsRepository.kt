package no.uio.ifi.in2000.project.data.alerts

import no.uio.ifi.in2000.project.model.alerts.MetAlertsResponse

class MetAlertsRepository() {
    private val metAlertsSource = MetAlertsDataSource()

    suspend fun fetchAlerts(county: String, lang: String): MetAlertsResponse? {
        return metAlertsSource.getAlerts(county, lang)
    }
    suspend fun sortAlerts(alerts: MetAlertsResponse?): LinkedHashMap<String, String>{
        val map = LinkedHashMap<String, String>()
        alerts?.features?.forEach{
            map[it.properties.eventAwarenessName] = it.properties.instruction
        }
        return map
    }
}