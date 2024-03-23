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

    //consider another solution, enum data classes? no idea.
    private val alertIcons = mapOf(
        "Avalanches" to "icon-warning-avalanches",
        "BlowingSnow" to "icon-warning-snow",
        "DrivingConditions" to "icon-warning-drivingconditions",
        "Flood" to "icon-warning-flood",
        "ForestFire" to "icon-warning-forestfire",
        "Gale" to "icon-warning-wind",
        "Ice" to "icon-warning-ice",
        "Icing" to "icon-warning-generic",
        "Landslide" to "icon-warning-landslide",
        "PolarLow" to "icon-warning-polarlow",
        "Rain" to "icon-warning-rain",
        "RainFlood" to "icon-warning-rainflood",
        "Snow" to "icon-warning-snow",
        "StormSurge" to "icon-warning-stormsurge",
        "Lightning" to "icon-warning-lightning",
        "Wind" to "icon-warning-wind",
        "Unknown" to "icon-warning-generic"
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