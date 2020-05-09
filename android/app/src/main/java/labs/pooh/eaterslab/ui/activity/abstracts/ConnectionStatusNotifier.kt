package labs.pooh.eaterslab.ui.activity.abstracts

interface ConnectionStatusNotifier {
    fun notifyNoConnection()
    fun notifyInternetConnectionRestored()
    fun notifyDataFetchError()
}
