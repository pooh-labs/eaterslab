package labs.pooh.eaterslab.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.cardview.widget.CardView
import kotlinx.android.synthetic.main.searched_cafeteria_view.view.*
import labs.pooh.eaterslab.R

class SearchedCafeteriaView(context: Context) : CardView(context) {

    private var cafeteriaTo: String = ""
        set(value) {
            field = value
            setTimesFormatted()
        }

    private var cafeteriaFrom: String = ""
        set(value) {
            field = value
            setTimesFormatted()
        }

    private var cafeteriaName: String = ""
        set(value) {
            field = value
            setTimesFormatted()
        }

    private fun setTimesFormatted() {
        textViewTime.text = resources.getString(R.string.time_from_to_format, this.cafeteriaFrom, this.cafeteriaTo)
    }

    init {
        inflate(context, R.layout.searched_cafeteria_view, this)
    }

    constructor(context: Context, cafeteriaName:String, cafeteriaFrom: String, cafeteriaTo: String, img: Bitmap) : this(context) {
        initConstructorFields(cafeteriaName, cafeteriaFrom, cafeteriaTo, img)
    }

    constructor(context: Context, cafeteriaName:String, cafeteriaFrom: String, cafeteriaTo: String, img: Drawable) : this(context) {
        initConstructorFields(cafeteriaName, cafeteriaFrom, cafeteriaTo, img)
    }

    constructor(context: Context, cafeteriaName:String, cafeteriaFrom: String, cafeteriaTo: String, resId: Int) : this(context) {
        initConstructorFields(cafeteriaName, cafeteriaFrom, cafeteriaTo, resId)
    }

    private fun initConstructorFields(cafeteriaName:String, cafeteriaFrom: String, cafeteriaTo: String, img: Any) {

        this.cafeteriaFrom = cafeteriaFrom
        this.cafeteriaTo = cafeteriaTo
        this.cafeteriaName = cafeteriaName

        when (img) {
            is Bitmap -> setImageBitmap(img)
            is Drawable -> setImageDrawable(img)
            is Int -> setImageResource(img)
        }
    }

    private fun setImageDrawable(img: Drawable) = cafeteriaImage.setImageDrawable(img)

    private fun setImageBitmap(img: Bitmap) = cafeteriaImage.setImageBitmap(img)

    private fun setImageResource(resId: Int) = cafeteriaImage.setImageResource(resId)
}
