package labs.pooh.eaterslab.repository.dao

import labs.pooh.eaterslab.client.models.OccupancyStats

data class OccupancyStatsDao(
    var id: Int?,
    var timestamp: org.threeten.bp.OffsetDateTime,
    var occupancyRelative: Double
)

fun OccupancyStats.toDao() = OccupancyStatsDao(id, timestamp!!, occupancyRelative!!.toDouble())
