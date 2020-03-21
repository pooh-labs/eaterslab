package labs.pooh.eaterslab.util

import android.content.Context
import android.graphics.Color
import android.view.View
import io.data2viz.axis.Orient
import io.data2viz.color.Colors
import io.data2viz.geom.Size
import io.data2viz.scale.Scales
import io.data2viz.viz.*
import io.data2viz.axis.axis
import io.data2viz.scale.FirstLastRange

data class PlotProportion(val x: Double, val y: Double)

private fun GroupNode.moveRightTransform(padding: Double, width: Double, index: Int, height: Double, labelSize: Double, space: Double = 0.0) {
    transform {
        translate(
            x = padding + index * (width + space),
            y = height + padding + labelSize
        )
    }
}

fun <T: Number> horizontalBarPlot(context: Context, data: List<T>, barColor: Int,
                                  lowValueFix: Double = 1.0, proportion: PlotProportion = PlotProportion(2.0, 1.0), plotLabels: Boolean = true,
                                  barWidth: Double = 10.0, labelColor: Int = Color.BLACK, padding: Double = 5.0, labelSize: Double = 9.0): View {

    val doubleData = data.map(Number::toDouble)
    assert(doubleData.isNotEmpty()) // TODO: handle empty plot

    val w = doubleData.size * (barWidth + padding) + padding
    val h = proportion.y / proportion.x * w - 2 * padding - labelSize

    val yScale = Scales.Continuous.linear {
        domain = listOf(doubleData.min()!! - lowValueFix, doubleData.max()!!)
        range = listOf(0.0, proportion.y)
    }

    return viz {

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
    }.toView(context)
}

fun <T: Number> linePlot(context: Context, data: List<T>, barColor: Int,
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
                moveRightTransform(padding, barWidth, index, h, labelSize)
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

    }.toView(context)
}

