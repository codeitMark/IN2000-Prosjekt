package no.uio.ifi.in2000.Byge.model.sunrise

data class Properties (
    val body: String,
    val sunrise: Sunrise,
    val sunset: Sunset,
    val solarnoon: SolarNoon,
    val solarmidnight: SolarMidnight
)