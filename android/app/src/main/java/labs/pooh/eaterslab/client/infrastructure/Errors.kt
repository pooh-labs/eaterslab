@file:Suppress("unused")
package labs.pooh.client.infrastructure

import java.lang.RuntimeException

open class ClientException(message: kotlin.String? = null, val statusCode: Int = -1) : RuntimeException(message) {

    companion object {
        private const val serialVersionUID: Long = 123L
    }
}

open class ServerException(message: kotlin.String? = null, val statusCode: Int = -1) : RuntimeException(message) {

    companion object {
        private const val serialVersionUID: Long = 456L
    }
}