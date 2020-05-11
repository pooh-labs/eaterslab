package labs.pooh.eaterslab.ui.fragment.menu

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_menu.*
import labs.pooh.eaterslab.R
import labs.pooh.eaterslab.repository.dao.FixedMenuOptionDao
import labs.pooh.eaterslab.ui.activity.abstracts.ConnectionStatusNotifier
import labs.pooh.eaterslab.ui.activity.abstracts.viewModelFactory
import labs.pooh.eaterslab.ui.view.RatedFoodView

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
        return inflater.inflate(R.layout.fragment_menu, container, false)
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
        menuOptionsGridLayout.removeAllViews()
    }

    private fun addMenuOptionToView(menuOption: FixedMenuOptionDao) {
        val layoutParams: GridLayout.LayoutParams = GridLayout.LayoutParams(
            GridLayout.spec(GridLayout.UNDEFINED, 1f),
            GridLayout.spec(GridLayout.UNDEFINED, 1f)
        )
        layoutParams.width = 0
        val option = with(menuOption.downloadedImage) {
            if (this != null) {
                RatedFoodView(context!!, (1..5).random().toFloat(), this, menuOption.name, menuOption.price)
            }
            else {
                RatedFoodView(context!!, (1..5).random().toFloat(), R.drawable.ic_no_food_image, menuOption.name, menuOption.price)
            }
        }
        option.layoutParams = layoutParams
        menuOptionsGridLayout.addView(option)
    }

}
