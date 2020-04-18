package labs.pooh.eaterslab.ui.gallery

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Space
import androidx.core.content.ContextCompat
import androidx.core.view.plusAssign
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import kotlinx.android.synthetic.main.fragment_gallery.*
import labs.pooh.eaterslab.R
import labs.pooh.eaterslab.util.*
import kotlin.math.abs
import kotlin.math.log
import kotlin.math.sin

class GalleryFragment : Fragment() {

    private val galleryViewModel: GalleryViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val red = ContextCompat.getColor(requireContext(), R.color.colorAccent)
        val yellow = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)

        val exampleData = listOf(
            List(30) { it + sin(it.toDouble()) },
            List(7) { 3 + sin(it.toDouble()) },
            List(13) { abs(6 - it).toDouble() },
            List(24) { if (it in (8..16)) (0..30).random().toDouble() else 0.0 },
            List(13) { 12 - abs(6 - it).toDouble() },
            List(10) { log(1.0 + it, 2.0) },
            List(51) { sin(abs(it - 25).toDouble() + 0.001) / (abs(it - 25)  + 0.001) }
        )

        // example data plots
        exampleData.forEach { dataSet ->
            with(requireContext()) {
                val frame = FrameLayout(this)
                val barView = when(dataSet.size) {
                    24 -> HorizontalBarPlot(this).apply {
                        ticks = getOrderedHours()
                        ticksIndexer = ::hoursIndexer
                        ticksScale = 2.0
                    }.plot(dataSet, red)
                    30 -> HorizontalBarPlot(this).apply {
                        ticks = getOrderedMonthDays(4)
                        ticksIndexer = ::monthDaysIndexer
                        ticksScale = 1.5
                    }.plot(dataSet, red)
                    7 -> HorizontalBarPlot(this).apply {
                        ticks = getOrderedWeekDays()
                        ticksIndexer = ::weekDaysIndexer
                    }.plot(dataSet, red)
                    else -> HorizontalBarPlot(this).plot(dataSet, red)
                }
                val plotView = DiscreteLinePlot(this).plot(dataSet, yellow)

                frame += barView
                frame += plotView
                frame.layoutParams = FrameLayout.LayoutParams(1000, 600).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                }

                linearLayout += frame
                linearLayout += Space(this).apply {
                    layoutParams = ViewGroup.LayoutParams(0, 50)
                }
            }
        }
    }
}
