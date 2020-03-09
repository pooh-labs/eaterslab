package labs.pooh.mycanteen.ui.slideshow

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.fragment_slideshow.*
import labs.pooh.mycanteen.R
import labs.pooh.mycanteen.ui.view.RatedFoodView
import labs.pooh.mycanteen.util.*
import java.util.*

class SlideshowFragment : Fragment() {

    private val slideshowViewModel: SlideshowViewModel by viewModels()

    /**
     * Resources that should be disposed when the fragment is destroyed
     */
    private val compositeDisposable = CompositeDisposable()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imagesURLs = listOf(
            "https://static.polityka.pl/_resource/res/path/b7/b0/b7b0a0f3-0cb7-48b8-9dec-30bf0f7463d3_f1400x900",
            "https://ocs-pl.oktawave.com/v1/AUTH_2887234e-384a-4873-8bc5-405211db13a2/spidersweb/2016/12/pyszne-pl.jpg",
            "https://cdn.pizzaportal.pl/content/4cb97ca0f7bfc00fcaf0a229facedcfb.png",
            "https://ocs-pl.oktawave.com/v1/AUTH_2887234e-384a-4873-8bc5-405211db13a2/spidersweb/2019/02/pexels-photo-1092730-1180x885.jpeg"
        )

        val names = listOf(
            "Pyszny posiłek",
            "Posiłek o bardzo długiej nazwie",
            "Szybki obiadek",
            "FastAndFood"
        )

        textSlideshow.text = "Ładowanie..."

        val disposable =
            imagesURLs
                .toObservable()
                .map(::downloadImageFrom)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError {
                    textSlideshow.text = "Błąd ładowania"
                }
                .doOnComplete {
                    textSlideshow.isVisible = false
                }
                .subscribe { image ->
                    image?.let { bitmap ->
                        verticalReviewSlideShow.addView(
                            RatedFoodView(
                                view.context,
                                stars = (1..5).random().toFloat(),
                                img = bitmap,
                                text = names.random(),
                                price = (1..100).random().toDouble()
                            )
                        )
                    }
                }

        disposable.addTo(compositeDisposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}
