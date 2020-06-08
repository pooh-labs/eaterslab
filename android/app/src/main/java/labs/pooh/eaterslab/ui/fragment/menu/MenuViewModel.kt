package labs.pooh.eaterslab.ui.fragment.menu

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import labs.pooh.eaterslab.repository.CafeteriasRepository
import labs.pooh.eaterslab.repository.dao.CafeteriaDao
import labs.pooh.eaterslab.repository.dao.FixedMenuOptionDao
import labs.pooh.eaterslab.ui.activity.abstracts.ConnectionStatusNotifier
import labs.pooh.eaterslab.ui.activity.main.MainActivity


class MenuViewModel(connectionStatusNotifier: ConnectionStatusNotifier) : ViewModel() {

    private val repository = CafeteriasRepository(connectionStatusNotifier)

    private val _textVisible = MutableLiveData<Boolean>().apply {
        value = true
    }

    val textVisible: LiveData<Boolean> = _textVisible

    private val menuOptionLiveData = MutableLiveData<MutableList<FixedMenuOptionDao>>().apply {
        value = mutableListOf()
    }

    private val _lastLoadedMenuOptionLiveData = MutableLiveData<FixedMenuOptionDao>()

    val lastLoadedMenuOptionLiveData: LiveData<FixedMenuOptionDao> = _lastLoadedMenuOptionLiveData

    private fun addMenuOption(menuOption: FixedMenuOptionDao?) {
        if (menuOption == null) {
            return
        }
        _lastLoadedMenuOptionLiveData.value = menuOption
        menuOptionLiveData.value?.add(menuOption)
        menuOptionLiveData.value = menuOptionLiveData.value
    }

    fun updateMenuOptionsData() {
        viewModelScope.launch {
            val cafeteriaId = MainActivity.lastSelectedCafeteriaId
            val options = repository.getMenuOptionsOfCafeteria(cafeteriaId)

            options?.forEach {
                it.downloadContent()
                addMenuOption(it)
            }
        }
    }

    fun clearMenuOptionsData() {
        menuOptionLiveData.value = mutableListOf()
    }

    fun addReview(optionId: Int, reviewUsername: String, reviewText: String,
                  reviewStars: Int) {
        viewModelScope.launch {
            repository.addFixedMenuOptionReview(optionId, reviewStars, reviewUsername, reviewText)
        }
    }
}
