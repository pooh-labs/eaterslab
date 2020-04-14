package labs.pooh.eaterslab.ui.fragment.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_slideshow.*
import labs.pooh.eaterslab.R
import labs.pooh.eaterslab.model.MenuOption
import labs.pooh.eaterslab.ui.view.RatedFoodView

class SlideshowFragment : Fragment() {

    private val slideshowViewModel by viewModels<SlideshowViewModel>()

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

    private fun addMenuOptionToView(menuOption: MenuOption) {
        verticalReviewSlideShow.addView(
            RatedFoodView(context!!, menuOption.stars, menuOption.image, menuOption.name, menuOption.price)
        )
    }

}
