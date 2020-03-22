package labs.pooh.eaterslab

import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import kotlin.math.max

abstract class AbstractRevealedActivity : AppCompatActivity() {

    abstract val revealedLayout: Lazy<View>

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)

        if (savedInstanceState == null) {
            revealedLayout.value.visibility = View.INVISIBLE

            val viewTreeObserver = revealedLayout.value.viewTreeObserver
            if (viewTreeObserver.isAlive) {
                viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        revealActivityAnimation(revealedLayout.value)
                        revealedLayout.value.viewTreeObserver.removeOnGlobalLayoutListener(this)
                    }
                })
            }
        }
    }

    private val revealAnimationTime = 800L

    /**
     * Specify the x position to start the reveal activity animation point
     */
    abstract fun getXStartPosition(): Int

    /**
     * Specify the y position to start the reveal activity animation point
     */
    abstract fun getYStartPosition(): Int

    private fun revealActivityAnimation(view: View) {
        val finalRadius = max(view.width, view.height)

        val circularReveal = ViewAnimationUtils.createCircularReveal(
            view,
            getXStartPosition(),
            getYStartPosition(),
            0F, finalRadius.toFloat())

        circularReveal.duration = revealAnimationTime
        view.visibility = View.VISIBLE
        circularReveal.start()
    }
}
