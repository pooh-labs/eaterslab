package labs.pooh.eaterslab.ui.fragment.stats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
        const val MIN_HOUR = 7
        const val HOURS_OCCUPANCY = 12
        const val MONTHS_REVIEW = 1
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
        val minTime = now.minusHours(HOURS_OCCUPANCY.toLong()).zeroMinutes()

        val data = repository.cafeteriaOccupancyStatsRead(cafeteriaId, minTime, now, HOURS_OCCUPANCY, StatsGroupOption.BY_HOUR)
        _currentDayOccupancyData.value = data
    }
    
    fun updateAvgReviewStatsMonthData() = viewModelScope.launch {
        val cafeteriaId = MainActivity.lastSelectedCafeteriaId

        val now = OffsetDateTime.now()
        val minTime = now.minusMonths(MONTHS_REVIEW.toLong()).withHour(MIN_HOUR).zeroMinutes()
        val data = repository.cafeteriaReviewStatsRead(cafeteriaId, minTime, now, Int.MAX_VALUE, StatsGroupOption.BY_DAY)
        _currentMonthAverageReviewData.value = data
    }
}

private fun OffsetDateTime.zeroMinutes() = this
    .withMinute(0)
    .withSecond(0)
    .withNano(0)
