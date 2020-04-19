package labs.pooh.eaterslab.ui.activity.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import labs.pooh.client.apis.CafeteriasApi
import labs.pooh.client.infrastructure.ClientException
import labs.pooh.client.models.Cafeteria
import labs.pooh.eaterslab.BuildConfig

class MapVewModel : ViewModel() {
    private val API = CafeteriasApi(BuildConfig.API_URL)

    private val _cafeteriasLiveData = MutableLiveData<List<Cafeteria>>().apply {
        listOf<Cafeteria>()
    }

    val cafeteriaLiveData: LiveData<List<Cafeteria>> = _cafeteriasLiveData

    fun updateMarkersData() {
        viewModelScope.launch {
            // TODO create repository for data management and use it here
            try {
                val cafeterias = withContext(Dispatchers.IO) { API.cafeteriasList() }
                _cafeteriasLiveData.value = cafeterias.toList()
            } catch (e: ClientException) {
                // TODO handle errors in common way
            }
        }
    }
}
