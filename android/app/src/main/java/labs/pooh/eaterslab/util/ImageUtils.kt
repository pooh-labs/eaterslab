package labs.pooh.eaterslab.util

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

suspend fun downloadImageFrom(url: URL): Bitmap? = withContext(Dispatchers.IO) {
    val connection = url.openConnection() as HttpURLConnection
    return@withContext try {
        connection.doInput = true
        connection.connect()
        val inputStream = connection.inputStream
        BitmapFactory.decodeStream(inputStream)
    }
    catch (e: Exception) {
        null
    }
}

suspend fun downloadImageFrom(urlString: String): Bitmap? {
    return try {
        val url = URL(urlString)
        downloadImageFrom(url)
    }
    catch (e: Exception) {
        null
    }
}
