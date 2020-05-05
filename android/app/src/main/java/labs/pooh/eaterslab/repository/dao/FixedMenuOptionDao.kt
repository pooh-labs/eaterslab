package labs.pooh.eaterslab.repository.dao

import labs.pooh.client.models.FixedMenuOption

data class FixedMenuOptionDao(
    val name: String,
    val price: Double,
    val photoUrl: String,
    val menuOptionTags: List<MenuOptionTagDao>,
    val fixedMenuOptionReviews: List<FixedMenuOptionReviewDao>
)

fun FixedMenuOption.toDao() = FixedMenuOptionDao(name, price.toDouble(), photoUrl,
    menuOptionTags?.map { it.toDao() }?.filterNotNull() ?: listOf(),
    fixedMenuOptionReviews?.map { it.toDao() } ?: listOf()
)