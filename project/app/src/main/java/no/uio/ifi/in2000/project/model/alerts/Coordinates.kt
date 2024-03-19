package no.uio.ifi.in2000.project.model.alerts

data class Coordinates(
    val coordinates: List<List<List<List<Int>>>>,
    val type: Type
)
