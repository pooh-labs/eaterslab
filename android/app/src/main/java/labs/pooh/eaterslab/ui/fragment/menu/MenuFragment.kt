package labs.pooh.eaterslab.ui.fragment.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_slideshow.*
import labs.pooh.eaterslab.R
import labs.pooh.eaterslab.repository.dao.FixedMenuOptionDao
import labs.pooh.eaterslab.ui.activity.abstracts.ConnectionStatusNotifier
import labs.pooh.eaterslab.ui.activity.abstracts.viewModelFactory
import labs.pooh.eaterslab.ui.view.RatedFoodView
import labs.pooh.eaterslab.util.convertDrawableToBitmap

class MenuFragment : Fragment() {

    private val slideshowViewModel by lazy {
        ViewModelProvider(this, viewModelFactory {
            MenuViewModel(activity as ConnectionStatusNotifier)
        }).get(MenuViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_slideshow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        textSlideshow.text = getString(R.string.loading)

        slideshowViewModel.textVisible.observe(viewLifecycleOwner, Observer { visible ->
            textSlideshow.visibility = if (visible) View.VISIBLE else View.GONE
        })

        slideshowViewModel.lastLoadedMenuOptionLiveData.observe(viewLifecycleOwner,
            Observer { menuOption -> addMenuOptionToView(menuOption) })
    }

    override fun onResume() {
        super.onResume()
        slideshowViewModel.updateMenuOptionsData()
    }

    override fun onPause() {
        super.onPause()
        slideshowViewModel.clearMenuOptionsData()
        verticalReviewSlideShow.removeAllViews()
    }

    private fun addMenuOptionToView(menuOption: FixedMenuOptionDao) {
        verticalReviewSlideShow.addView(
            RatedFoodView(context!!, (1..5).random().toFloat(),
                menuOption.downloadedImage ?: convertDrawableToBitmap(context!!, R.drawable.no_food_image),
                menuOption.name, menuOption.price)
        )
    }

}
