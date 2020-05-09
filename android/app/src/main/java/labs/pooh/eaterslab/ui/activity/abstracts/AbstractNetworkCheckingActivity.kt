package labs.pooh.eaterslab.ui.activity.abstracts

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import labs.pooh.eaterslab.R
import labs.pooh.eaterslab.util.isNetworkConnected
import labs.pooh.eaterslab.util.snack


abstract class AbstractNetworkCheckingActivity : AbstractThemedActivity(), ConnectionStatusNotifier {

    companion object {
        private val networkRequest = NetworkRequest.Builder().apply {
            addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            addTransportType(NetworkCapabilities.TRANSPORT_VPN)
        }.build()
    }

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    override fun onResume() {
        super.onResume()

        unregisterNetworkChecking()
        registerNetworkChecking()
    }

    override fun onPause() {
        super.onPause()
        unregisterNetworkChecking()
    }

    override fun notifyInternetConnectionRestored() {
        snack(getString(R.string.connection_restored))
    }

    override fun notifyNoConnection() {
        snack(getString(R.string.connection_lost))
    }

    override fun notifyDataFetchError() {
        snack(getString(R.string.data_fetch_error))
    }

    private fun registerNetworkChecking() {
        if (!isNetworkConnected(baseContext)) {
            notifyNoConnection()
        }

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = defineNetworkChangeCallback()
        networkCallback?.let {
            cm.registerNetworkCallback(networkRequest, it)
        }
    }

    private fun unregisterNetworkChecking() {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback?.let {
            cm.unregisterNetworkCallback(it)
        }
        networkCallback = null
    }

    protected open fun defineNetworkChangeCallback(): ConnectivityManager.NetworkCallback
            = RunConnectionActivityCallback()

    inner class RunConnectionActivityCallback() : ConnectivityManager.NetworkCallback () {

        private var entryNotificationDone = false

        override fun onLost(network: Network) {
            super.onLost(network)
            notifyNoConnection()
        }

        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            if (entryNotificationDone) {
                notifyInternetConnectionRestored()
            }
            else {
                entryNotificationDone = true
            }
        }
    }
}
