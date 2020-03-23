package labs.pooh.client.infrastructure

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.OffsetDateTime
import java.util.UUID
import java.util.Date

object Serializer {
    @JvmStatic
    val gsonBuilder: GsonBuilder = GsonBuilder()
        .registerTypeAdapter(Date::class.java, DateAdapter())
        .registerTypeAdapter(OffsetDateTime::class.java, OffsetDateTimeAdapter())
        .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
        .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
        .registerTypeAdapter(ByteArray::class.java, ByteArrayAdapter())
    
    @JvmStatic
    val gson: Gson by lazy {
        gsonBuilder.create()
    }
}
