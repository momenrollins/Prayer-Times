package com.momen.prayerstimes.ui.qibla

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.momen.prayerstimes.R
import com.momen.prayerstimes.databinding.ActivityMapsBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private val mapViewModel: MapViewModel by viewModels()
    private val kaabaLatLng = LatLng(21.4225, 39.8262)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapViewModel.qiblaDirection.observe(this) {
            drawQiblaDirectionLine()
        }

        if (checkLocationPermission()) {
            fetchUserLocation()
        } else {
            requestLocationPermission()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
    }

    private fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestLocationPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSIONS_REQUEST_LOCATION
        )
    }

    private fun fetchUserLocation() {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        if (checkLocationPermission()) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val userLatLng = LatLng(location.latitude, location.longitude)
                    mMap.addMarker(
                        MarkerOptions()
                            .position(userLatLng)
                            .title("Current Location")
                    )

                    mapViewModel.fetchQiblaDirection(location.latitude, location.longitude)
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun drawQiblaDirectionLine() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val userLatLng = LatLng(location.latitude, location.longitude)

                // Draw a polyline from user location to Kaaba
                mMap.addPolyline(
                    PolylineOptions()
                        .add(userLatLng, kaabaLatLng)
                        .width(5f)
                        .color(Color.RED)
                )

                mMap.addMarker(
                    MarkerOptions()
                        .position(kaabaLatLng)
                        .title("Kaaba")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.kabaa))
                )
                val bounds = LatLngBounds.builder()
                    .include(userLatLng)
                    .include(kaabaLatLng)
                    .build()
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))

            }
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_LOCATION = 1001
    }
}



