package labs.pooh.eaterslab.ui.fragment.home

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import labs.pooh.eaterslab.repository.dao.CafeteriaDao
import labs.pooh.eaterslab.ui.activity.abstracts.ConnectionStatusNotifier
import labs.pooh.eaterslab.ui.activity.abstracts.RepositoryAccessViewModel
import labs.pooh.eaterslab.ui.activity.main.MainActivity

class CafeteriaViewModel(notifier: ConnectionStatusNotifier) : RepositoryAccessViewModel(notifier) {

    companion object {
        const val OCCUPANCY_DATA_UPDATE_MILLIS = 1_000
    }

    private val _cafeteriaName = MutableLiveData<String>()
    val cafeteriaName: LiveData<String> = _cafeteriaName

    private val _cafeteriaOccupancy = MutableLiveData<Int>()
    val cafeteriaOccupancy: LiveData<Int> = _cafeteriaOccupancy

    private val _cafeteriaDescription = MutableLiveData<String>()
    val cafeteriaDescription: LiveData<String> = _cafeteriaDescription

    private val _cafeteriaSubDescription = MutableLiveData<String>()
    val cafeteriaSubDescription: LiveData<String> = _cafeteriaSubDescription

    private val _cafeteriaOpenFrom = MutableLiveData<String>()
    val cafeteriaOpenFrom: LiveData<String> = _cafeteriaOpenFrom

    private val _cafeteriaOpenTo = MutableLiveData<String>()
    val cafeteriaOpenTo: LiveData<String> = _cafeteriaOpenTo

    private val _cafeteriaAddress = MutableLiveData<String>()
    val cafeteriaAddress: LiveData<String> = _cafeteriaAddress

    private val _cafeteriaLogo = MutableLiveData<Bitmap?>()
    val cafeteriaLogo: LiveData<Bitmap?> = _cafeteriaLogo

    private var latestCafeteriaDao: CafeteriaDao? = null

    fun updateCafeteriaTextInfo() = viewModelScope.launch {
        updateTextCafeteriaData()
    }

    fun updateCafeteriaFullData() = viewModelScope.launch {
        updateTextCafeteriaData()
        latestCafeteriaDao?.run {
            downloadContent()
            _cafeteriaLogo.value = downloadedImage
        }
    }

    fun runPeriodicDataUpdate() = viewModelScope.launch {
        while (this.isActive) {
            val cafeteria = MainActivity.lastSelectedCafeteriaId
            val updatedData = repository.cafeteriasRead(cafeteria)
            updatedData?.occupancy?.let {
                _cafeteriaOccupancy.value = scaleOccupancy(it)
            }
            delay(OCCUPANCY_DATA_UPDATE_MILLIS.toLong())
        }
    }

    private suspend fun updateTextCafeteriaData() {
        val cafeteriaId = MainActivity.lastSelectedCafeteriaId
        val cafeteria = repository.cafeteriasRead(cafeteriaId)
        latestCafeteriaDao = cafeteria
        latestCafeteriaDao?.run {
            _cafeteriaName.value = name
            _cafeteriaDescription.value = description
            _cafeteriaSubDescription.value = subDescription
            _cafeteriaOpenFrom.value = openedFrom
            _cafeteriaOpenTo.value = openedTo
            _cafeteriaAddress.value = address
            _cafeteriaOccupancy.value = scaleOccupancy(occupancy)
        }
    }

    private fun scaleOccupancy(modelData: Double) = (modelData * 100).toInt()
}
