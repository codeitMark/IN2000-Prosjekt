package no.uio.ifi.in2000.project.data

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.gson.gson

data class DataSource(private val path: String = "link") { //for json files
    private val client = HttpClient(){
        install(ContentNegotiation){
            gson()
        }
    }

    suspend fun getObject(){
        val response = client.get(path)
        Log.i("DataSource", "response ${response.status.value}") //Checks API response and if it's available (200 if successful)
        /* //example code of what to return from an API call
        val objects = response.body<>()
        return objects.objects
         */
    }
}