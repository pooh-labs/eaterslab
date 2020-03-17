package labs.pooh.mycanteen

import labs.pooh.mycanteen.util.downloadImageFrom
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
