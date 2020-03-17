package labs.pooh.mycanteen.ui.view

import android.os.ParcelFileDescriptor
import kotlinx.android.synthetic.main.map_item_view.view.*
import labs.pooh.mycanteen.R
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

class SingleLocationInfo(mapView: MapView, private val onCloseListener: (() -> Unit)? = null) : MarkerInfoWindow(R.layout.map_item_view, mapView) {

    companion object {
        private var openedInfo: SingleLocationInfo? = null

        fun closeOpened() = openedInfo?.close()
    }

    init {
        // mark it as it wouldn't be clicked so other markers under it can be clocked through it
        this.mView.setOnTouchListener { _, _ -> false }
    }


    var progress: Int = 0
    get() {
        return mView.progressBar.progress
    }
    set(value) {
        field = value
        mView.progressBar.progress = value
    }

    override fun onOpen(item: Any?) {
        closeOpened()
        super.onOpen(item)
        openedInfo = this
    }

    override fun onClose() {
        super.onClose()
        openedInfo = null
        onCloseListener?.let { it() }
    }
}