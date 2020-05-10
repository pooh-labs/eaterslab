package labs.pooh.eaterslab.ui.fragment.home

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import labs.pooh.client.apis.CafeteriasApi
import labs.pooh.client.infrastructure.ClientException
import labs.pooh.eaterslab.BuildConfig
import labs.pooh.eaterslab.repository.dao.CafeteriaDao
import labs.pooh.eaterslab.ui.activity.abstracts.ConnectionStatusNotifier
import labs.pooh.eaterslab.ui.activity.abstracts.RepositoryAccessViewModel
import labs.pooh.eaterslab.ui.activity.main.MainActivity
import labs.pooh.eaterslab.util.convertDrawableToBitmap
import labs.pooh.eaterslab.util.downloadImageFrom
import java.lang.Exception
import kotlin.math.exp
import kotlin.math.pow

class CafeteriaViewModel(notifier: ConnectionStatusNotifier) : RepositoryAccessViewModel(notifier) {

    private val _cafeteriaName = MutableLiveData<String>()
    val cafeteriaName: LiveData<String> = _cafeteriaName

    private val _cafeteriaOccupancy = MutableLiveData<Int>()
    val cafeteriaOccupancy: LiveData<Int> = _cafeteriaOccupancy

    private val _cafeteriaDescription = MutableLiveData<String>()
    val cafeteriaDescription: LiveData<String> = _cafeteriaDescription

    private val _cafeteriaSubDescription = MutableLiveData<String>()
    val cafeteriaSubDescription: LiveData<String> = _cafeteriaSubDescription


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

    private suspend fun updateTextCafeteriaData() {
        val cafeteriaId = MainActivity.lastSelectedCafeteriaId
        val cafeteria = repository.cafeteriasRead(cafeteriaId)
        latestCafeteriaDao = cafeteria
        latestCafeteriaDao?.run {
            _cafeteriaName.value = name
            _cafeteriaDescription.value = description
            _cafeteriaSubDescription.value = subDescription
        }

        // for tests generate data
        _cafeteriaOccupancy.value = (0..100).random()
    }
}
