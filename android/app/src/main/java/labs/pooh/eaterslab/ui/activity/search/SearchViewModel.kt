package labs.pooh.eaterslab.ui.activity.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import labs.pooh.eaterslab.repository.BooleanApi
import labs.pooh.eaterslab.repository.TimeApi
import labs.pooh.eaterslab.repository.dao.CafeteriaDao
import labs.pooh.eaterslab.ui.activity.abstracts.ConnectionStatusNotifier
import labs.pooh.eaterslab.ui.activity.abstracts.RepositoryAccessViewModel

typealias CafeteriaFilter = (CafeteriaDao) -> Boolean

class SearchViewModel(notifier: ConnectionStatusNotifier) : RepositoryAccessViewModel(notifier) {

    private val _cafeteriasLiveData = MutableLiveData<MutableList<CafeteriaDao>>().apply {
        value = mutableListOf()
    }

    private val _lastDownloadedCafeteriaLiveData = MutableLiveData<CafeteriaDao>()

    val lastDownloadedCafeteriaLiveData: LiveData<CafeteriaDao> = _lastDownloadedCafeteriaLiveData

    private fun addSearchedCafeteria(cafeteriaDao: CafeteriaDao?) {
        if (cafeteriaDao == null) {
            return
        }

        _lastDownloadedCafeteriaLiveData.value = cafeteriaDao
        _cafeteriasLiveData.value?.add(cafeteriaDao)
        _cafeteriasLiveData.value = _cafeteriasLiveData.value
    }

    fun getFilteredData(prefixFilter: String, onlyOpenedFilter: Boolean, timeFrom: TimeApi?, timeTo: TimeApi?) {
        viewModelScope.launch {
            val cafeterias = repository.cafeteriasListFiltered(
                openedNow = BooleanApi.packedForTrue(onlyOpenedFilter),
                prefixName = prefixFilter,
                openedFrom = timeFrom,
                openedTo = timeTo
            )
            cafeterias
                ?.forEach { cafeteria ->
                    cafeteria.downloadContent()
                    addSearchedCafeteria(cafeteria)
            }
        }
    }
}