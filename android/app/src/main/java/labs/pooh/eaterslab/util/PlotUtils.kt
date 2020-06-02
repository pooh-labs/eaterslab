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
import org.threeten.bp.OffsetDateTime

import labs.pooh.eaterslab.R

data class PlotProportion(val x: Double, val y: Double)

private fun GroupNode.moveTransform(padding: Double, width: Double, index: Int, height: Double, labelSize: Double, space: Double = 0.0) {
    transform {
        translate(
            x = padding + index * (width + space),
            y = height + padding + labelSize
        )
    }
}

abstract class AbstractStandardPlot<D>(protected val context: Context) {

    var proportion = PlotProportion(2.0, 1.0)
    var barWidth = 10.0
    var lowValueFix = 1.0
    var padding = 5.0
    var labelSize = 4.0

    abstract fun plot(data: List<D>, mainColor: Int, dataMap: (D) -> Number): View
}

open class HorizontalBarPlot<D>(context: Context) : AbstractStandardPlot<D>(context) {

    var ticksScale = 4.0
    var ticksDistance = 0.5
    var labelColor = Color.BLACK
    var plotLabels = true
    var ticksMap: ((D, Int) -> PrintableDateTimeTicks)? = null
    var printTicks = true

    override fun plot(data: List<D>, mainColor: Int, dataMap: (D) -> Number): View {

        val doubleData = data.map(dataMap).map { it.toDouble() }
        if (data.isEmpty()) {
            return FrameLayout(context)
        }

        val printTicks = ticksMap != null

        val w = doubleData.size * (barWidth + padding) + padding
        val h = proportion.y / proportion.x * w - 2 * padding - labelSize

        val wTicks = (doubleData.size * (barWidth + padding) + padding) * ticksScale
        val hTicks = (wTicks * proportion.y  / proportion.x)
        val paddingTicks = wTicks * (padding + barWidth / 2) / ((data.size * barWidth) + ((data.size + 1) * padding))

        val yScale = Scales.Continuous.linear {
            domain = listOf(doubleData.min()!! - lowValueFix, doubleData.max()!!)
            range = listOf(0.0, h)
        }

        val plotView = viz {

            size = Size(w, h)

            doubleData.forEachIndexed { index, value ->
                group {
                    moveTransform(padding, barWidth, index, h, labelSize, padding)
                    rect {
                        vAlign = TextVAlign.BASELINE
                        width = barWidth
                        height = (-1.0) * (yScale(value))
                        fill = Colors.rgb(mainColor)
                    }
                }
            }

            if (plotLabels) {
                doubleData.forEachIndexed { index, value ->
                    group {
                        moveTransform(padding, barWidth, index, h, labelSize, padding)
                        text {
                            textContent = String.format("%.0f", value)
                            fontSize = labelSize
                            x = barWidth / 2
                            y = (-1.0) * (yScale(value)) - (labelSize / 2)
                            vAlign = TextVAlign.BASELINE
                            hAlign = TextHAlign.MIDDLE
                            textColor = Colors.rgb(labelColor)
                        }
                    }
                }
            }
        }.toView(context)

        if (!printTicks || !this.printTicks) {
            return plotView
        }

        val ticks = data.mapIndexed { index, value -> ticksMap!!(value, index) }

        val ticksView = viz {
            size = Size(wTicks, hTicks * (1 + ticksDistance))

            group {
                transform {
                    translate(y = hTicks)
                }
                axis(Orient.BOTTOM, FirstLastRangeTickable(ticks, wTicks, paddingTicks) { it.index })
            }
        }.toView(context)

        return FrameLayout(context).apply {
            addView(plotView)
            addView(ticksView)
        }
    }
}

open class DiscreteLinePlot<D>(context: Context) : AbstractStandardPlot<D>(context) {

    var ticksScale = 4.0
    var ticksDistance = 0.5
    var ticksMap: ((D, Int) -> PrintableDateTimeTicks)? = null
    var printTicks = true

    override fun plot(data: List<D>, mainColor: Int, dataMap: (D) -> Number): View {

        val doubleData = data.map(dataMap).map { it.toDouble() }
        if (data.isEmpty()) {
            return FrameLayout(context)
        }

        val printTicks = ticksMap != null

        val wTicks = (doubleData.size * (barWidth + padding) + padding) * ticksScale
        val hTicks = (wTicks * proportion.y  / proportion.x)
        val paddingTicks = wTicks * (padding + barWidth / 2) / ((data.size * barWidth) + ((data.size + 1) * padding))


        val w = doubleData.size * barWidth + padding
        val h = proportion.y / proportion.x * w - 2 * padding - labelSize

        val yScale = Scales.Continuous.linear {
            domain = listOf(doubleData.min()!! - lowValueFix, doubleData.max()!!)
            range = listOf(0.0, proportion.y)
        }

        val plotView = viz {
            size = Size(w, h)
            doubleData.zipWithNext().forEachIndexed { index, value ->
                group {
                    moveTransform(padding + barWidth / 4, barWidth, index, h, labelSize)
                    line {
                        x1 = 0.0
                        y1 = (-1.0) * (h * yScale(value.first))
                        x2 = barWidth
                        y2 = (-1.0) * (h * yScale(value.second))
                        hAlign = TextHAlign.MIDDLE
                        vAlign = TextVAlign.MIDDLE
                        strokeWidth = 10.0
                        fill = Colors.rgb(mainColor)
                        stroke = Colors.rgb(mainColor)
                    }
                }
            }

        }.toView(context)

        if (!printTicks || !this.printTicks) {
            return plotView
        }

        val ticks = data.mapIndexed { index, value -> ticksMap!!(value, index) }

        val ticksView = viz {
            size = Size(wTicks, hTicks * (1 + ticksDistance))

            group {
                transform {
                    translate(y = hTicks)
                }
                axis(Orient.BOTTOM, FirstLastRangeTickable(ticks, wTicks, paddingTicks) { it.index })
            }
        }.toView(context)

        return FrameLayout(context).apply {
            addView(plotView)
            addView(ticksView)
        }
    }
}

sealed class PrintableDateTimeTicks(val index: Int, val dateTime: OffsetDateTime, protected val context: Context) {
    abstract override fun toString(): String
}

class HourTicks(index: Int, dateTime: OffsetDateTime, context: Context)
    : PrintableDateTimeTicks(index, dateTime, context) {
    override fun toString() = "${dateTime.hour}:00"
}

class WeekDayTicks(index: Int, dateTime: OffsetDateTime, context: Context)
    : PrintableDateTimeTicks(index, dateTime, context) {
    override fun toString() = when(dateTime.dayOfWeek.value) {
         in 1..7 -> context.getOrderedWeekDays()[dateTime.dayOfWeek.value - 1]
        else -> context.getString(R.string.noday)
    }
}

class MonthDayTicks(index: Int, dateTime: OffsetDateTime, context: Context)
    : PrintableDateTimeTicks(index, dateTime, context) {
    override fun toString() = "${dateTime.dayOfMonth}"
}

private class FirstLastRangeTickable<T>(private val ticksValues: List<T>,
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
