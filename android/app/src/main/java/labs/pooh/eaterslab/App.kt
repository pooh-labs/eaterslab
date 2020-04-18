package labs.pooh.eaterslab

import android.app.Application
import com.yariksoffice.lingver.Lingver
import com.yariksoffice.lingver.store.PreferenceLocaleStore
import java.util.*

class App : Application() {

    @Suppress("UNUSED_VARIABLE")
    override fun onCreate() {
        super.onCreate()
        val store = PreferenceLocaleStore(this, Locale(DEFAULT_LANGUAGE))
        val lingver = Lingver.init(this, store)
    }

    companion object {
        const val DEFAULT_LANGUAGE = BuildConfig.DEFAULT_LANG

        private val AVAILABLE_LANGUAGES = setOf("pl", "en")

        fun getLocale(language: String?): Locale {
            if (language != null && AVAILABLE_LANGUAGES.contains(language)) {
                return Locale(language)
            }
            return Locale(DEFAULT_LANGUAGE)
        }
    }
}
