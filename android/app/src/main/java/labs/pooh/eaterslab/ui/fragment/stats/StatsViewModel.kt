package labs.pooh.eaterslab.ui.fragment.stats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import labs.pooh.eaterslab.repository.CafeteriasRepository
import labs.pooh.eaterslab.repository.StatsGroupOption
import labs.pooh.eaterslab.repository.dao.OccupancyStatsDao
import labs.pooh.eaterslab.ui.activity.abstracts.ConnectionStatusNotifier
import labs.pooh.eaterslab.ui.activity.main.MainActivity
import org.threeten.bp.OffsetDateTime

class StatsViewModel(connectionStatusNotifier: ConnectionStatusNotifier) : ViewModel() {

    companion object {
        const val minHour = 8
        const val hours = 12
        const val maxHour = minHour + hours
    }

    private val repository = CafeteriasRepository(connectionStatusNotifier)

    private val _currentDayData = MutableLiveData<List<OccupancyStatsDao>>().apply {
        value = listOf()
    }

    val currentDayData: LiveData<List<OccupancyStatsDao>> = _currentDayData

    fun updateStatsDayData() = viewModelScope.launch {
        val cafeteriaId = MainActivity.lastSelectedCafeteriaId

        val minTime = OffsetDateTime.now()
            .withHour(minHour)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)

        val data = repository.cafeteriaOccupancyStatsRead(cafeteriaId, minTime, hours, StatsGroupOption.BY_HOUR)
        _currentDayData.value = data
    }
}