package labs.pooh.eaterslab

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Space
import androidx.core.content.ContextCompat
import androidx.core.view.plusAssign
import kotlinx.android.synthetic.main.activity_plot.*
import labs.pooh.eaterslab.util.horizontalBarPlot
import labs.pooh.eaterslab.util.linePlot
import kotlin.math.abs
import kotlin.math.log
import kotlin.math.sin

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
            val barView = horizontalBarPlot(this, dataSet, red)
            val plotView = linePlot(this, dataSet, yellow)
            frame += barView
            frame += plotView
            frame.layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 600)

            linearLayout += frame
            linearLayout += Space(this).apply {
                layoutParams = ViewGroup.LayoutParams(0, 50)
            }
        }
    }
}
