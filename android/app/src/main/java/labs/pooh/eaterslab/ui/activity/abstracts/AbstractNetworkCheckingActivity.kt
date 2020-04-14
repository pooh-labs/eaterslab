package labs.pooh.eaterslab.ui.activity.abstracts

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import labs.pooh.eaterslab.ui.activity.loading.LoadingConnectionActivity
import labs.pooh.eaterslab.util.isNetworkConnected
import labs.pooh.eaterslab.util.start


abstract class AbstractNetworkCheckingActivity : AbstractThemedActivity() {

    companion object {
        private val networkRequest = NetworkRequest.Builder().apply {
            addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            addTransportType(NetworkCapabilities.TRANSPORT_VPN)
        }.build()
    }

    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        unregisterNetworkChecking()
        registerNetworkChecking()
    }

    override fun onResume() {
        super.onResume()

        unregisterNetworkChecking()
        registerNetworkChecking()
    }

    override fun onPause() {
        super.onPause()
        unregisterNetworkChecking()
    }

    private fun registerNetworkChecking() {
        if (!isNetworkConnected(baseContext) && this !is LoadingConnectionActivity) {
            start<LoadingConnectionActivity>()
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
            = RunNoConnectionActivityCallback()

    inner class RunNoConnectionActivityCallback() : ConnectivityManager.NetworkCallback () {

        override fun onLost(network: Network) {
            super.onLost(network)
            start<LoadingConnectionActivity>()
        }
    }
}
