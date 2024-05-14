package no.uio.ifi.in2000.project.datasource

import kotlinx.coroutines.runBlocking
import no.uio.ifi.in2000.project.data.search.SearchDataSource
import org.junit.Test

class SearchDataSourceTest {
    private val location = "Oslo, Norway"
    private val lang = "no"
    private val source = SearchDataSource()
    @Test
    fun test_getConnection(){
        runBlocking {
            val suggestionsData = source.getSuggestions(location, lang)
            println(suggestionsData)
            assert(source.connected)
        }
    }

    @Test
    fun test_getAlertsAccess(){
        runBlocking {
            val suggestionsData = source.getSuggestions(location, lang)
            println(suggestionsData)
            assert(source.authorized)
        }
    }
}