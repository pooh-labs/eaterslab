package labs.pooh.eaterslab.ui.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.os.Handler
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import kotlinx.android.synthetic.main.activity_hello_select.*
import kotlin.math.abs
import kotlin.time.Duration


fun View.rotateAnimation(rotateBy: Int = 360, duration: Int = 700) {
    this.animate()
        .setDuration(duration.toLong())
        .rotationBy(rotateBy.toFloat())
}

internal fun View.moveYAndHide(yMove: Int? = null, duration: Int) {
    val y = yMove?.toFloat() ?: height.toFloat()

    this.animate()
        .translationY(y)
        .alpha(0.0F)
        .setDuration(duration.toLong())
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                super.onAnimationEnd(animation)
                this@moveYAndHide.visibility = GONE
            }
        })
}

fun View.moveDownAndHide(yMove: Int? = null, duration: Int = 300)
        = moveYAndHide(yMove?.let { abs(it) }, duration)

internal fun View.moveYAndShow(yMove: Int? = null, duration: Int) {
    visibility = INVISIBLE
    val y = -(yMove?.toFloat() ?: height.toFloat())

    this.animate()
        .translationY(y)
        .alpha(100.0F)
        .setDuration(duration.toLong())
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationStart(animation: Animator) {
                super.onAnimationEnd(animation)
                this@moveYAndShow.visibility = View.VISIBLE
            }
        })
}

fun View.moveUpAndShow(yMove: Int? = null, duration: Int = 400)
        = moveYAndShow(yMove?.let { abs(it) }, duration)

fun View.bounceAndScaleDelayed(activity: Activity, delay: Long = 500, startScale: Float = 0.3f) {
    val finishY = y
    animate().y(- 2 * y).scaleX(startScale).scaleY(startScale).start()

    Handler().postDelayed({
        val firstYAnim = SpringAnimation(this, DynamicAnimation.TRANSLATION_Y, 0f).apply {
            spring.dampingRatio = SpringForce.DAMPING_RATIO_HIGH_BOUNCY
            spring.stiffness = SpringForce.STIFFNESS_LOW
        }

        activity.runOnUiThread {
            firstYAnim.animateToFinalPosition(finishY)
            animate().scaleX(1.0f).scaleY(1.0f).start()
        }
    }, delay)
}
