package com.example.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.io.IOException

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val LOCATION_PERMISSION_REQUEST_CODE = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Check for location permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Enable the location layer
            mMap.isMyLocationEnabled = true

            // Get current location and move camera
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    val currentLocationMarker = mMap.addMarker(
                        MarkerOptions()
                            .position(currentLatLng)
                            .title("Current Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    )
                    if (currentLocationMarker != null) {
                        currentLocationMarker.tag = "CurrentLocationMarker"
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(currentLatLng))
                }
            }

            // Define other locations and add markers and polyline as before
            val chapraLatLng = LatLng(25.7815, 84.7477) // Chapra coordinates
            val patnaLatLng = LatLng(25.5941, 85.1376) // Patna coordinates
            val amnourLatLng = LatLng(25.974308, 84.924067) // Amnour coordinates
            val sivanLatLng = LatLng(25.1337, 85.5300) // Sivan coordinates
            val garkhaLatLng = LatLng(25.2345, 85.7236) // Garkha coordinates

            mMap.addMarker(MarkerOptions().position(chapraLatLng).title("Chapra"))
            mMap.addMarker(MarkerOptions().position(patnaLatLng).title("Patna"))
//            mMap.addMarker(MarkerOptions().position(amnourLatLng).title("Amnour"))
            mMap.addMarker(MarkerOptions().position(sivanLatLng).title("Sivan"))
            mMap.addMarker(MarkerOptions().position(garkhaLatLng).title("Garkha"))

            val polylineOptions = PolylineOptions()
                .add(chapraLatLng)
                .add(LatLng(25.7815, 84.8576))
                .add(LatLng(25.7753, 84.8636))
                .add(patnaLatLng)
                .add(amnourLatLng)
                .add(sivanLatLng)
                .add(garkhaLatLng)
                .width(10f)
                .color(resources.getColor(R.color.colorPolyline))

            mMap.addPolyline(polylineOptions)

            // Set up marker click listener
             mMap.setOnMarkerClickListener { marker ->
                if (marker.tag == "CurrentLocationMarker") {
                    // Display an alert dialog with current location data
                    val geocoder = Geocoder(this)
                    try {
                        val addresses = geocoder.getFromLocation(marker.position.latitude, marker.position.longitude, 1)
                        val address = addresses?.firstOrNull()
                        val addressText = address?.getAddressLine(0) ?: "Address not found"


                        val alertDialog = AlertDialog.Builder(this)
                            .setTitle("Current Location")
                            .setMessage("Address: $addressText\nLatitude: ${marker.position.latitude}, Longitude: ${marker.position.longitude}")
                            .setPositiveButton("OK") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                        alertDialog.show()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    true // Consume the event
                } else {
                    false // Let the default behavior occur for other markers
                }
            }
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, check location permission again to refresh the map
                checkLocationPermission()
            }
        }
    }

    private fun checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, call onMapReady to refresh the map
            onMapReady(mMap)
        }
    }
}
