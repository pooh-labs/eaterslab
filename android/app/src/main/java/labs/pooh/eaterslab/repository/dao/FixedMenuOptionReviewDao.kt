package labs.pooh.eaterslab.repository.dao

import labs.pooh.client.models.FixedMenuOptionReview

data class FixedMenuOptionReviewDao(
    val stars: Int,
    val authorNick: String,
    val reviewTime: org.threeten.bp.OffsetDateTime,
    val optionId: Int
)

fun FixedMenuOptionReview.toDao() = FixedMenuOptionReviewDao(stars, authorNick, reviewTime, option)