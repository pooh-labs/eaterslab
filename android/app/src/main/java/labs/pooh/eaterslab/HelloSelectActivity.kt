package labs.pooh.eaterslab

import android.os.Bundle
import android.view.View
import kotlinx.android.synthetic.main.activity_hello_select.*
import labs.pooh.eaterslab.ui.view.bounceAndScaleDelayed
import labs.pooh.eaterslab.util.start

class HelloSelectActivity : AbstractThemedActivity() {

    companion object {
        const val BUTTON_MAP_POSITION_X = "BUTTON_MAP_POSITION_X"
        const val BUTTON_MAP_POSITION_Y = "BUTTON_MAP_POSITION_Y"
        const val BUTTON_SEARCH_POSITION_X = "BUTTON_SEARCH_POSITION_X"
        const val BUTTON_SEARCH_POSITION_Y = "BUTTON_SEARCH_POSITION_Y"
    }

    override val showActionBar = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hello_select)

        fabSearch.setOnClickListener {
            markButtonsClickable(false)
            start<MainActivity> {
                getCenterPositionOf(fabSearch).let { (x, y) ->
                    putExtra(BUTTON_SEARCH_POSITION_X, x)
                    putExtra(BUTTON_SEARCH_POSITION_Y, y)
                }
            }
        }

        fabMap.setOnClickListener {
            markButtonsClickable(false)
            start<MapSearchActivity> {
                getCenterPositionOf(fabMap).let { (x, y) ->
                    putExtra(BUTTON_MAP_POSITION_X, x)
                    putExtra(BUTTON_MAP_POSITION_Y, y)
                }
            }
        }

        imageViewHeader.bounceAndScaleDelayed(this)
    }

    override fun onResume() {
        super.onResume()
        markButtonsClickable()
    }

    private fun markButtonsClickable(clickable: Boolean = true) {
        fabMap.isClickable = clickable
        fabSearch.isClickable = clickable
    }

    private fun getCenterPositionOf(view: View): Pair<Int, Int> {
        val x = view.x + view.width / 2
        val y = view.y + view.height / 2
        return Pair(x.toInt(), y.toInt())
    }
}
