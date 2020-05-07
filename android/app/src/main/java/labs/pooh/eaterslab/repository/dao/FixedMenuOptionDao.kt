package labs.pooh.eaterslab.repository.dao

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import labs.pooh.client.models.FixedMenuOption
import labs.pooh.eaterslab.util.downloadImageFrom

data class FixedMenuOptionDao(
    val name: String,
    val price: Double,
    val photo: Bitmap?,
    val menuOptionTags: List<MenuOptionTagDao>,
    val fixedMenuOptionReviews: List<FixedMenuOptionReviewDao>
)

suspend fun FixedMenuOption.toDao() = withContext(Dispatchers.IO) {
    FixedMenuOptionDao(name, price.toDouble(), downloadImageFrom(photoUrl),
        menuOptionTags?.mapNotNull { it.toDao() } ?: listOf(),
        fixedMenuOptionReviews?.map { it.toDao() } ?: listOf()
    )
}