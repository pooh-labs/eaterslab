package labs.pooh.eaterslab.repository.dao

import labs.pooh.eaterslab.client.models.Cafeteria
import kotlin.math.min

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
    val occupancy: Double,
    val id: Int?
) : ImageContentDao(logoUrl) {
    val occupancyPercent: Int
    get() = (min(1.0, occupancy) * 100).toInt()
}

fun Cafeteria.toDao() =
    CafeteriaDao(name, description, subDescription, longitude.toDouble(), latitude.toDouble(),
        logoUrl, address, openedFrom, openedTo, occupancyRelative!!.toDouble(), id)
