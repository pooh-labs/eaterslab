package labs.pooh.eaterslab.ui.activity.abstracts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import labs.pooh.eaterslab.R

abstract class AbstractThemedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (isDarkModeEnabled()) {
            if (showActionBar) {
                setTheme(R.style.AppTheme_Dark)
            }
            else {
                setTheme(R.style.AppTheme_Dark_NoActionBar)
            }
        }
        else {
            if (showActionBar) {
                setTheme(R.style.AppTheme)
            }
            else {
                setTheme(R.style.AppTheme_NoActionBar)
            }
        }

        super.onCreate(savedInstanceState)
    }

    abstract val showActionBar: Boolean

    fun isDarkModeEnabled(): Boolean {
        val prefs = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        return prefs.getBoolean(getString(R.string.dark_theme), false)
    }
}
