package labs.pooh.eaterslab.ui.activity.search

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_search.*
import labs.pooh.eaterslab.R
import labs.pooh.eaterslab.util.sendNotification

class SearchActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        rangeSeekbar4.setOnRangeSeekbarChangeListener{ min: Number, max: Number ->
            textMin.text = min.toString()
            textMax.text = max.toString()
        }

        rangeSeekbar3.setOnRangeSeekbarChangeListener{ min: Number, max: Number ->
            textMin2.text = min.toString()
            textMax2.text = max.toString()
        }

        searchButton.setOnClickListener{
            if(ratingBarMin.rating > ratingBarMax.rating)
                run { sendNotification(getString(R.string.search_error)) }

        }
    }
}
