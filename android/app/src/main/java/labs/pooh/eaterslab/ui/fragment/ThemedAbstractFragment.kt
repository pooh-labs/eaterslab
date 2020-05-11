package labs.pooh.eaterslab.ui.fragment

import android.graphics.Color
import android.util.TypedValue
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import labs.pooh.eaterslab.R
import labs.pooh.eaterslab.ui.activity.abstracts.AbstractThemedActivity

abstract class ThemedAbstractFragment : Fragment() {

    protected fun getPlotFontColorForTheme(): Int {
        val value = TypedValue()
        if (activity?.theme?.resolveAttribute(R.attr.foregroundColorText, value, true) == true) {
            return value.data
        }
        else {
            return Color.BLACK
        }
    }

    val accentColor by lazy { ContextCompat.getColor(requireContext(), R.color.colorAccent) }
    val themeColor by lazy { ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark) }

    protected fun printTicks() = !((activity as? AbstractThemedActivity)?.isDarkModeEnabled() ?: false)
}