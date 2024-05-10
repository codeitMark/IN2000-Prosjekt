package no.uio.ifi.in2000.project.data.search

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.serialization.gson.gson
import no.uio.ifi.in2000.project.model.search.AutoCompleteResponse
import no.uio.ifi.in2000.project.model.search.ReverseGeocodingResponse


data class SearchDataSource(private val path: String = "https://api.geoapify.com") {
    var authorized = false
    var connected = false
    val apiKey = "19c993f3dda5470f99c6cebeb819fa9f"
    private val client = HttpClient(CIO){
        install(ContentNegotiation){
            gson()
        }
        defaultRequest {
            url(path)
        }
    }

    suspend fun getSuggestions(text: String): AutoCompleteResponse? {
        return try {
            val httpResponse = client.get("/v1/geocode/autocomplete?text=$text&apiKey=$apiKey")
            connected = true
            if (httpResponse.status.value == 200){
                authorized = true
                //Log.d("TestSearch1000", "Api kall med teksten: $text") //Kommentert ut for at test ikke skal returnere null
                httpResponse.body<AutoCompleteResponse>() //Samme som det kommentert over
            } else{
                null //Innholdet blir null uansett, bare at man får LocationForecastResponse objekt med parameterne null. Enklere å bare gjøre det til null tidlig nå, så slipper vi å lage en if-sjekk senere for om det det som er inni er null.
            }
        } catch (e: Exception){
            null
        }
    }

    suspend fun getUserLocation(lat: Double, lon: Double): ReverseGeocodingResponse? {
        return try {
            val httpResponse = client.get("/v1/geocode/reverse?lat=$lat&lon=$lon&type=postcode&lang=en&limit=1&format=json&apiKey=$apiKey")
            connected = true
            if (httpResponse.status.value == 200) {
                authorized = true
                httpResponse.body<ReverseGeocodingResponse>()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}