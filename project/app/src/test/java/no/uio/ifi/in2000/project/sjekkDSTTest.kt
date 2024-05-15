package no.uio.ifi.in2000.project

import android.icu.util.TimeZone
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import no.uio.ifi.in2000.project.ui.home.HomeViewModel
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.mockito.Mockito.spy
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner


@RunWith(MockitoJUnitRunner::class)
class SjekkDST_Test {
    private val hvm = HomeViewModel()

    @Mock
    lateinit var mockZone: TimeZone

    @Test
    fun has_DST(){
        var tidoo = spy(mockZone)
        Mockito.`when`(TimeZone.getTimeZone("Europe/Oslo")).thenReturn(mockZone)

        //Locations are written in the format Continent/City. Limited selection of locations. Up to date available IDs will be returned in TimeZone.getAvailableIDs. Example of output: https://garygregory.wordpress.com/2013/06/18/what-are-the-java-timezone-ids/
        hvm.sjekkDST("Europe/Oslo")
        //verify(timezone).getTimeZone("Europe/Oslo")
        print(hvm.dst)
        assertTrue(hvm.dst)
    }

    @Test
    fun lacks_DST(){
        hvm.sjekkDST("Europe/Moscow")
        print(hvm.dst)
        assertFalse(hvm.dst)
    }
}