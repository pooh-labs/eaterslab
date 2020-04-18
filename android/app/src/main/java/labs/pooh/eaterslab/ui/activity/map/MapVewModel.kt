package labs.pooh.eaterslab.ui.activity.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import labs.pooh.client.apis.CafeteriaApi
import labs.pooh.client.models.Cafeteria
import labs.pooh.eaterslab.BuildConfig

class MapVewModel : ViewModel() {
    private var API = CafeteriaApi(BuildConfig.API_URL)

    private val _cafeteriasLiveData = MutableLiveData<List<Cafeteria>>().apply {
        listOf<Cafeteria>()
    }

    val cafeteriaLiveData: LiveData<List<Cafeteria>> = _cafeteriasLiveData

    fun updateMarkersData() {
        viewModelScope.launch {
            val cafeterias = withContext(Dispatchers.IO) { API.cafeteriaList() }
            _cafeteriasLiveData.value = cafeterias.toList()
        }
    }
}
