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
import kotlin.math.exp
import kotlin.math.pow

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

    fun updateCafeteriaTextInfo() {
        viewModelScope.launch {
            // TODO create repository for data management and use it here
           try {
               val cafeteria = withContext(Dispatchers.IO) { API.cafeteriasRead(MainActivity.lastSelectedCafeteriaId) }
               _cafeteriaName.value = cafeteria.name
               _cafeteriaDescription.value = cafeteria.description
               _cafeteriaSubDescription.value = cafeteria.subDescription
               _cafeteriaCurrentDayData.value = generateRandomHoursData()
           } catch (e: ClientException) {
               // TODO handle errors in common way
           }
        }
    }

    private fun generateRandomHoursData() = List(9) {
        exp(-((4 - it) / 2.0).pow(2))
    }.map { it * 10.0 }.toList()
}
