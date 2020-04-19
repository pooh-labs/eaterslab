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
import kotlinx.android.synthetic.main.fragment_home.*
import labs.pooh.eaterslab.R
import labs.pooh.eaterslab.ui.activity.abstracts.AbstractThemedActivity
import labs.pooh.eaterslab.ui.fragment.ThemedAbstractFragment
import labs.pooh.eaterslab.util.HorizontalBarPlot
import labs.pooh.eaterslab.util.getOpenOrderedHours
import labs.pooh.eaterslab.util.hoursIndexer
import labs.pooh.eaterslab.util.workingHoursIndexer

class CafeteriaFragment : ThemedAbstractFragment() {

    private val model: CafeteriaViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_home, container, false)
        model.cafeteriaName.observe(viewLifecycleOwner, Observer {
            placeName.text = it
        })
        model.cafeteriaDescription.observe(viewLifecycleOwner, Observer {
            placeDescription.text = it
        })
        model.cafeteriaSubDescription.observe(viewLifecycleOwner, Observer {
            placeSubDescription.text = it
        })
        model.cafeteriaCurrentDayData.observe(viewLifecycleOwner, Observer {
            managePlotViewData(it)
        })
        return root
    }

    override fun onResume() {
        super.onResume()
        model.updateCafeteriaTextInfo()
    }

    private fun managePlotViewData(data: List<Double>) {
        val frame = FrameLayout(context!!)
        val barView = HorizontalBarPlot(context!!).apply {
            ticks = getOpenOrderedHours()
            ticksIndexer = ::workingHoursIndexer
            ticksScale = 2.3
            labelColor = getPlotFontColorForTheme()
            printTicks = printTicks()
        }.plot(data, accentColor)
        frame += barView
        val width = (plotsLayout.width * 0.9).toInt()
        frame.layoutParams = FrameLayout.LayoutParams(width, (0.6 * width).toInt()).apply {
            gravity = Gravity.CENTER_HORIZONTAL
        }
        plotsLayout.removeAllViews()
        plotsLayout += frame
    }
}
