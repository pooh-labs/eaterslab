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
    val logoUrl: String,
    val address: String,
    val openedFrom: String,
    val openedTo: String,
    val id: Int?
) : ImageContentDao(logoUrl)

fun Cafeteria.toDao() =
    CafeteriaDao(name, description, subDescription, longitude.toDouble(), latitude.toDouble(),
        logoUrl, address, parseTime(openedFrom), parseTime(openedTo), id)

private fun parseTime(stringTime: String): String {
    return if (stringTime.count { it == ':' } > 1) {
        val valid = stringTime.indexOf(':')
        val index = stringTime.indexOf(':', startIndex = valid + 1)
        stringTime.substring(0, index)
    } else {
        stringTime
    }
}
