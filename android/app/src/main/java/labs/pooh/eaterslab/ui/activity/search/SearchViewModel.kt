package labs.pooh.eaterslab.ui.activity.search

import androidx.lifecycle.MutableLiveData
import labs.pooh.eaterslab.ui.activity.abstracts.ConnectionStatusNotifier
import labs.pooh.eaterslab.ui.activity.abstracts.RepositoryAccessViewModel

class SearchViewModel(notifier: ConnectionStatusNotifier) : RepositoryAccessViewModel(notifier) {

    private val _openedLiveData = MutableLiveData<Boolean>().apply {
        this.value = false
    }

}