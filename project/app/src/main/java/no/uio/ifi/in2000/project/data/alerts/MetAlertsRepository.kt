package no.uio.ifi.in2000.project.data.alerts

import no.uio.ifi.in2000.project.model.alerts.MetAlertsResponse

class MetAlertsRepository() {
    private val metAlertsSource = MetAlertsDataSource()
    private val countyNumbers = mapOf(
        "Oslo" to "03",
        "Østfold" to "31",
        "Akershus" to "32",
        "Buskerud" to "33",
        "Innlandet" to "34",
        "Vestfold" to "39",
        "Telemark" to "40",
        "Agder" to "42",
        "Rogaland" to "11",
        "Vestland" to "46",
        "Møre og Romsdal" to "15",
        "Trøndelag" to "50",
        "Nordland" to "18",
        "Troms" to "55",
        "Finnmark" to "56",
        "Svalbard" to "21",
        "Jan Mayen" to "22"
    )

    suspend fun fetchAlerts(county: String, lang: String): MetAlertsResponse? {
        return metAlertsSource.getAlerts(countyNumbers[county]!!, lang)
    }
    suspend fun sortAlerts(alerts: MetAlertsResponse?): LinkedHashMap<String, String>{
        val map = LinkedHashMap<String, String>()
        alerts?.features?.forEach{
            map[it.properties.eventAwarenessName] = it.properties.instruction
        }
        return map
    }
}