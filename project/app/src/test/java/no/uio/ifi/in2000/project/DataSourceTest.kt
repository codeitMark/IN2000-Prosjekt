package no.uio.ifi.in2000.project

import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.project.data.DataSource
import org.junit.Test

class DataSourceTest {
    @Test
    fun test_fetchData(){ //sjekke API status og om det er tomt
        //runBlocking oppretter en coroutine for å kjøre funksjonene våre (spesielt for suspend funksjoner)
        runBlocking {
            val source = DataSource()
            val objects = source.getObject()
            //println(objects) //ikke lov å bruke println og runBLocking utenfor test.
            //println(objects.size) // antar at objects blir lagret i en liste.
            //assert(objects.isNotEmpty()) //kreves for å passe test. Hvis det ikke kommer noe ut er det sannsynlig det ikke funker, sjekk logcat sin respons.
        }
    }
}