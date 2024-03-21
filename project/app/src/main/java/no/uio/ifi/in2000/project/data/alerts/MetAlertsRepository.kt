package no.uio.ifi.in2000.project.data.alerts

import no.uio.ifi.in2000.project.model.alerts.MetAlertsResponse

class MetAlertsRepository(private val metAlertsSource: MetAlertsDataSource) {

    suspend fun fetchAlerts(lat: Double, lon: Double, lang: String): MetAlertsResponse {
        return metAlertsSource.getAlerts(lat, lon, lang)
    }
}