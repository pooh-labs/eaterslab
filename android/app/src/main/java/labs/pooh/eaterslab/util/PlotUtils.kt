package labs.pooh.eaterslab.util

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.FrameLayout
import io.data2viz.axis.Orient
import io.data2viz.axis.axis
import io.data2viz.color.Colors
import io.data2viz.geom.Size
import io.data2viz.scale.FirstLastRange
import io.data2viz.scale.Scales
import io.data2viz.scale.Tickable
import io.data2viz.viz.*

data class PlotProportion(val x: Double, val y: Double)

private fun GroupNode.moveRightTransform(padding: Double, width: Double, index: Int, height: Double, labelSize: Double, space: Double = 0.0) {
    transform {
        translate(
            x = padding + index * (width + space),
            y = height + padding + labelSize
        )
    }
}

fun <T: Number> Context.horizontalBarPlot(data: List<T>, barColor: Int,
                                  lowValueFix: Double = 1.0, proportion: PlotProportion = PlotProportion(2.0, 1.0), plotLabels: Boolean = true,
                                  barWidth: Double = 10.0, labelColor: Int = Color.BLACK, padding: Double = 5.0, labelSize: Double = 9.0,
                                  ticks: List<String> = listOf(), ticksIndexer: ((String) -> Int)? = null, ticksScale: Double = 4.0): View {

    val doubleData = data.map(Number::toDouble)
    assert(doubleData.isNotEmpty()) // TODO: handle empty plot

    val printTicks = ticks.isNotEmpty() && ticksIndexer != null

    val w = doubleData.size * (barWidth + padding) + padding
    val h = proportion.y / proportion.x * w - 2 * padding - labelSize

    val wTicks = (ticks.size * (barWidth + padding) + padding) * ticksScale
    val hTicks = wTicks * proportion.y  / proportion.x
    val paddingTicks = wTicks * (padding + barWidth / 2) / ((data.size * barWidth) + ((data.size + 1) * padding))

    val yScale = Scales.Continuous.linear {
        domain = listOf(doubleData.min()!! - lowValueFix, doubleData.max()!!)
        range = listOf(0.0, proportion.y)
    }

    val plotView = viz {

        size = Size(w, h)

        doubleData.forEachIndexed { index, value ->
            group {
                moveRightTransform(padding, barWidth, index, h, labelSize, padding)
                rect {
                    vAlign = TextVAlign.BASELINE
                    width = barWidth
                    height = (-1.0) * (h * yScale(value))
                    fill = Colors.rgb(barColor)
                }
            }
        }

        if (plotLabels) {
            doubleData.forEachIndexed { index, value ->
                group {
                    moveRightTransform(padding, barWidth, index, h, labelSize, padding)
                    text {
                        textContent = String.format("%.0f", value)
                        fontSize = labelSize
                        x = barWidth / 2
                        y = (-1.0) * (h * yScale(value)) - (labelSize / 2)
                        vAlign = TextVAlign.BASELINE
                        hAlign = TextHAlign.MIDDLE
                        textColor = Colors.rgb(labelColor)

                    }
                }
            }
        }
    }.toView(this)

    if (!printTicks) {
        return plotView
    }

    val ticksView = viz {
        size = Size(wTicks, hTicks)

        group {
            transform {
                translate(y = hTicks / 2)
            }
            axis(Orient.BOTTOM, FirstLastRangeTickable(ticks, wTicks, paddingTicks, ticksIndexer!!))
        }
    }.toView(this)

    return FrameLayout(this).apply {
        addView(plotView)
        addView(ticksView)
    }
}

fun <T: Number> Context.discreteLinePlot(data: List<T>, barColor: Int,
                                 lowValueFix: Double = 1.0, proportion: PlotProportion = PlotProportion(2.0, 1.0),
                                 barWidth: Double = 10.0, padding: Double = 5.0, labelSize: Double = 9.0): View {

    val doubleData = data.map(Number::toDouble)
    assert(doubleData.isNotEmpty()) // TODO: handle empty plot

    val w = doubleData.size * barWidth + padding
    val h = proportion.y / proportion.x * w - 2 * padding - labelSize

    val yScale = Scales.Continuous.linear {
        domain = listOf(doubleData.min()!! - lowValueFix, doubleData.max()!!)
        range = listOf(0.0, proportion.y)
    }

    return viz {
        size = Size(w, h)
        doubleData.zipWithNext().forEachIndexed { index, value ->
            group {
                moveRightTransform(padding + barWidth / 4, barWidth, index, h, labelSize)
                line {
                    x1 = 0.0
                    y1 = (-1.0) * (h * yScale(value.first))
                    x2 = barWidth
                    y2 = (-1.0) * (h * yScale(value.second))
                    hAlign = TextHAlign.MIDDLE
                    vAlign = TextVAlign.MIDDLE
                    strokeWidth = 10.0
                    fill = Colors.rgb(barColor)
                    stroke = Colors.rgb(barColor)
                }
            }
        }

    }.toView(this)
}

class FirstLastRangeTickable<T>(private val ticksValues: List<T>,
                                val widthWithPadding: Double, val padding: Double,
                                val ticksIndexer: (T) -> Int): Tickable<T>, FirstLastRange<T, Double> {

    override fun ticks(count: Int) = ticksValues

    override fun end() = widthWithPadding - padding

    override fun start() = padding

    override fun invoke(domainValue: T): Double {
        val index = ticksIndexer(domainValue)
        val ticks = ticksValues.size
        if (ticks < 2) {
            return widthWithPadding / 2
        }

        val distance = (widthWithPadding - 2 * padding) / (ticks - 1)
        return index * distance + padding
    }
}
