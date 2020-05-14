package labs.pooh.eaterslab.ui.activity.search

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_search.*
import labs.pooh.eaterslab.R
import labs.pooh.eaterslab.ui.activity.abstracts.AbstractRevealedActivity
import labs.pooh.eaterslab.ui.activity.hello.HelloSelectActivity

class SearchActivity : AbstractRevealedActivity() {

    override fun getXStartPosition() = intent.getIntExtra(HelloSelectActivity.BUTTON_SEARCH_POSITION_X, 0)

    override fun getYStartPosition() = intent.getIntExtra(HelloSelectActivity.BUTTON_SEARCH_POSITION_Y, 0)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
    }

    override val revealedLayout = lazy { rootTextSearchLayout }

    override val showActionBar = false
}
