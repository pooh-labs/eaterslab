package labs.pooh.eaterslab.repository.dao

import android.graphics.Bitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import labs.pooh.eaterslab.client.models.Cafeteria
import labs.pooh.eaterslab.util.downloadImageFrom

data class CafeteriaDao (
    val name: String,
    val description: String,
    val subDescription: String,
    val longitude: Double,
    val latitude: Double,
    val logoUrl: String,
    val address: String,
    val openedFrom: String,
    val openedTo: String,
    val id: Int?
) : ImageContentDao(logoUrl)

fun Cafeteria.toDao() =
    CafeteriaDao(name, description, subDescription, longitude.toDouble(), latitude.toDouble(),
        logoUrl, address, openedFrom, openedTo, id)
