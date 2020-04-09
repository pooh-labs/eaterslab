package labs.pooh.eaterslab.ui.activity.settings

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.preference.PreferenceFragmentCompat
import com.yariksoffice.lingver.Lingver
import labs.pooh.eaterslab.ui.activity.abstracts.AbstractThemedActivity
import labs.pooh.eaterslab.App
import labs.pooh.eaterslab.R
import labs.pooh.eaterslab.ui.activity.hello.HelloSelectActivity


class SettingsActivity : AbstractThemedActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(
                R.id.settings,
                SettingsFragment()
            )
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override val showActionBar = true

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }

        override fun onResume() {
            super.onResume()
            preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
        }

        override fun onPause() {
            super.onPause()
            preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        }

        override fun onSharedPreferenceChanged(
            sharedPreferences: SharedPreferences?,
            key: String?
        ) {
            when(key) {
                getString(R.string.dark_theme) -> {
                    restartApplication()
                }
                getString(R.string.lang) -> {
                    val lang = preferenceScreen.sharedPreferences.getString(getString(
                        R.string.lang
                    ), "")
                    setNewLocale(lang)
                }
            }
        }

        private fun setNewLocale(lang: String?) {
            Lingver.getInstance().setLocale(context!!,
                App.getLocale(lang)
            )
            restartApplication()
        }

        private fun restartApplication() {
            startActivity(Intent(context!!, HelloSelectActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK))
        }
    }
}
