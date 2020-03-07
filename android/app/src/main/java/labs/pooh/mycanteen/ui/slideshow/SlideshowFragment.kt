package labs.pooh.mycanteen.ui.slideshow

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import kotlinx.android.synthetic.main.fragment_slideshow.*
import labs.pooh.mycanteen.R

class SlideshowFragment : Fragment() {

    private val slideshowViewModel: SlideshowViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)
        slideshowViewModel.text.observe(viewLifecycleOwner, Observer {
            textSlideshow.text = it
        })
        return root
    }
}
