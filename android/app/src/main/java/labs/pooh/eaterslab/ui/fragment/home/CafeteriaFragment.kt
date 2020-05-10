package labs.pooh.eaterslab.ui.fragment.home

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.plusAssign
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_home.*
import labs.pooh.eaterslab.R
import labs.pooh.eaterslab.ui.activity.abstracts.AbstractThemedActivity
import labs.pooh.eaterslab.ui.activity.abstracts.ConnectionStatusNotifier
import labs.pooh.eaterslab.ui.activity.abstracts.viewModelFactory
import labs.pooh.eaterslab.ui.fragment.ThemedAbstractFragment
import labs.pooh.eaterslab.util.*

class CafeteriaFragment : ThemedAbstractFragment() {

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory {
            CafeteriaViewModel(activity as ConnectionStatusNotifier)
        }).get(CafeteriaViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        with(viewModel) {
            cafeteriaName.observe(viewLifecycleOwner, Observer {
                placeName.text = it
            })
            cafeteriaDescription.observe(viewLifecycleOwner, Observer {
                placeDescription.text = it
            })
            cafeteriaSubDescription.observe(viewLifecycleOwner, Observer {
                placeSubDescription.text = it
            })
            cafeteriaLogo.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    imageViewLogo.setImageBitmap(it)
                }
                else {
                    imageViewLogo.setImageResource(R.drawable.ic_location)
                }
            })
            cafeteriaOccupancy.observe(viewLifecycleOwner, Observer {
                occupancyBar.progress = it
            })
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.updateCafeteriaFullData()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateCafeteriaTextInfo()
    }
}
