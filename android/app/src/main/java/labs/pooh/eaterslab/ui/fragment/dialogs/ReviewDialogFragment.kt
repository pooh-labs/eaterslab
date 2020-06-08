package labs.pooh.eaterslab.ui.fragment.dialogs

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RatingBar
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.dialog_review.*
import labs.pooh.eaterslab.R

typealias ReviewConsumer = (String, Int) -> Unit

class ReviewDialogFragment(private val reviewConsumer: ReviewConsumer) : DialogFragment() {

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(getString(R.string.your_review))
        val layout = layoutInflater.inflate(R.layout.dialog_review, null)
        builder.setView(layout)
        builder.setPositiveButton(getString(R.string.send_review)) { _, _ ->
            val text = layout.findViewById<EditText>(R.id.reviewText)
            val ratingBar = layout.findViewById<RatingBar>(R.id.ratingBarReview)
            reviewConsumer(text.editableText.toString(), ratingBar.rating.toInt())
        }
        return builder.create()
    }
}