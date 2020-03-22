package labs.pooh.eaterslab

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Space
import androidx.core.content.ContextCompat
import androidx.core.view.plusAssign
import io.data2viz.axis.Orient
import io.data2viz.axis.axis
import io.data2viz.color.Colors
import io.data2viz.geom.Point
import io.data2viz.geom.Size
import io.data2viz.scale.Scales
import io.data2viz.viz.Margins
import io.data2viz.viz.toView
import io.data2viz.viz.viz
import kotlinx.android.synthetic.main.activity_plot.*
import labs.pooh.eaterslab.util.*
import kotlin.math.*

class PlotActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plot)

        val red = ContextCompat.getColor(this, R.color.colorAccent)
        val yellow = ContextCompat.getColor(this, R.color.colorPrimaryDark)

        val data = listOf(
            List(30) { it + sin(it.toDouble()) },
            List(7) { 3 + sin(it.toDouble()) },
            List(13) { abs(6 - it).toDouble() },
            List(24) { if (it in (8..16)) (0..30).random().toDouble() else 0.0 },
            List(13) { 12 - abs(6 - it).toDouble() },
            List(10) { log(1.0 + it, 2.0) },
            List(51) { sin(abs(it - 25).toDouble() + 0.001) / (abs(it - 25)  + 0.001) }
        )

        data.forEach { dataSet ->
            val frame = FrameLayout(this)
            val barView = when(dataSet.size) {
                24 -> horizontalBarPlot(dataSet, red, ticks = getOrderedHours(), ticksIndexer = ::hoursIndexer, ticksScale = 2.0)
                30 -> horizontalBarPlot(dataSet, red, ticks = getOrderedMonthDays(4), ticksIndexer = ::monthDaysIndexer, ticksScale = 1.5)
                7 -> horizontalBarPlot(dataSet, red, ticks = getOrderedWeekDays(), ticksIndexer = this::weekDaysIndexer)
                else -> horizontalBarPlot(dataSet, red)
            }
            val plotView = discreteLinePlot(dataSet, yellow)
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

        run {
            val frame = FrameLayout(this)
            frame += funcPlot.toView(this)
            frame.layoutParams = FrameLayout.LayoutParams(1000, 500).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }

            linearLayout += frame
            linearLayout += Space(this).apply {
                layoutParams = ViewGroup.LayoutParams(0, 50)
            }
        }
    }
}


var superscript = "⁰¹²³⁴⁵⁶⁷⁸⁹"

val margins = Margins(40.5, 30.5, 50.5, 50.5)

val chartWidth = 960.0 - margins.hMargins
val chartHeight = 500.0 - margins.vMargins


// linear scale domain 0..100 is mapped to 0..width
val xScale = Scales.Continuous.linear {
    domain = listOf(.0, 100.0)
    range = listOf(.0, chartWidth)
}

// log scale
val yScale = Scales.Continuous.log(E) {
    domain = listOf(kotlin.math.exp(0.0), kotlin.math.exp(9.0))
    range = listOf(chartHeight, .0) // <- y is mapped in the reverse order (in SVG, javafx (0,0) is top left.
}

// the mathematical function
val functionToPlot = { x:Double -> x * x + x + 1}

//100  points to define the curve
val points = (0 until 100).map { i -> Point(i.toDouble(), functionToPlot(i.toDouble())) }


val funcPlot = viz {
    size = Size(1000.0, 500.0)

    group {
        transform {
            translate(x = margins.left, y = margins.top)
        }

        group {
            transform {
                translate(x = -10.0)
            }
            axis(Orient.LEFT, yScale) {
                tickFormat = { "e${superscript[round(ln(it)).toInt()]}" } // <- specific formatter to add exponents (ex: e¹)
            }
        }

        group {
            transform {
                translate(y = chartHeight + 10.0)
            }
            axis(Orient.BOTTOM, xScale)
        }

        group {
            path {
                fill = null
                stroke = Colors.Web.steelblue
                strokeWidth = 1.5

                moveTo(xScale(points[0].x), yScale(points[0].y))
                (1 until 100).forEach {
                    lineTo(xScale(points[it].x), yScale(points[it].y))
                }
            }
        }
    }
}
