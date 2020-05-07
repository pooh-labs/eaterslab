package labs.pooh.eaterslab.repository.dao

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import labs.pooh.client.models.Cafeteria
import labs.pooh.eaterslab.util.downloadImageFrom

data class CafeteriaDao (
    val name: String,
    val description: String,
    val subDescription: String,
    val longitude: Double,
    val latitude: Double,
    val logo: Bitmap?,
    val address: String,
    val openedFrom: String,
    val openedTo: String,
    val id: Int,
    val fixedMenuOptions: List<FixedMenuOptionDao>
)

suspend fun Cafeteria.toDao() = withContext(Dispatchers.IO) {
    CafeteriaDao(name, description, subDescription, longitude.toDouble(), latitude.toDouble(),
        downloadImageFrom(logoUrl), address, openedFrom, openedTo, id ?: -1,
        fixedMenuOptions?.map { it.toDao() } ?: listOf())
}



