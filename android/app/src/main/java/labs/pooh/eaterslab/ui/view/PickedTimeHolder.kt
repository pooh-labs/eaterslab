package labs.pooh.eaterslab.ui.view

import android.content.Context
import labs.pooh.eaterslab.R
import labs.pooh.eaterslab.repository.TimeApi

class PickedTimeHolder(private val context: Context, private val useRepresentation: (String) -> Unit) {
    private var hour: Int? = null
    private var minute: Int? = null

    fun setTime(hour: Int, minute: Int) {
        this.hour = hour
        this.minute = minute
        useRepresentation(toString())
    }

    override fun toString(): String {
        val currMinute = minute
        val currHour = hour
        return if (currHour == null || currMinute == null) {
            context.getString(R.string.time_not_selected)
        } else if (currMinute < 10) {
            "$currHour:0$currMinute"
        } else {
            "$currHour:$currMinute"
        }
    }

    fun toTimeApiFormat(): TimeApi? {
        val currHour = hour
        val currMinute = minute
        return if (currHour == null || currMinute == null) {
            null
        } else {
            TimeApi(currHour, currMinute)
        }
    }
}
