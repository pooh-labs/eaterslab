package labs.pooh.eaterslab.ui.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.base_cafeteria_data.*
import kotlinx.android.synthetic.main.fragment_home.*
import labs.pooh.eaterslab.R
import labs.pooh.eaterslab.ui.activity.abstracts.ConnectionStatusNotifier
import labs.pooh.eaterslab.ui.activity.abstracts.viewModelFactory
import labs.pooh.eaterslab.ui.fragment.ThemedAbstractFragment

class CafeteriaFragment : ThemedAbstractFragment() {

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory {
            CafeteriaViewModel(activity as ConnectionStatusNotifier)
        }).get(CafeteriaViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(viewModel) {
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

            listOf(cafeteriaName, cafeteriaDescription, cafeteriaSubDescription,
                cafeteriaOpenFrom, cafeteriaOpenTo, cafeteriaAddress
            ).zip(
                listOf(placeName, placeDescription, placeSubDescription,
                    hoursFrom, hoursTo, placeAddress)
            ).forEach { (liveData, textView) ->
                liveData.observe(viewLifecycleOwner, Observer { textView.text = it })
            }
        }

        viewModel.updateCafeteriaFullData()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateCafeteriaTextInfo()
    }
}
