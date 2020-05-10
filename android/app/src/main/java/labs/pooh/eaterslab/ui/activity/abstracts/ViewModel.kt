package labs.pooh.eaterslab.ui.activity.abstracts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import labs.pooh.eaterslab.repository.CafeteriasRepository

@Suppress("UNCHECKED_CAST")
inline fun <VM : ViewModel> viewModelFactory(crossinline construct: () -> VM) =
    object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(aClass: Class<T>):T = construct() as T
    }

abstract class RepositoryAccessViewModel(notifier: ConnectionStatusNotifier) : ViewModel() {

    protected val repository = CafeteriasRepository(notifier)
}