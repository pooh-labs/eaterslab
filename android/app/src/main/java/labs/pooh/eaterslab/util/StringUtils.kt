package labs.pooh.eaterslab.util

import android.content.Context
import labs.pooh.eaterslab.R
import org.threeten.bp.OffsetDateTime

fun Context.getOrderedWeekDays() = listOf(
    getString(R.string.monday), getString(R.string.tuesday), getString(R.string.wednesday),
    getString(R.string.thursday), getString(R.string.friday), getString(R.string.saturday),
    getString(R.string.sunday)
)
