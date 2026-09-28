package com.example.tadaassignment

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.tadaassignment.presentation.map.TopSafeAreaApp
import com.example.tadaassignment.ui.theme.StatusBarNavyDark
import com.example.tadaassignment.ui.theme.TADAAssignmentTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val fusedLocationClient by lazy { LocationServices.getFusedLocationProviderClient(this) }

    // Holds the user's current location once permission is granted and the lookup completes.
    private var currentLocation by mutableStateOf<LatLng?>(null)
    private var hasLocationPermission by mutableStateOf(false)

    private val requestLocationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasLocationPermission = isGranted
        if (isGranted) fetchCurrentLocation()
        // Per spec, we don't need to handle the denied case.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setUpSystemBarColors()

        hasLocationPermission = isPermissionGranted()
        if (hasLocationPermission) {
            fetchCurrentLocation()
        } else {
            requestLocationPermission.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        setContent {
            TADAAssignmentTheme {
                TopSafeAreaApp(
                    initialCameraLocation = currentLocation,
                    hasLocationPermission = hasLocationPermission
                )
            }
        }
    }

    /**
     * The status bar uses a slightly darker shade than the app's navy top
     * bar (the standard colorPrimary/colorPrimaryDark relationship), and the
     * nav bar matches the app's white bottom bar - so both system bars stay
     * visually related to their adjacent Compose bar without being identical.
     */
    private fun setUpSystemBarColors() {
        window.statusBarColor = StatusBarNavyDark.toArgb()
        window.navigationBarColor = AndroidColor.WHITE
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = false // navy background -> light icons
            isAppearanceLightNavigationBars = true // white background -> dark icons
        }
    }

    private fun isPermissionGranted(): Boolean = ActivityCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    // Permission is checked right above every call site, but the compiler
    // can't see that, so we suppress the missing-permission lint warning.
    @SuppressLint("MissingPermission")
    private fun fetchCurrentLocation() {
        if (!isPermissionGranted()) return

        // lastLocation is often null on a cold start / emulator with no cached
        // fix, so fall back to actively requesting a fresh one.
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = LatLng(location.latitude, location.longitude)
            } else {
                requestFreshLocation()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestFreshLocation() {
        if (!isPermissionGranted()) return

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            CancellationTokenSource().token
        ).addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = LatLng(location.latitude, location.longitude)
            }
        }
    }
}
