package labs.pooh.eaterslab.ui.activity.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import labs.pooh.eaterslab.repository.CafeteriasRepository
import labs.pooh.eaterslab.repository.dao.CafeteriaDao
import labs.pooh.eaterslab.ui.activity.abstracts.ConnectionStatusNotifier
import labs.pooh.eaterslab.ui.activity.abstracts.RepositoryAccessViewModel


class MapViewModel(notifier: ConnectionStatusNotifier) : RepositoryAccessViewModel(notifier) {

    private val _cafeteriasLiveData = MutableLiveData<List<CafeteriaDao>>().apply {
        value = listOf()
    }

    val cafeteriaLiveData: LiveData<List<CafeteriaDao>> = _cafeteriasLiveData

    fun updateMarkersData() {
        viewModelScope.launch {
            repository.cafeteriasList()?.let {
                _cafeteriasLiveData.value = it
            }
        }
    }
}
