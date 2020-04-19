package labs.pooh.eaterslab.ui.fragment.home

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
import labs.pooh.eaterslab.ui.activity.main.MainActivity
import okhttp3.internal.notify

class CafeteriaViewModel : ViewModel() {

    private val API = CafeteriasApi(BuildConfig.API_URL)

    private val _cafeteriaName = MutableLiveData<String>()
    val cafeteriaName: LiveData<String> = _cafeteriaName

    private val _cafeteriaDescription = MutableLiveData<String>()
    val cafeteriaDescription: LiveData<String> = _cafeteriaDescription

    private val _cafeteriaSubDescription = MutableLiveData<String>()
    val cafeteriaSubDescription: LiveData<String> = _cafeteriaSubDescription

    private val _cafeteriaCurrentDayData = MutableLiveData<List<Double>>()
    val cafeteriaCurrentDayData: LiveData<List<Double>> = _cafeteriaCurrentDayData

    init {
        setEmptyData()
    }

    fun updateCafeteriaTextInfo() {
        viewModelScope.launch {
            // TODO create repository for data management and use it here
           try {
               val cafeteria = withContext(Dispatchers.IO) { API.cafeteriasRead(MainActivity.lastSelectedCafeteriaId) }
               _cafeteriaName.value = cafeteria.name
               _cafeteriaDescription.value = cafeteria.description
               _cafeteriaSubDescription.value = cafeteria.subDescription
           } catch (e: ClientException) {
               // TODO handle errors in common way
           }
        }
    }

    private fun setEmptyData() {
        arrayOf(_cafeteriaName, _cafeteriaDescription, _cafeteriaSubDescription).forEach {
            it.value = ""
        }
        _cafeteriaCurrentDayData.value = listOf()
    }
}
