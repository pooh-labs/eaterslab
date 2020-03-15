package labs.pooh.mycanteen.ui.view

import android.animation.AnimatorListenerAdapter
import android.view.View

fun View.rotate(rotateBy: Int = 360, duration: Int = 700) {
    this.animate()
        .setDuration(duration.toLong())
        .rotationBy(rotateBy.toFloat())
}

