package labs.pooh.eaterslab.repository.dao

import labs.pooh.eaterslab.client.models.AverageDishReviewStats
import org.threeten.bp.OffsetDateTime

data class AverageDishReviewStatsDao(
    var id: Int?,
    var timestamp: OffsetDateTime,
    var value: Double
)

fun AverageDishReviewStats.toDao() = AverageDishReviewStatsDao(id, timestamp!!, value!!.toDouble())