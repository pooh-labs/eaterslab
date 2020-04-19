package labs.pooh.eaterslab.ui.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_home.*
import labs.pooh.eaterslab.R
import labs.pooh.eaterslab.ui.activity.main.MainActivity

class CafeteriaFragment : Fragment() {

    private val homeViewModel: CafeteriaViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        homeViewModel.cafeteriaName.observe(viewLifecycleOwner, Observer {
            placeName.text = it
        })
        homeViewModel.cafeteriaDescription.observe(viewLifecycleOwner, Observer {
            placeDescription.text = it
        })
        homeViewModel.cafeteriaSubDescription.observe(viewLifecycleOwner, Observer {
            placeSubDescription.text = it
        })
        return root
    }

    override fun onResume() {
        super.onResume()
        homeViewModel.updateCafeteriaTextInfo()
    }
}
