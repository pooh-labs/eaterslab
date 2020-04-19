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
import labs.pooh.eaterslab.ui.activity.main.MainActivity
import labs.pooh.eaterslab.util.downloadImageFrom
import java.lang.Exception
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

    private val _cafeteriaLogo = MutableLiveData<Bitmap?>()
    val cafeteriaLogo: LiveData<Bitmap?> = _cafeteriaLogo

    fun updateCafeteriaTextInfo() {
        viewModelScope.launch {
            // TODO create repository for data management and use it here
           try {
               val cafeteria = withContext(Dispatchers.IO) { API.cafeteriasRead(MainActivity.lastSelectedCafeteriaId) }
               _cafeteriaName.value = cafeteria.name
               _cafeteriaDescription.value = cafeteria.description
               _cafeteriaSubDescription.value = cafeteria.subDescription

               // for tests generate data
               _cafeteriaCurrentDayData.value = generateRandomHoursData()
           } catch (e: ClientException) {
               // TODO handle errors in common way
           }
        }
    }

    fun updateCafeteriaFullData() {
        updateCafeteriaTextInfo()

        viewModelScope.launch {
            // TODO create repository for data management and use it here
            try {
                // TODO change to model data when ready
                val urlFromModelData = "https://scontent-vie1-1.xx.fbcdn.net/v/t1.0-9/81886122_868050633623837_983310879560826880_o.jpg?_nc_cat=100&_nc_sid=09cbfe&_nc_oc=AQkNUbuv_hCwJpyAF4NIi9IKR2T3Veyw8q-5lKsYE24CZB0TTrj-J9ypIyuPvDPtd5E&_nc_ht=scontent-vie1-1.xx&oh=4fba692698fdafa5138ab373edea8c36&oe=5EC2C2CC"
                _cafeteriaLogo.value = withContext(Dispatchers.IO) { downloadImageFrom(urlFromModelData) }
            } catch (e: Exception) {
                // TODO handle errors in common way
            }
        }
    }

    private fun generateRandomHoursData() = List(9) {
        exp(-((4 - it) / 2.0).pow(2))
    }.map { it * 10.0 }.toList()
}
