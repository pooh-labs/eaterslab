package labs.pooh.eaterslab.repository.dao

import labs.pooh.eaterslab.client.models.OccupancyStats
import kotlin.math.min

data class OccupancyStatsDao(
    var id: Int?,
    var timestamp: org.threeten.bp.OffsetDateTime,
    var occupancyRelative: Double
) {
    val occupancyPercent: Int
    get() = (min(1.0, occupancyRelative) * 100.0).toInt()
}

fun OccupancyStats.toDao() = OccupancyStatsDao(id, timestamp!!, occupancyRelative!!.toDouble())
