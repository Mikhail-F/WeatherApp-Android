package com.example.watherapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.FragmentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource

class LocationService {
    private fun getLocation(request: (input: String) -> Unit, context: Context) {

        val ct = CancellationTokenSource()
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        val fLocationClient = LocationServices.getFusedLocationProviderClient(context)
        fLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, ct.token)
            .addOnCompleteListener {
                request("${it.result.latitude}, ${it.result.longitude}")
            }
    }

    private fun isLocationEnabled(activity: FragmentActivity?): Boolean {
        val lm = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

     fun checkLocation(request: (input: String) -> Unit, context: Context, activity: FragmentActivity?) {
        if (isLocationEnabled(activity)) {
            getLocation(request, context)
        } else {
            DialogManager.locationSettingsDialog(context, object : DialogManager.Listener {
                override fun onClick(name: String?) {
                    activity?.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                }
            })
        }
    }
}