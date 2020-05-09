package labs.pooh.eaterslab.repository.dao

interface DownloadableContent<T> {
    suspend fun downloadContent(): T
}