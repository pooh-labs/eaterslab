package labs.pooh.eaterslab.repository

import labs.pooh.client.apis.CafeteriasApi
import labs.pooh.client.infrastructure.ClientException
import labs.pooh.client.infrastructure.ServerException
import labs.pooh.eaterslab.repository.dao.CafeteriaDao
import labs.pooh.eaterslab.repository.dao.FixedMenuOptionReviewDao
import labs.pooh.eaterslab.repository.dao.MenuOptionTagDao
import labs.pooh.eaterslab.repository.dao.toDao

object CafeteriaRepository {

    private val cafeteriasApi by lazy { CafeteriasApi() }

    private val cachedCafeterias: Map<Int, CafeteriaDao> = mutableMapOf()
    private val cachedTags: Map<Int, MenuOptionTagDao> = mutableMapOf()
    private val cachedReviews: Map<Int, FixedMenuOptionReviewDao> = mutableMapOf()

    fun cafeteriasList(): List<CafeteriaDao> {
        try {
            val data = cafeteriasApi.cafeteriasList()
        } catch (e: ClientException) {

        } catch (e: ServerException) {

        } catch (e: UnsupportedOperationException) {

        }
        return listOf()
    }
}