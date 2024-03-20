package no.uio.ifi.in2000.project.data.alerts

import no.uio.ifi.in2000.project.model.alerts.Features

class MetAlertsRepository {
    private val metAlertsSource = MetAlertsDataSource()

    suspend fun fetchAlerts(lat: Double, lon: Double, lang: String): Features {
        return metAlertsSource.getAlerts(lat, lon, lang)
    }
}