package labs.pooh.eaterslab

import android.os.Bundle
import android.view.Menu
import com.google.android.material.snackbar.Snackbar
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import labs.pooh.eaterslab.util.start

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            Snackbar.make(it, "Hello from snackbar", Snackbar.LENGTH_SHORT).show()
            start<LoadingConnectionActivity>()
        }
        val navController = findNavController(R.id.navHostFragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navHome, R.id.navGallery, R.id.navSlideshow),
            drawerLayout)
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
