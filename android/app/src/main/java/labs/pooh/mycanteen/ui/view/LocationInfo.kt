package labs.pooh.mycanteen.ui.view

import labs.pooh.mycanteen.R
import org.osmdroid.events.MapListener
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.OverlayWithIW
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow

class LocationInfo(mapView: MapView) : MarkerInfoWindow(R.layout.map_item_view, mapView) {

    companion object {
        private var openedInfo: LocationInfo? = null
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