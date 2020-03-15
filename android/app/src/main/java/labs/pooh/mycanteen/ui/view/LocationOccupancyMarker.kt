package labs.pooh.mycanteen.ui.view

import android.provider.Settings.Global.getString
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import labs.pooh.mycanteen.R
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class LocationOccupancyMarker(mapView: MapView, title: String, val description: String, position: GeoPoint, val occupancy: Int) : Marker(mapView) {

    init {
        this.infoWindow = LocationInfo(mapView).apply {
            progress = occupancy
        }
        this.title = title
        this.snippet = description
        this.subDescription = mResources.getString(R.string.occupancy)
        this.icon = getDrawable(mapView.context, R.drawable.ic_location)
        this.position = position
        setVisible(true)
        setPanToView(true)
    }
}