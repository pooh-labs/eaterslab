package labs.pooh.eaterslab.ui.fragment.stats

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.view.plusAssign
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.android.synthetic.main.fragment_stats.*
import labs.pooh.eaterslab.R
import labs.pooh.eaterslab.repository.dao.AverageDishReviewStatsDao
import labs.pooh.eaterslab.repository.dao.OccupancyStatsDao
import labs.pooh.eaterslab.ui.activity.abstracts.ConnectionStatusNotifier
import labs.pooh.eaterslab.ui.activity.abstracts.viewModelFactory
import labs.pooh.eaterslab.ui.fragment.ThemedAbstractFragment
import labs.pooh.eaterslab.util.*
import kotlin.math.min

class StatsFragment : ThemedAbstractFragment() {

    companion object {
        const val PLOT_HEIGHT = 600
        const val PLOT_WIDTH = 1000
    }

    private val viewModel by lazy {
        ViewModelProvider(this, viewModelFactory {
            StatsViewModel(activity as ConnectionStatusNotifier)
        }).get(StatsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val red = ContextCompat.getColor(requireContext(), R.color.colorAccent)
        val yellow = ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark)

        viewModel.occupancyData.observe(viewLifecycleOwner, Observer { stats ->
            if (stats.isEmpty()) {
                return@Observer
            }
            with(requireContext()) {
                val context = this
                val frame = FrameLayout(context)
                val barView = HorizontalBarPlot<OccupancyStatsDao>(context).apply {
                    labelColor = getPlotFontColorForTheme()
                    printTicks = printTicks()
                    ticksScale = 1.9
                    ticksDistance = 0.5
                    ticksMap = { value, index -> HourTicks(index, value.timestamp, context) }
                }.plot(stats, red) { min(it.occupancyRelative, 1.0) * 100.0 }

                frame += barView
                frame.layoutParams = FrameLayout.LayoutParams(PLOT_WIDTH, PLOT_HEIGHT).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                }

                progressBarOccupancy.visibility = View.GONE
                statsOccupancyLayout += frame
            }
        })

        viewModel.reviewData.observe(viewLifecycleOwner, Observer { stats ->
            if (stats.isEmpty()) {
                return@Observer
            }
            with(requireContext()) {
                val context = this
                val frame = FrameLayout(context)
                val barView = DiscreteLinePlot<AverageDishReviewStatsDao>(context).apply {
                    printTicks = printTicks()
                    ticksScale = 1.1
                    ticksDistance = 0.5
                    ticksMap = { value, index -> MonthDayTicks(index, value.timestamp, context) }
                }.plot(stats, yellow) { it.value }

                frame += barView
                frame.layoutParams = FrameLayout.LayoutParams(PLOT_WIDTH, PLOT_HEIGHT).apply {
                    gravity = Gravity.CENTER_HORIZONTAL
                }

                progressBarReview.visibility = View.GONE
                statsReviewLayout += frame
            }
        })
    }

    override fun onResume() {
        super.onResume()
        updateStatsOccupancy()
        updateStatsReviews()
    }

    override fun onPause() {
        super.onPause()
        resetViews()
    }

    private fun updateStatsOccupancy() {
        statsOccupancyLayout.removeAllViews()
        progressBarOccupancy.visibility = View.VISIBLE
        viewModel.updateOccupancyStatsDayData()
    }

    private fun updateStatsReviews() {
        statsReviewLayout.removeAllViews()
        progressBarReview.visibility = View.VISIBLE
        viewModel.updateAvgReviewStatsMonthData()
    }

    private fun resetViews() {
        statsOccupancyLayout?.removeAllViews()
        statsReviewLayout?.removeAllViews()
        progressBarOccupancy?.visibility = View.VISIBLE
        progressBarReview?.visibility = View.VISIBLE
    }
}
