package no.uio.ifi.in2000.project.data.sunrise

class SunriseRepository() {
    private val sunriseDataSource = SunriseDataSource()

    suspend fun fetchSunrise(lat: Double, lon: Double) {
        sunriseDataSource.getSunrise(lat, lon)
    }
}