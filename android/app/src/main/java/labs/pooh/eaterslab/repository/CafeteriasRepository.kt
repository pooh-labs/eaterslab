package labs.pooh.eaterslab.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import labs.pooh.client.apis.CafeteriasApi
import labs.pooh.client.apis.FixedMenuReviewsApi
import labs.pooh.client.infrastructure.ClientException
import labs.pooh.client.infrastructure.ServerException
import labs.pooh.eaterslab.repository.dao.CafeteriaDao
import labs.pooh.eaterslab.repository.dao.FixedMenuOptionReviewDao
import labs.pooh.eaterslab.repository.dao.MenuOptionTagDao
import labs.pooh.eaterslab.repository.dao.toDao
import labs.pooh.eaterslab.ui.activity.abstracts.ConnectionStatusNotifier

class CafeteriasRepository(private  val connectionStatusNotifier: ConnectionStatusNotifier) {

    private val cafeteriasApi by lazy { CafeteriasApi() }
    private val reviewsApi by lazy { FixedMenuReviewsApi() }

    private val cachedCafeterias: MutableMap<Int, CafeteriaDao> = mutableMapOf()
    private val cachedTags: MutableMap<Int, MenuOptionTagDao> = mutableMapOf()
    private val cachedReviews: MutableMap<Int, FixedMenuOptionReviewDao> = mutableMapOf()

    suspend fun cafeteriasList(): List<CafeteriaDao>? {
        val data = tryGetDataWithReport { cafeteriasApi.cafeteriasList() }

        data?.map { it.toDao() }?.run {
            cachedCafeterias.clear()
            forEach { cachedCafeterias[it.id] = it }
        }

        val cached = cachedCafeterias.values.toList()
        return if (cached.isNotEmpty()) cached else null
    }

    suspend fun cafeteriasRead(id: Int): CafeteriaDao? {
        val refreshed = tryGetDataWithReport { cafeteriasApi.cafeteriasRead(id).toDao() }
                        ?: cachedCafeterias[id]
        refreshed?.let {
            cachedCafeterias[it.id] = it
        }

        return refreshed
    }

    suspend fun myFun() {
    }



    private suspend fun <T> tryGetDataWithReport(get: () -> T): T? {
        try {
            return withContext(Dispatchers.IO) { get() }
        } catch (e: ClientException) {
            reportDataFetchError()
        } catch (e: ServerException) {
            reportDataFetchError()
        } catch (e: UnsupportedOperationException) {
            reportDataFetchError()
        }
        return null
    }

    private fun reportDataFetchError() = connectionStatusNotifier.notifyDataFetchError()
}