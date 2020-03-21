package labs.pooh.eaterslab.ui.map

import androidx.appcompat.content.res.AppCompatResources.getDrawable
import labs.pooh.eaterslab.R
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class LocationOccupancyMarker(mapView: MapView, title: String, val id: Int, val description: String,
                              val latitude: Double, val longitude: Double, val occupancy: Int,
                              private val onMarkerClickListener: (LocationOccupancyMarker.() -> Boolean)? = null) : Marker(mapView) {

    init {
        this.infoWindow = SingleLocationInfo(mapView)
            .apply {
            progress = occupancy
        }
        this.title = title
        this.snippet = description
        this.subDescription = mResources.getString(R.string.occupancy)
        this.icon = getDrawable(mapView.context, R.drawable.ic_location)
        this.position = GeoPoint(latitude, longitude)
        setVisible(true)
        setPanToView(true)
    }

    override fun onMarkerClickDefault(marker: Marker, mapView: MapView): Boolean {
        val superResult = super.onMarkerClickDefault(marker, mapView)

        val locationMarker = (marker as? LocationOccupancyMarker)
        return locationMarker?.let {  mark ->
            onMarkerClickListener?.invoke(mark) ?: superResult
        } ?: superResult
    }
}
