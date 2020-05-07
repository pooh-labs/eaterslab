package labs.pooh.eaterslab.repository

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import labs.pooh.client.apis.BaseCafeteriasApi
import labs.pooh.client.apis.CafeteriasApi
import labs.pooh.client.apis.FixedMenuReviewsApi
import labs.pooh.client.infrastructure.ClientException
import labs.pooh.client.infrastructure.ServerException
import labs.pooh.client.models.FixedMenuOptionReview
import labs.pooh.eaterslab.repository.dao.*
import labs.pooh.eaterslab.ui.activity.abstracts.ConnectionStatusNotifier
import org.threeten.bp.OffsetDateTime

class CafeteriasRepository(private  val connectionStatusNotifier: ConnectionStatusNotifier) {

    private val cafeteriasApi by lazy { CafeteriasApi() }
    private val cafeteriasBaseApi by lazy { BaseCafeteriasApi() }
    private val reviewsApi by lazy { FixedMenuReviewsApi() }

    private val cachedCafeterias = mutableMapOf<Int, CafeteriaDao>()
    private val cachedCafeteriasList = mutableSetOf<CafeteriaBaseDao>()

    private val cachedTags = mutableMapOf<Int, MenuOptionTagDao>()
    private val cachedReviews = mutableMapOf<Int, FixedMenuOptionReviewDao>()

    suspend fun cafeteriasList(): List<CafeteriaBaseDao>? {
        val data = tryApiConnect { cafeteriasBaseApi.baseCafeteriasList() }

        data?.map { it.toDao() }?.forEach { cachedCafeteriasList += it }
        cachedCafeterias.values.map(CafeteriaDao::toBase).forEach { cachedCafeteriasList += it }

        return if (cachedCafeteriasList.isNotEmpty()) cachedCafeteriasList.toList() else null
    }

    suspend fun cafeteriasRead(id: Int): CafeteriaDao? {
        val refreshed = tryApiConnect {
            val model = cafeteriasApi.cafeteriasRead(id)
            val withImage = model.toDao()
            withImage
        } ?: cachedCafeterias[id]

        refreshed?.let {
            cachedCafeterias[it.id] = it
        }

        return refreshed
    }

    suspend fun addFixedMenuOptionReview(menuOption: Int, stars: Int, name: String): Unit {
        val review = FixedMenuOptionReview(stars, name, OffsetDateTime.now(), menuOption)
        tryApiConnect { reviewsApi.fixedMenuReviewsCreate(review) }
    }

    private suspend fun <T> tryApiConnect(get: suspend () -> T): T? {
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