package no.uio.ifi.in2000.byge.model.alerts

data class MetAlertsResponse(
    val features: List<Feature>,
    val lang: String,
    val lastChange: String,
    val type: String
    )