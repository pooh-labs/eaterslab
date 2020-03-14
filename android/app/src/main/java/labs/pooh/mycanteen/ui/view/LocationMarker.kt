package labs.pooh.mycanteen.ui.view

import androidx.appcompat.content.res.AppCompatResources.getDrawable
import labs.pooh.mycanteen.R
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class LocationMarker(mapView: MapView, title: String, val description: String, position: GeoPoint) : Marker(mapView) {

    init {
        this.infoWindow = LocationInfo(mapView)
        this.title = title
        this.snippet = description
        this.icon = getDrawable(mapView.context, R.drawable.ic_location)
        this.position = position
        setVisible(true)
        setPanToView(true)
    }
}