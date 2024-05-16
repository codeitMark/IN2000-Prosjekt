package no.uio.ifi.in2000.byge.data.alerts

import no.uio.ifi.in2000.byge.model.alerts.MetAlertsResponse

class MetAlertsRepository {
    private val metAlertsSource = MetAlertsDataSource()

    //consider another solution, enum data classes? no idea.
    val alertIcons = mapOf(
        "avalanches" to "icon_warning_avalanches",
        "blowingSnow" to "icon_warning_snow",
        "drivingConditions" to "icon_warning_drivingconditions",
        "flood" to "icon_warning_flood",
        "forestFire" to "icon_warning_forestfire",
        "gale" to "icon_warning_wind",
        "ice" to "icon_warning_ice",
        "icing" to "icon_warning_generic",
        "landslide" to "icon_warning_landslide",
        "polarLow" to "icon_warning_polarlow",
        "rain" to "icon_warning_rain",
        "rainFlood" to "icon_warning_rainflood",
        "snow" to "icon_warning_snow",
        "stormSurge" to "icon_warning_stormsurge",
        "lightning" to "icon_warning_lightning",
        "wind" to "icon_warning_wind",
        "unknown" to "icon_warning_generic"
    )

    suspend fun fetchAlerts(lang: String, lat: Double, lon: Double): MetAlertsResponse? {
        return metAlertsSource.getAlerts(lang, lat, lon)
    }

    fun sortAlerts(alerts: MetAlertsResponse?): LinkedHashMap<String, String> { //Problemet med dette er at man mister varsler om det er samme varsel med ulik nivå (farge).
        val map = LinkedHashMap<String, String>()
        alerts?.features?.forEach {
            map[it.properties.eventAwarenessName] = it.properties.instruction
        }
        return map
    }

    fun fetchAlertIcons(alerts: MetAlertsResponse?): MutableList<String>? {
        val icons = mutableListOf<String>()
        return if (alerts?.features?.isNotEmpty()!!) { //If no alerts. No alerts =/= alerts.features is null.
            alerts.features.forEach {
                icons.add("https://raw.githubusercontent.com/nrkno/yr-warning-icons/master/design/svg/${alertIcons[it.properties.event]}-${it.properties.riskMatrixColor.lowercase()}.svg")
            }
            icons
        } else { //In case of no alerts
            null
        }
    }
}