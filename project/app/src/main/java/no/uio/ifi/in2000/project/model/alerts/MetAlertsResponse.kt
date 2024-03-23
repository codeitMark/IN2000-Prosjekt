package no.uio.ifi.in2000.project.model.alerts

data class MetAlertsResponse(
    val features: List<Feature>,
    val lang: String,
    val lastChange: String,
    val type: String
    )