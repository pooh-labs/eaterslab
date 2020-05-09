package labs.pooh.eaterslab.repository.dao

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import labs.pooh.eaterslab.util.downloadImageFrom

abstract class ImageContentDao(private val photoUrl: String) : DownloadableContent<Bitmap?> {
    private var image: Bitmap? = null

    val downloadedImage: Bitmap?
    get() { return image }

    override suspend fun downloadContent(): Bitmap? = withContext(Dispatchers.IO) {
        if (image == null) {
            image = downloadImageFrom(photoUrl)
        }
        image
    }
}