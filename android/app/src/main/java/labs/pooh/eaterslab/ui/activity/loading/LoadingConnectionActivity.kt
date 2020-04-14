package labs.pooh.eaterslab.ui.activity.loading

import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import labs.pooh.eaterslab.R
import labs.pooh.eaterslab.ui.activity.abstracts.AbstractNetworkCheckingActivity

class LoadingConnectionActivity : AbstractNetworkCheckingActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_loading_connection)
    }

    /**
     * Do nothing on Back pressed, activity will kill itself on
     * internet connection active or it can be minimized only
     */
    override fun onBackPressed() = Unit

    override val showActionBar = false

    override fun defineNetworkChangeCallback() =
        RunConnectionRestoredCallback(
            this
        )
}

class RunConnectionRestoredCallback(private val activity: LoadingConnectionActivity) : ConnectivityManager.NetworkCallback () {

    override fun onAvailable(network: Network) {
        super.onAvailable(network)
        activity.finish()
    }
}
