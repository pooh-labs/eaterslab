package labs.pooh.eaterslab.ui.fragment.stats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.data2viz.timer.now
import kotlinx.coroutines.launch
import labs.pooh.eaterslab.repository.CafeteriasRepository
import labs.pooh.eaterslab.repository.StatsGroupOption
import labs.pooh.eaterslab.repository.dao.AverageDishReviewStatsDao
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

    private val _currentDayOccupancyData = MutableLiveData<List<OccupancyStatsDao>>().apply {
        value = listOf()
    }

    private val _currentMonthAverageReviewData = MutableLiveData<List<AverageDishReviewStatsDao>>().apply {
        value = listOf()
    }

    val occupancyData: LiveData<List<OccupancyStatsDao>> = _currentDayOccupancyData

    val reviewData: LiveData<List<AverageDishReviewStatsDao>> = _currentMonthAverageReviewData

    fun updateOccupancyStatsDayData() = viewModelScope.launch {
        val cafeteriaId = MainActivity.lastSelectedCafeteriaId

        val now = OffsetDateTime.now()
        val minTime = now.withHour(minHour).zeroMinutes()

        val data = repository.cafeteriaOccupancyStatsRead(cafeteriaId, minTime, now, hours, StatsGroupOption.BY_HOUR)
        _currentDayOccupancyData.value = data
    }
    
    fun updateAvgReviewStatsMonthData() = viewModelScope.launch {
        val cafeteriaId = MainActivity.lastSelectedCafeteriaId

        val now = OffsetDateTime.now()
        val minTime = now.minusMonths(1).withHour(minHour).zeroMinutes()
        val data = repository.cafeteriaReviewStatsRead(cafeteriaId, minTime, now, Int.MAX_VALUE, StatsGroupOption.BY_DAY)
        _currentMonthAverageReviewData.value = data
    }
}

private fun OffsetDateTime.zeroMinutes() = this
    .withMinute(0)
    .withSecond(0)
    .withNano(0)