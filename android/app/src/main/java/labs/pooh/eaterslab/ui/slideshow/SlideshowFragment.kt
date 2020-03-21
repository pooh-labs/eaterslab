package labs.pooh.eaterslab.ui.slideshow

import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.toObservable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.fragment_slideshow.*
import labs.pooh.eaterslab.R
import labs.pooh.eaterslab.ui.view.RatedFoodView
import labs.pooh.eaterslab.util.*

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
        return inflater.inflate(R.layout.fragment_slideshow, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val imagesURLs = mapOf(
            "Pyszny posiłek" to "https://static.polityka.pl/_resource/res/path/b7/b0/b7b0a0f3-0cb7-48b8-9dec-30bf0f7463d3_f1400x900",
            "Posiłek o bardzo długiej nazwie" to "https://ocs-pl.oktawave.com/v1/AUTH_2887234e-384a-4873-8bc5-405211db13a2/spidersweb/2016/12/pyszne-pl.jpg",
            "Szybki obiadek" to "https://cdn.pizzaportal.pl/content/4cb97ca0f7bfc00fcaf0a229facedcfb.png",
            "FastAndFood" to "https://ocs-pl.oktawave.com/v1/AUTH_2887234e-384a-4873-8bc5-405211db13a2/spidersweb/2019/02/pexels-photo-1092730-1180x885.jpeg"
        )

        textSlideshow.text = getString(R.string.loading)

        val observables = toObservableBitmaps(imagesURLs.entries.toObservable())
        val disposable = toDisposableSubscibe(observables, view)

        compositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }

    private fun toObservableBitmaps(data: Observable<Map.Entry<String, String>>)
            = data
                .map { Pair(it.key, downloadImageFrom(it.value)) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete {
                    textSlideshow.isVisible = false
                }


    private fun toDisposableSubscibe(data: Observable<Pair<String, Bitmap?>>, view: View)
            = data.subscribe { (name, image) ->
                image?.let { bitmap ->
                    verticalReviewSlideShow.addView(
                        RatedFoodView(
                            view.context,
                            stars = (1..5).random().toFloat(),
                            img = bitmap,
                            text = name,
                            price = (1..100).random().toDouble()
                        )
                    )
                }
            }

}
