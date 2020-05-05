package labs.pooh.eaterslab.repository.dao

import labs.pooh.client.models.Cafeteria

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
    val id: Int,
    val fixedMenuOptions: List<FixedMenuOptionDao>
)

fun Cafeteria.toDao()
        = CafeteriaDao(name, description, subDescription, longitude.toDouble(), latitude.toDouble(),
                    logoUrl, address, openedFrom, openedTo, id ?: -1,
    fixedMenuOptions?.map { it.toDao() } ?: listOf())



