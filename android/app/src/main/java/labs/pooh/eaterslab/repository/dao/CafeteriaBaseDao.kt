package labs.pooh.eaterslab.repository.dao

import labs.pooh.client.models.CafeteriaBase

data class CafeteriaBaseDao (
    val name: String,
    val description: String,
    val subDescription: String,
    val longitude: Double,
    val latitude: Double,
    val openedFrom: String,
    val openedTo: String,
    val id: Int
)

fun CafeteriaBase.toDao() = CafeteriaBaseDao(name, description, subDescription, longitude.toDouble(), latitude.toDouble(), openedFrom, openedTo, id ?: -1)

fun CafeteriaDao.toBase() = CafeteriaBaseDao(name, description, subDescription, longitude, latitude, openedFrom, openedTo, id)