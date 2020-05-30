package labs.pooh.eaterslab.util

import android.content.Context
import labs.pooh.eaterslab.R
import org.threeten.bp.OffsetDateTime

fun Context.getOrderedWeekDays() = listOf(
    getString(R.string.monday), getString(R.string.tuesday), getString(R.string.wednesday),
    getString(R.string.thursday), getString(R.string.friday), getString(R.string.saturday),
    getString(R.string.sunday)
)

fun getOrderedMonthDays(month: Int, leapYear: Boolean = false) = when(month) {
    1, 3, 5, 7, 8, 10, 12 -> (1..31)
    2 -> (1..(if (leapYear) 29 else 28))
    else -> (1..30)
}.map(Int::toString)

fun getOpenOrderedHours(from: OffsetDateTime, to: OffsetDateTime) = (from.hour..to.hour).map { "$it:00" }

fun Context.weekDaysIndexer(day: String) = getOrderedWeekDays().indexOf(day)

fun monthDaysIndexer(day: String) = day.toInt() - 1

fun hoursIndexer(hour: String) = hour.takeWhile { it != ':' }.toInt()

fun workingHoursIndexer(from: OffsetDateTime): (String) -> Int = { (hoursIndexer(it) - from.hour) % 24 }
