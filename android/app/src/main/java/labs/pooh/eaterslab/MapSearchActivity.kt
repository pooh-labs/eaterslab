package labs.pooh.eaterslab

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_map_search.*
import labs.pooh.client.apis.CafeteriaApi
import labs.pooh.client.models.Cafeteria
import labs.pooh.eaterslab.HelloSelectActivity.Companion.BUTTON_MAP_POSITION_X
import labs.pooh.eaterslab.HelloSelectActivity.Companion.BUTTON_MAP_POSITION_Y
import labs.pooh.eaterslab.ui.map.LocationOccupancyMarker
import labs.pooh.eaterslab.ui.map.SingleLocationInfo
import labs.pooh.eaterslab.ui.map.TransparentListenerOverlay
import labs.pooh.eaterslab.ui.view.*
import labs.pooh.eaterslab.util.*
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class MapSearchActivity : AbstractRevealedActivity() {

    companion object {
        const val DEFAULT_ZOOM = 16.0
        const val MIN_ZOOM = 7.0
        const val MAX_ZOOM = 19.0

        const val ZOOM_ANIMATION_TIME: Long = 400
        val DEFAULT_CENTER_LOCATION = GeoPoint(52.211903, 20.982224)

        private const val REQUEST_LOCATION_ON_BUTTON_CODE = 1001
    }

    private lateinit var rotationGestureOverlay: RotationGestureOverlay
    private lateinit var myLocationOverlay: MyLocationNewOverlay

    private lateinit var gpsProvider: GpsMyLocationProvider

    private lateinit var API: CafeteriaApi

    /**
     * Resources that should be disposed when the fragment is destroyed
     */
    private val compositeDisposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        API = CafeteriaApi(getString(R.string.api_url))

        Configuration.getInstance().load(applicationContext, PreferenceManager.getDefaultSharedPreferences(applicationContext))
        setContentView(R.layout.activity_map_search)

        buttonSelect.moveDownAndHide()
        gpsProvider = GpsMyLocationProvider(applicationContext)

        with(map) {
            setTileSource(TileSourceFactory.MAPNIK)

            rotationGestureOverlay = configureZoomOverlay()
            myLocationOverlay = configureGPSOverlay(applicationContext, gpsProvider)

            // this layer should be created BEFORE markers overlay in order
            // to firstly get the listeners on markers and if none touched then
            // the touch event goes to transparent layer listener
            overlays += TransparentListenerOverlay {
                SingleLocationInfo.closeOpened()
                buttonSelect.moveDownAndHide()
            }

            controller.setCenter(DEFAULT_CENTER_LOCATION)
            controller.setZoom(DEFAULT_ZOOM)
        }

        fabGPS.setOnClickListener(this@MapSearchActivity::fabGPSListener)
        map.addPlacesInBackground()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode) {
            REQUEST_LOCATION_ON_BUTTON_CODE -> {
                if (allRequestedPermissionsGranted(grantResults)) {
                    map?.controller?.let { setLocationToGPS(it) }
                }
                else {
                    fabGPS.visibility = GONE
                }
            }
        }
    }

    override val revealedLayout = lazy { rootMapSearchLayout }

    override fun getXStartPosition(): Int = intent.getIntExtra(BUTTON_MAP_POSITION_X, 0)

    override fun getYStartPosition(): Int = intent.getIntExtra(BUTTON_MAP_POSITION_Y, 0)

    override fun onResume() {
        super.onResume()
        map.onResume()
    }

    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    private fun fabGPSListener(view: View) {
        view.rotateAnimation()

        if (!hasPermission(applicationContext, LOCATION_PERMISSION)) {
            requestListedPermission(REQUEST_LOCATION_ON_BUTTON_CODE, LOCATION_PERMISSION)
        }
        else {
            setLocationToGPS(map.controller)
        }
    }

    private fun setLocationToGPS(mapController: IMapController) {
        gpsProvider.lastKnownLocation?.let {
            val locationPoint = GeoPoint(it.latitude, it.longitude)
            mapController.animateTo(locationPoint)
        }
    }

    private fun MapView.configureZoomOverlay(): RotationGestureOverlay {
        val rotationGestureOverlay = RotationGestureOverlay(this)
        rotationGestureOverlay.isEnabled = true
        setMultiTouchControls(true)
        zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
        minZoomLevel = MIN_ZOOM
        maxZoomLevel = MAX_ZOOM

        plus.setOnClickListener {
            if (canZoomIn()) {
                controller.zoomIn(ZOOM_ANIMATION_TIME)
            }
        }

        minus.setOnClickListener {
            if (canZoomOut()) {
                controller.zoomOut(ZOOM_ANIMATION_TIME)
            }
        }

        overlays += rotationGestureOverlay
        return rotationGestureOverlay
    }

    private fun MapView.configureGPSOverlay(context: Context, gpsProvider: IMyLocationProvider): MyLocationNewOverlay {
        val myLocationOverlay = MyLocationNewOverlay(gpsProvider, this)
        myLocationOverlay.enableMyLocation()

        convertDrawableToBitmap(context, R.drawable.ic_current_location)?.let { icon ->
            myLocationOverlay.setPersonIcon(icon)
            myLocationOverlay.enableAutoStop = false
            myLocationOverlay.setPersonHotspot(icon.width / 2F, icon.height / 2F)
            myLocationOverlay.setDirectionArrow(icon, icon)
        }

        overlays += myLocationOverlay
        return myLocationOverlay
    }

    private fun onSelectPlaceButtonClick(marker: LocationOccupancyMarker, view: View) {
        Snackbar.make(view, "You selected ${marker.id} id marker", Snackbar.LENGTH_LONG)
            .setAction("Action", null).show()
    }

    private fun onMarkerClickListener(marker: LocationOccupancyMarker): Boolean {
        buttonSelect.moveUpAndShow(yMove = 0)
        buttonSelect.setOnClickListener { onSelectPlaceButtonClick(marker, it) }
        return true
    }

    private fun MapView.addPlacesInBackground() {

        val disposable = Observable.defer { Observable.just(API.cafeteriaList()) }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .forEach { array ->
                array.forEach { cafeteria ->
                    cafeteria.toMarker(map, this@MapSearchActivity::onMarkerClickListener)
                        ?.let { overlays += it }
                }
            }
        compositeDisposable.add(disposable)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.dispose()
    }
}


fun Cafeteria.toMarker(mapView: MapView, listener: (LocationOccupancyMarker) -> Boolean) = id?.let { existingId ->
    return@let LocationOccupancyMarker(
        mapView,
        title = name, id = existingId,
        description = subDescription,
        latitude = latitude.toDouble(), longitude = longitude.toDouble(),
        occupancy = capacity,
        onMarkerClickListener = listener
    )
}
