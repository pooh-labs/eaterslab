package labs.pooh.mycanteen.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

internal const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION

fun hasPermission(context: Context, permission: String): Boolean
        = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

fun Activity.requestListedPermission(activity: Activity, requestCode: Int, vararg permissions: String)
        = ActivityCompat.requestPermissions(activity, arrayOf(*permissions), requestCode)

fun allRequestedPermissionsGranted(grantResults: IntArray)
        = grantResults.none { it == PackageManager.PERMISSION_DENIED }