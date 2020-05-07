package labs.pooh.eaterslab.ui.activity.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import labs.pooh.eaterslab.repository.CafeteriasRepository
import labs.pooh.eaterslab.repository.dao.CafeteriaBaseDao
import labs.pooh.eaterslab.ui.activity.abstracts.ConnectionStatusNotifier

class MapViewModel(notifier: ConnectionStatusNotifier) : ViewModel() {

    private val repository = CafeteriasRepository(notifier)

    private val _cafeteriasLiveData = MutableLiveData<List<CafeteriaBaseDao>>().apply {
        value = listOf()
    }

    val cafeteriaLiveData: LiveData<List<CafeteriaBaseDao>> = _cafeteriasLiveData

    fun updateMarkersData() {
        viewModelScope.launch {
            repository.cafeteriasList()?.let {
                _cafeteriasLiveData.value = it
            }
        }
    }
}
