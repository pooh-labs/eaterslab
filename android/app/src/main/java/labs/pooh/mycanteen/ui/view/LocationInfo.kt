package labs.pooh.mycanteen.ui.view

import kotlinx.android.synthetic.main.map_item_view.view.*
import labs.pooh.mycanteen.R
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

class LocationInfo(mapView: MapView) : MarkerInfoWindow(R.layout.map_item_view, mapView) {

    companion object {
        private var openedInfo: LocationInfo? = null
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
        openedInfo?.close()
        super.onOpen(item)
        openedInfo = this
    }

    override fun onClose() {
        super.onClose()
        openedInfo = null
    }
}