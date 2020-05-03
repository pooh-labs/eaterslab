package labs.pooh.eaterslab.ui.activity.abstracts

interface ConnectionStatusNotifier {
    fun notifyNoConnection(): Unit
    fun notifyInternetConnectionRestored(): Unit
    fun notifyDataFetchError(): Unit
}