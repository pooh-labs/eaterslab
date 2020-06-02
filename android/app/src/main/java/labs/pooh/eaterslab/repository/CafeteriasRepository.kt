package labs.pooh.eaterslab.repository

import com.yariksoffice.lingver.Lingver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import labs.pooh.eaterslab.client.apis.CafeteriasApi
import labs.pooh.eaterslab.client.apis.FixedMenuReviewsApi
import labs.pooh.eaterslab.client.models.AverageDishReviewStats
import labs.pooh.eaterslab.client.models.FixedMenuOptionReview
import labs.pooh.eaterslab.client.models.OccupancyStats
import labs.pooh.eaterslab.repository.dao.*
import labs.pooh.eaterslab.ui.activity.abstracts.ConnectionStatusNotifier
import org.threeten.bp.OffsetDateTime
import java.lang.Exception
import java.math.BigDecimal

class CafeteriasRepository(private val connectionStatusNotifier: ConnectionStatusNotifier) {

    private val cafeteriasApi by lazy { CafeteriasApi() }
    private val reviewsApi by lazy { FixedMenuReviewsApi() }

    private val cachedCafeterias = mutableMapOf<Int, CafeteriaDao>()
    private val cachedMenuOptions = mutableMapOf<Int, List<FixedMenuOptionDao>>()

    suspend fun cafeteriasList() = cafeteriasListFiltered()

    suspend fun cafeteriasListFiltered(openedFrom: TimeApi? = null, openedTo: TimeApi? = null,
                                       openedNow: BooleanApi? = null, haveVegs: BooleanApi? = null, prefixName: String? = null): List<CafeteriaDao>? {
        val refreshed = tryApiConnect {
            val models = cafeteriasApi.cafeteriasList(openedFrom?.getForRequest(), openedTo?.getForRequest(),
                                                      openedNow?.getForRequest(), prefixName, null,
                                                      haveVegs?.getForRequest(), usedLanguage)
            val daos = models.map { it.toDao() }
            daos
        }

        refreshed?.forEach {
            cachedCafeterias[it.id ?: 0] = it
        }
        val currentCafeteriaData = cachedCafeterias.values

        return if (currentCafeteriaData.isNotEmpty()) currentCafeteriaData.toList() else null
    }

    suspend fun cafeteriasRead(id: Int): CafeteriaDao? {
        val refreshed = tryApiConnect {
            val model = cafeteriasApi.cafeteriasRead(id)
            val dao = model.toDao()
            dao
        }

        refreshed?.let {
            cachedCafeterias[it.id ?: 0] = it
        }

        return cachedCafeterias[id]
    }

    suspend fun cafeteriaOccupancyStatsRead(cafeteriaId: Int, from: OffsetDateTime, to: OffsetDateTime, count: Int,
                                            groupBy: StatsGroupOption): List<OccupancyStatsDao>? {
        val data = tryApiConnect {
            cafeteriasApi.cafeteriasStatsOccupancyList("$cafeteriaId", "$from", "$to",
                BigDecimal(count), groupBy.getForRequest())
        }

        return data?.map(OccupancyStats::toDao)
    }

    suspend fun cafeteriaReviewStatsRead(cafeteriaId: Int, from: OffsetDateTime, to: OffsetDateTime, count: Int,
                                         groupBy: StatsGroupOption): List<AverageDishReviewStatsDao>? {
        val data = tryApiConnect {
            cafeteriasApi.cafeteriasStatsAvgDishReviewList("$cafeteriaId", "$from", "$to",
                BigDecimal(count), groupBy.getForRequest())
        }

        return data?.map(AverageDishReviewStats::toDao)
    }

    suspend fun addFixedMenuOptionReview(menuOption: Int, stars: Int, name: String) {
        val review = FixedMenuOptionReview(stars, name, OffsetDateTime.now(), menuOption)
        tryApiConnect {
            reviewsApi.fixedMenuReviewsCreate(review)
        }
    }

    suspend fun getMenuOptionsOfCafeteria(cafeteriaDao: CafeteriaDao): List<FixedMenuOptionDao>? {
        val refreshed = tryApiConnect {
            val models = cafeteriasApi.cafeteriasFixedMenuOptionsList(cafeteriaDao.id!!.toString(), usedLanguage)
            val daos = models.map { it.toDao() }
            daos
        }

        refreshed?.let {
            cachedMenuOptions[cafeteriaDao.id ?: 0] = it
        }
        val currentMenuOptionData = cafeteriaDao.id?.let { cachedMenuOptions[it] } ?: listOf()

        return if (currentMenuOptionData.isNotEmpty()) currentMenuOptionData else null
    }

    suspend fun getMenuOptionsOfCafeteria(cafeteriaId: Int): List<FixedMenuOptionDao>? {
        val refreshed = tryApiConnect {
            val models = cafeteriasApi.cafeteriasFixedMenuOptionsList(cafeteriaId.toString(), usedLanguage)
            val daos = models.map { it.toDao() }
            daos
        }

        refreshed?.let {
            cachedMenuOptions[cafeteriaId] = it
        }
        val currentMenuOptionData = cachedMenuOptions[cafeteriaId]

        return if (currentMenuOptionData?.isNotEmpty() == true) currentMenuOptionData else null
    }

    private suspend fun <T> tryApiConnect(get: suspend () -> T): T? {
        try {
            return withContext(Dispatchers.IO) { get() }
        } catch (e: Exception) {
            reportDataFetchError()
        }
        return null
    }

    private fun reportDataFetchError() = connectionStatusNotifier.notifyDataFetchError()

    fun isDevAPIVersion(): Boolean = cafeteriasApi.baseUrl.contains("dev", ignoreCase = true)

    private val usedLanguage: String
    get() {
        return Lingver.getInstance().getLanguage()
    }
}

interface ApiParamRepresentation {
    fun getForRequest(): String
}

class TimeApi(val hour: Int, val minute: Int) : ApiParamRepresentation {

    override fun getForRequest(): String = "$hour:$minute:00"
}

class BooleanApi(val value: Boolean) : ApiParamRepresentation {
    override fun getForRequest(): String = if (value) "True" else "False"

    companion object {
        fun packedForTrue(value: Boolean) = if (value) BooleanApi(true) else null
    }
}

enum class StatsGroupOption: ApiParamRepresentation {
    BY_HOUR { override fun getForRequest() = "hour" },
    BY_DAY { override fun getForRequest() = "day" },
    BY_WEEK { override fun getForRequest() = "week" },
    BY_MONTH  { override fun getForRequest() = "month" },
    BY_YEAR { override fun getForRequest() = "year" },
}
