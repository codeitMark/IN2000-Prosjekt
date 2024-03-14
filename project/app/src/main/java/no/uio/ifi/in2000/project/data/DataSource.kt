package no.uio.ifi.in2000.project.data

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.gson.gson

data class DataSource(private val path: String = "link") { //for json files
    private val client = HttpClient(){
        install(ContentNegotiation){
            gson()
        }
    }

    suspend fun getObject(){
        //val response = client.get(path)
        //Log.i("DataSource", "response ${response.status.value}") //Checks API response and if it's available (200 if successful)
        /* //example code of what to return from an API call
        val objects = response.body<>()
        return objects.objects
         */

        //metode 1: defaultRequest (inkluderer API-nøkkel!)
        val method2 = HttpClient(CIO){
            defaultRequest {
                url("https://gw-uio.intark.uh-it.no/in2000/")
                header("X-Gravitee-Api-Key", "2da3279c-ee4c-4d21-955e-d13822ff578c")
            }
        }
        val response = client.get("weatherapi/")
        Log.i("DataSource", "response ${response.status.value}")
    }

    /*
    Her er de to obligatoriske API-ene
    url til LocationForecast: https://api.met.no/weatherapi/locationforecast/2.0/complete?lat=60.10&lon=10
    url til MetAlerts: https://api.met.no/weatherapi/metalerts/2.0/current.json
     */
}