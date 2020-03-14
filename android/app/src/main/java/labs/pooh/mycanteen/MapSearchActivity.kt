package labs.pooh.mycanteen

import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_map_search.*
import labs.pooh.mycanteen.ui.view.LocationInfo
import labs.pooh.mycanteen.ui.view.LocationMarker
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MapSearchActivity : AppCompatActivity() {

    companion object {
        const val DEFAULT_ZOOM = 16.0
    }

    private lateinit var rotationGestureOverlay: RotationGestureOverlay
    private lateinit var myLocationOverlay: MyLocationNewOverlay

    private lateinit var gpsProvider: GpsMyLocationProvider

    private val mapItems = mutableListOf<OverlayItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);

        // TODO: handle permission to get access to internet, gps, etc.

        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))
        setContentView(R.layout.activity_map_search)

        with(map) {
            setTileSource(TileSourceFactory.MAPNIK)

            rotationGestureOverlay = RotationGestureOverlay(this)
            rotationGestureOverlay.isEnabled = true
            overlays += rotationGestureOverlay

            val mimMarker = LocationMarker(this, "Kubuś MIMUW", "Szybko i smacznie", GeoPoint(52.211903, 20.982224))
            overlays += mimMarker

            val bioMarker = LocationMarker(this, "Biologia UW", "Wejdź i wyjdź", GeoPoint(52.213231, 20.986494))
            overlays += bioMarker

            setMultiTouchControls(true)

            gpsProvider = GpsMyLocationProvider(applicationContext)
            myLocationOverlay = MyLocationNewOverlay(gpsProvider, this)
            myLocationOverlay.enableMyLocation()
            overlays += myLocationOverlay

            fabMap.setOnClickListener {
                gpsProvider.lastKnownLocation?.let {
                    val locationPoint = GeoPoint(it.latitude, it.longitude)
                    controller.animateTo(locationPoint)
                }
            }

            controller.setCenter(GeoPoint(52.211903, 20.982224))
            controller.setZoom(DEFAULT_ZOOM)
        }
    }

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }
}