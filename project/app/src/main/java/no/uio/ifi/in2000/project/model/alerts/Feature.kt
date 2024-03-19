package no.uio.ifi.in2000.project.model.alerts

data class Feature(
    val geometry: Geometry,
    val properties: Properties,
    val type: Type,
    val time: When
)

