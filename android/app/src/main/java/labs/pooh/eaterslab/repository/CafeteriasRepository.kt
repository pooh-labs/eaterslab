package labs.pooh.eaterslab.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import labs.pooh.client.apis.CafeteriasApi
import labs.pooh.client.apis.FixedMenuReviewsApi
import labs.pooh.client.models.FixedMenuOptionReview
import labs.pooh.eaterslab.repository.dao.*
import labs.pooh.eaterslab.ui.activity.abstracts.ConnectionStatusNotifier
import org.threeten.bp.OffsetDateTime
import java.lang.Exception

class CafeteriasRepository(private val connectionStatusNotifier: ConnectionStatusNotifier) {

    private val cafeteriasApi by lazy { CafeteriasApi() }
    private val reviewsApi by lazy { FixedMenuReviewsApi() }

    private val cachedCafeterias = mutableMapOf<Int, CafeteriaDao>()
    private val cachedMenuOptions = mutableMapOf<Int, List<FixedMenuOptionDao>>()

    suspend fun cafeteriasList(): List<CafeteriaDao>? {
        val refreshed = tryApiConnect {
            val models = cafeteriasApi.cafeteriasList()
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

    suspend fun addFixedMenuOptionReview(menuOption: Int, stars: Int, name: String) {
        val review = FixedMenuOptionReview(stars, name, OffsetDateTime.now(), menuOption)
        tryApiConnect {
            reviewsApi.fixedMenuReviewsCreate(review)
        }
    }

    suspend fun getMenuOptionsOfCafeteria(cafeteriaDao: CafeteriaDao): List<FixedMenuOptionDao>? {
        val refreshed = tryApiConnect {
            val models = cafeteriasApi.cafeteriasFixedMenuOptionsList(cafeteriaDao.id!!.toString())
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
            val models = cafeteriasApi.cafeteriasFixedMenuOptionsList(cafeteriaId.toString())
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
}
