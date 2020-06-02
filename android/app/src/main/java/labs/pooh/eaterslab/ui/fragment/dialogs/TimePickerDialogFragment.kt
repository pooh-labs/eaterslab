package labs.pooh.eaterslab.ui.fragment.dialogs

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import labs.pooh.eaterslab.ui.view.PickedTimeHolder
import java.util.*

class TimePickerDialogFragment(private val holder: PickedTimeHolder) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val timeNow = Calendar.getInstance()
        return TimePickerDialog(activity, { _, hour, minute ->
            holder.setTime(hour, minute)
        }, timeNow.get(Calendar.HOUR_OF_DAY), timeNow.get(Calendar.MINUTE), true)
    }
}
