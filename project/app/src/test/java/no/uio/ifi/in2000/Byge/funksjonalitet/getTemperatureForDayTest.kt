package no.uio.ifi.in2000.Byge.funksjonalitet

import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.Byge.data.forecast.LocationForecastDataSource
import no.uio.ifi.in2000.Byge.data.forecast.LocationForecastRepository
import org.junit.Test

//This test checks if the outputted data correlates with the data from the API.
class getTemperatureForDayTest {
    private val lat = 58.7753
    private val lon = 5.9056
    private val source = LocationForecastDataSource()
    private val reppy = LocationForecastRepository()
    //private val vm = HomeViewModel()
    
    @Test
    fun test_getTemperatureForDay(){
        runBlocking {
            val weatherData = source.getWeather(lat, lon)
            if (weatherData != null) {
                for (item in weatherData.properties.timeseries){
                    //same as the method below to verify?
                }
                println(weatherData.properties.timeseries[0].data.next_6_hours.details.air_temperature_min)
                println(weatherData.properties.timeseries[0].data.next_6_hours.details.air_temperature_max)

            }
            //vm
        }
    }
}

/*
    fun getTemperatureForDay(response: LocationForecastResponse, date: String): Pair<Double?, Double?>? {
        val timeseries = response.properties.timeseries

        var maxTemp: Double? = null
        var minTemp: Double? = null

        for (item in timeseries) {
            val dateTime = item.time.split("T")[0] // Hent bare dato-delen av tiden
            if (dateTime == date) {
                val next6Hours = item.data.next_6_hours
                if (next6Hours != null) { //next_6_hours can be null, ignore warning!
                    val tempMax = next6Hours.details.air_temperature_max.toDouble()
                    val tempMin = next6Hours.details.air_temperature_min.toDouble()

                    // Oppdater maksimumstemperaturen hvis den er høyere enn den nåværende maksimumstemperaturen
                    if (maxTemp == null || tempMax > maxTemp) {
                        maxTemp = tempMax
                    }

                    // Oppdater minimumstemperaturen hvis den er lavere enn den nåværende minimumstemperaturen
                    if (minTemp == null || tempMin < minTemp) {
                        minTemp = tempMin
                    }
                }
            }
        }
        return Pair(maxTemp, minTemp)
    }
}
 */
