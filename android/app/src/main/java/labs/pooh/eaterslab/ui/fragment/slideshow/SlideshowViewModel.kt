package labs.pooh.eaterslab.ui.fragment.slideshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import labs.pooh.eaterslab.model.MenuOption
import labs.pooh.eaterslab.util.downloadImageFrom

class SlideshowViewModel : ViewModel() {

    private val _textVisible = MutableLiveData<Boolean>().apply {
        value = true
    }

    val textVisible: LiveData<Boolean> = _textVisible

    private val menuOptionLiveData = MutableLiveData<MutableList<MenuOption>>().apply {
        value = mutableListOf()
    }

    private val _lastLoadedMenuOptionLiveData = MutableLiveData<MenuOption>()

    val lastLoadedMenuOptionLiveData: LiveData<MenuOption> = _lastLoadedMenuOptionLiveData

    private fun addMenuOption(menuOption: MenuOption?) {
        if (menuOption == null) {
            return
        }
        _lastLoadedMenuOptionLiveData.value = menuOption
        menuOptionLiveData.value?.add(menuOption)
        menuOptionLiveData.value = menuOptionLiveData.value
    }

    private suspend fun fetchMenuOptionsData(): List<FakeMenuOptionGeneratedFromApi> {
        delay(1500) // sleep for test purpose TODO remove and change to API call
        return listOf(
            FakeMenuOptionGeneratedFromApi("Pyszny posiłek", 3f, "https://static.polityka.pl/_resource/res/path/b7/b0/b7b0a0f3-0cb7-48b8-9dec-30bf0f7463d3_f1400x900", 23.50),
            FakeMenuOptionGeneratedFromApi("Posiłek o bardzo długiej nazwie", 4.5f, "https://ocs-pl.oktawave.com/v1/AUTH_2887234e-384a-4873-8bc5-405211db13a2/spidersweb/2016/12/pyszne-pl.jpg", 15.54),
            FakeMenuOptionGeneratedFromApi("Szybki obiadek", 1.5f, "https://cdn.pizzaportal.pl/content/4cb97ca0f7bfc00fcaf0a229facedcfb.png", 11.22),
            FakeMenuOptionGeneratedFromApi("FastAndFood", 4.8f, "https://ocs-pl.oktawave.com/v1/AUTH_2887234e-384a-4873-8bc5-405211db13a2/spidersweb/2019/02/pexels-photo-1092730-1180x885.jpeg", 16.0)
        )
    }

    fun updateMenuOptionsData() {
        viewModelScope.launch {
            val data = fetchMenuOptionsData()
            for (option in data) {
                val withImage = option.downloadImage()
                addMenuOption(withImage)
            }
        }
    }

    fun clearMenuOptionsData() {
        menuOptionLiveData.value = mutableListOf()
    }
}

data class FakeMenuOptionGeneratedFromApi(val name: String, val stars: Float, val imageURL: String, val price: Double)

private suspend fun FakeMenuOptionGeneratedFromApi.downloadImage(): MenuOption? {
    val image = downloadImageFrom(imageURL)
    return if (image != null) {
        MenuOption(name, stars, image, price)
    }
    else {
        null
    }
}
