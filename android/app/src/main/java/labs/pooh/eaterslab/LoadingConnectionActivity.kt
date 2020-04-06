package labs.pooh.eaterslab

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle

class LoadingConnectionActivity : AppCompatActivity() {
    companion object {
        private var isLoadingScreenActive = false

        fun isLoadingScreenActive() = isLoadingScreenActive
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_connection)
    }

    override fun onStart() {
        super.onStart()
        isLoadingScreenActive = true
    }

    override fun onStop() {
        super.onStop()
        isLoadingScreenActive = false
    }
}
