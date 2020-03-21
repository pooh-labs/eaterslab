package labs.pooh.eaterslab.ui.view

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import kotlin.math.abs


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
