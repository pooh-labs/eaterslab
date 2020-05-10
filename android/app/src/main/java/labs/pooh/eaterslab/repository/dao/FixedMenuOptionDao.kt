package labs.pooh.eaterslab.repository.dao

import labs.pooh.client.models.FixedMenuOption

data class FixedMenuOptionDao(
    val name: String,
    val price: Double,
    val photoUrl: String,
    val avgReviewStars: Double,
    val id: Int?
) : ImageContentDao(photoUrl)

fun FixedMenuOption.toDao() =
    FixedMenuOptionDao(name, price.toDouble(), photoUrl, avgReviewStars?.toDouble() ?: 0.0, id)
