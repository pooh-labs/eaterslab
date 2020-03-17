package labs.pooh.mycanteen.ui.view

import android.view.MotionEvent
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Overlay

class TransparentListenerOverlay(private val listener: MapView.() -> Unit) : Overlay() {

    override fun onSingleTapConfirmed(e: MotionEvent?, mapView: MapView?): Boolean {
        mapView?.let { listener(it) }
        return true
    }
}