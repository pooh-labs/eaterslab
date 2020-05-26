package labs.pooh.eaterslab.ui.activity.search

import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.coroutines.cancelChildren
import labs.pooh.eaterslab.R
import labs.pooh.eaterslab.repository.dao.CafeteriaDao
import labs.pooh.eaterslab.ui.activity.abstracts.AbstractRevealedActivity
import labs.pooh.eaterslab.ui.activity.abstracts.viewModelFactory
import labs.pooh.eaterslab.ui.activity.hello.HelloSelectActivity
import labs.pooh.eaterslab.ui.fragment.dialogs.TimePickerDialogFragment
import labs.pooh.eaterslab.ui.view.PickedTimeHolder
import labs.pooh.eaterslab.ui.view.SearchedCafeteriaView



class SearchActivity : AbstractRevealedActivity() {

    private val pickedTimeFrom by lazy { PickedTimeHolder(this) { textViewOpenedFromValue.text = it } }
    private val pickedTimeTo by lazy { PickedTimeHolder(this) { textViewOpenedToValue.text = it } }

    private val searchViewModel by lazy { ViewModelProvider(this, viewModelFactory {
        SearchViewModel(this) }).get(SearchViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        searchViewModel.lastDownloadedCafeteriaLiveData.observe(this, Observer {
            addNewSearchResult(it)
        })

        buttonSearch.setOnClickListener {
            clearSearched()
            searchViewModel.viewModelScope.coroutineContext.cancelChildren()
            val textSearch = searchText.text.toString()
            val onlyOpened = switchOpened.isChecked
            searchViewModel.getFilteredData(textSearch, onlyOpened, pickedTimeFrom.toTimeApiFormat(), pickedTimeTo.toTimeApiFormat())
        }

        textViewOpenedFromValue.setOnClickListener {
            TimePickerDialogFragment(pickedTimeFrom).show(supportFragmentManager, "timePickerFrom")
        }

        textViewOpenedToValue.setOnClickListener {
            TimePickerDialogFragment(pickedTimeTo).show(supportFragmentManager, "timePickerTo")
        }
    }

    private fun clearSearched() {
        cafeteriasList.removeAllViews()
    }

    private fun addNewSearchResult(cafeteriaDao: CafeteriaDao) {
        val cafeteriaView = with(cafeteriaDao.downloadedImage) {
            if (this != null) {
                SearchedCafeteriaView(this@SearchActivity, cafeteriaDao.name, cafeteriaDao.openedFrom, cafeteriaDao.openedTo, this)
            }
            else {
                SearchedCafeteriaView(this@SearchActivity, cafeteriaDao.name, cafeteriaDao.openedFrom, cafeteriaDao.openedTo, R.drawable.ic_location)
            }
        }
        cafeteriasList.addView(cafeteriaView)
    }

    override val revealedLayout = lazy { rootTextSearchLayout }

    override val showActionBar = false

    override fun getXStartPosition() = intent.getIntExtra(HelloSelectActivity.BUTTON_SEARCH_POSITION_X, 0)

    override fun getYStartPosition() = intent.getIntExtra(HelloSelectActivity.BUTTON_SEARCH_POSITION_Y, 0)
}
