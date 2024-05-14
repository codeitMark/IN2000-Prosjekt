package no.uio.ifi.in2000.project.data.search

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.gson.gson
import no.uio.ifi.in2000.project.model.search.AutoCompleteResponse
import no.uio.ifi.in2000.project.model.search.ReverseGeocodingResponse
import java.net.URLEncoder

fun encode(url: String) = URLEncoder.encode(url, "UTF-8")


data class SearchDataSource(private val path: String = "https://api.geoapify.com") {
    var authorized = false
    var connected = false

    private val apiKey = "19c993f3dda5470f99c6cebeb819fa9f"
    private val client = HttpClient(CIO){
        install(ContentNegotiation){
            gson()
        }
        defaultRequest {
            url(path)
        }
    }
    // %C3%85lg%C3%A5rd
    suspend fun getSuggestions(text: String, lang: String): AutoCompleteResponse? {
        return try {
            val encoded = encode(text)
            val httpResponse = client.get("/v1/geocode/autocomplete?text=${encoded}&lang=$lang&apiKey=$apiKey")
            //Using lang variable for future implementation of language choice.
            connected = true
            if (httpResponse.status.value == 200){
                authorized = true
                httpResponse.body<AutoCompleteResponse>() //Samme som det kommentert over
            } else{
                null //Innholdet blir null uansett, bare at man får LocationForecastResponse objekt med parameterne null. Enklere å bare gjøre det til null tidlig nå, så slipper vi å lage en if-sjekk senere for om det det som er inni er null.
            }
        } catch (e: Exception){
            null
        }
    }

    suspend fun getUserLocation(lat: Double, lon: Double, lang: String): ReverseGeocodingResponse? {
        return try {
            val httpResponse = client.get("/v1/geocode/reverse?lat=$lat&lon=$lon&type=postcode&lang=$lang&limit=1&format=json&apiKey=$apiKey")
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