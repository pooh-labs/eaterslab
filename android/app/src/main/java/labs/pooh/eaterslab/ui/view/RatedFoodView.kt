package labs.pooh.eaterslab.ui.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.cardview.widget.CardView
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.rated_food_view.view.*
import labs.pooh.eaterslab.R

class RatedFoodView(context: Context) : CardView(context) {

    private var numStars: Float = 0F
        set(value) {
            assert(value >= 0) { "Rating cannot be negative value" }
            field = value
            ratingBarStars.rating = numStars
        }

    private var price: Double = 0.0
        set(value) {
            assert(value >= 0.0) { "Price cannot be negative value" }
            field = value
            textViewPrice.text = String.format("%.2f zł", value)
        }

    private var text: String = ""
        set(value) {
            field = value
            textViewName.text = value
        }

    init {
        inflate(context, R.layout.rated_food_view, this)
    }

    constructor(context: Context, stars: Float, img: Bitmap, text: String, price: Double?, stepSize: Float = 1F) : this(context) {
        initConstructorFields(stars, img, text, price, stepSize)
    }

    constructor(context: Context, stars: Float, img: Drawable, text: String, price: Double?, stepSize: Float = 1F) : this(context) {
        initConstructorFields(stars, img, text, price, stepSize)
    }

    constructor(context: Context, stars: Float, resId: Int, text: String, price: Double?, stepSize: Float = 1F) : this(context) {
        initConstructorFields(stars, resId, text, price, stepSize)
    }

    private fun initConstructorFields(stars: Float, img: Any, text: String, price: Double?, stepSize: Float) {

        this.ratingBarStars.stepSize = stepSize
        this.numStars = stars
        this.text = text
        price?.let { this.price = price } ?: run { this.textViewPrice.isVisible = false }
        when (img) {
            is Bitmap -> setImageBitmap(img)
            is Drawable -> setImageDrawable(img)
            is Int -> setImageResource(img)
        }
    }

    private fun setImageDrawable(img: Drawable) = ratedImage.setImageDrawable(img)

    private fun setImageBitmap(img: Bitmap) = ratedImage.setImageBitmap(img)

    private fun setImageResource(resId: Int) = ratedImage.setImageResource(resId)
}
