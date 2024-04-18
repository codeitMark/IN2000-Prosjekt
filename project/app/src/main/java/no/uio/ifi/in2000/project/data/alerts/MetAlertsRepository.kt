package no.uio.ifi.in2000.project.data.alerts

import no.uio.ifi.in2000.project.model.alerts.MetAlertsResponse
import no.uio.ifi.in2000.project.model.forecast.LocationForecastResponse

class MetAlertsRepository() {
    private val metAlertsSource = MetAlertsDataSource()

    //consider another solution, enum data classes? no idea.
    private val alertIcons = mapOf(
        "avalanches" to "icon-warning-avalanches",
        "blowingSnow" to "icon-warning-snow",
        "drivingConditions" to "icon-warning-drivingconditions",
        "flood" to "icon-warning-flood",
        "forestFire" to "icon-warning-forestfire",
        "gale" to "icon-warning-wind",
        "ice" to "icon-warning-ice",
        "icing" to "icon-warning-generic",
        "landslide" to "icon-warning-landslide",
        "polarLow" to "icon-warning-polarlow",
        "rain" to "icon-warning-rain",
        "rainFlood" to "icon-warning-rainflood",
        "snow" to "icon-warning-snow",
        "stormSurge" to "icon-warning-stormsurge",
        "lightning" to "icon-warning-lightning",
        "wind" to "icon-warning-wind",
        "unknown" to "icon-warning-generic"
    )

    suspend fun fetchAlerts(lang: String, lat: Double, lon: Double): MetAlertsResponse? {
        return metAlertsSource.getAlerts(lang, lat, lon)
    }
    suspend fun sortAlerts(alerts: MetAlertsResponse?): LinkedHashMap<String, String>{ //Problemet med dette er at man mister varsler om det er samme varsel med ulik nivå (farge).
        val map = LinkedHashMap<String, String>()
        alerts?.features?.forEach{
            map[it.properties.eventAwarenessName] = it.properties.instruction
        }
        return map
    }

    suspend fun fetchAlertIcons(alerts: MetAlertsResponse?): MutableList<String>? {
        val icons = mutableListOf<String>()
        return if (alerts?.features?.isNotEmpty()!!) { //If no alerts. No alerts =/= alerts.features is null.
            alerts.features.forEach{
                icons.add("https://raw.githubusercontent.com/nrkno/yr-warning-icons/master/design/svg/${alertIcons[it.properties.event]}-${it.properties.riskMatrixColor.lowercase()}.svg")
            }
            icons
        }else{ //In case of no alerts
            null
        }
    }
}