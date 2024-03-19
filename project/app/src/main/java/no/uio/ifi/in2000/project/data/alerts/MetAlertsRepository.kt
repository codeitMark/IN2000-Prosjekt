package no.uio.ifi.in2000.project.data.alerts

class MetAlertsRepository {
    private val metAlertsSource = MetAlertsDataSource()

    suspend fun fetchAlerts() {
        return metAlertsSource.getAlerts()
    }

}