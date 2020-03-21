package labs.pooh.eaterslab

import labs.pooh.eaterslab.util.downloadImageFrom
import org.junit.Test

import org.junit.Assert.*

class ImageUtilsUnitTest {

    @Test
    fun imageDownload_toBitmapNull() {
        val url = ""
        val image = downloadImageFrom(url)
        assertNull(image)
    }
}
