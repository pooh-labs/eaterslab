package labs.pooh.eaterslab.repository

import labs.pooh.eaterslab.repository.dao.CafeteriaDao
import labs.pooh.eaterslab.repository.dao.FixedMenuOptionReviewDao
import labs.pooh.eaterslab.repository.dao.MenuOptionTagDao

class CafeteriaRepository {
    private val cachedCafeterias: Map<Int, CafeteriaDao> = mutableMapOf()
    private val cachedTags: Map<Int, MenuOptionTagDao> = mutableMapOf()
    private val cachedReviews: Map<Int, FixedMenuOptionReviewDao> = mutableMapOf()


}