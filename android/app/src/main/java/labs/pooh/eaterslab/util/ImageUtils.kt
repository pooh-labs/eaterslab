package labs.pooh.eaterslab.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

fun downloadImageFrom(url: URL): Bitmap? {
    return try {
        val connection = url.openConnection() as HttpURLConnection
        connection.doInput = true
        connection.connect()
        val inputStream = connection.inputStream
        BitmapFactory.decodeStream(inputStream)
    }
    catch (e: Exception) {
        null
    }
}

fun downloadImageFrom(urlString: String): Bitmap? {
    return try {
        val url = URL(urlString)
        downloadImageFrom(url)
    }
    catch (e: Exception) {
        null
    }
}
