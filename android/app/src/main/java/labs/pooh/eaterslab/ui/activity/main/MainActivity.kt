package labs.pooh.eaterslab.ui.activity.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import labs.pooh.eaterslab.R
import labs.pooh.eaterslab.ui.activity.settings.SettingsActivity
import labs.pooh.eaterslab.ui.activity.abstracts.AbstractNetworkCheckingActivity

class MainActivity : AbstractNetworkCheckingActivity() {

    companion object {
        const val ID_KEY = "CAFETERIA_KEY"
        var lastSelectedCafeteriaId: Int = -1
    }

    private lateinit var appBarConfiguration: AppBarConfiguration

    override val showActionBar = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        val navController = findNavController(R.id.navHostFragment)

        if (isDarkModeEnabled()) {
            toolbar.popupTheme = R.style.AppTheme_Dark_PopupOverlay
        }
        else {
            toolbar.popupTheme = R.style.AppTheme_PopupOverlay
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navHome,
                R.id.navStats,
                R.id.navMenu
            ),
            drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        lastSelectedCafeteriaId = intent.getIntExtra(ID_KEY, -1)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
