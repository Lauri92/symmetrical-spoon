package fi.lauriari.ar_project.Fragments

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.gms.location.*
import fi.lauriari.ar_project.R
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import java.util.*
import kotlin.collections.ArrayList

class GameMapFragment : Fragment() {

    private lateinit var fusedLocationClient:
            FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var view: ConstraintLayout
    private lateinit var map: MapView
    private lateinit var ownLocationmarker: Marker
    private var isMapSet: Boolean = false
    private var isInteractionsLocationsSet: Boolean = false
    private var activeDestination = Location("activeDestination")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        Log.d("test", "onCreateView called")

        requestPermissions()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_game_map, container, false) as ConstraintLayout
        map = view.findViewById<MapView>(R.id.map)
        ownLocationmarker = Marker(map)

        setMap(GeoPoint(60.2238005, 24.7589279), firstcall = true)


        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                Log.d("mapDBG", locationResult.lastLocation.toString())

                for ((index, location) in locationResult.locations.withIndex()) {
                    Log.d(
                        "mapDBG",
                        "index: $index new location latitude:${location.latitude} and longitude:${location.longitude}"
                    )

                    val geoPoint = GeoPoint(location.latitude, location.longitude)
                    if (!isMapSet) {
                        setMap(geoPoint)
                    }
                    updateUserLocation(geoPoint)
                    if (!isInteractionsLocationsSet) {
                        setInteractionLocations(geoPoint)
                        isInteractionsLocationsSet = !isInteractionsLocationsSet
                    }
                }
            }
        }

        checkSelfPermissions()

        view.findViewById<Button>(R.id.navigate_to_game_AR_btn).setOnClickListener {
            findNavController().navigate(R.id.action_gameMapFragment_to_gameARFragment)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("lifecycle", "ondestroyview")
        fusedLocationClient.removeLocationUpdates(locationCallback)
        activeDestination.latitude = 0.0
        activeDestination.longitude = 0.0
        isInteractionsLocationsSet = false
        isMapSet = false
    }

    /**
     * Request user permissions to access locations
     */
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireContext() as Activity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            0
        )
        // similar for ACCESS_COARSE_LOCATION,


        ActivityCompat.requestPermissions(
            requireContext() as Activity,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
            0
        )

        Log.i(
            "GEOLOCATION",
            "${
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            } ${
                ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.INTERNET
                )
            } ${PackageManager.PERMISSION_GRANTED}"
        )

    }

    /**
     * Check if permissions are granted
     */
    private fun checkSelfPermissions() {
        val locationRequest = LocationRequest
            .create()
            .setInterval(1000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permissions are not granted so requesting of location updates is not possible
            Log.i("test", "failed")
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            //requestPermissions()

        } else {
            // Permissions are granted so start requesting location updates
            Log.i("test", "Passed")
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback, Looper.getMainLooper()
            )

        }
    }

    /**
     * Draw a polygon at a location
     */
    private fun drawPolygon(geoPoint: GeoPoint) {

        map.overlays.forEach {
            if (it is Polygon && it.id == "activityRange") {
                map.overlays.remove(it)
            }
        }

        val geoPoints = ArrayList<GeoPoint>();
        val polygon = Polygon()
        val radius = 10.0
        for (i in 1..360) {
            geoPoints.add(GeoPoint(geoPoint).destinationPoint(radius, i.toDouble()))
        }
        polygon.id = "activityRange"
        Log.d("DBG", geoPoints.toString())
        geoPoints.add(geoPoints[0])
        polygon.points = geoPoints
        //polygon.title = "A sample polygon"
        map.overlays.add(polygon)
    }

    /**
     * Initialize the map values
     */
    private fun setMap(geoPoint: GeoPoint, firstcall: Boolean = false) {
        map.setTileSource(TileSourceFactory.MAPNIK)
        map.setMultiTouchControls(true)
        map.controller.setZoom(13.0)
        map.controller.setCenter(geoPoint)

        // So the map has some initial value for centering before getting location updates, when setMap is called
        // from the callback, map won't be set again, this is done so that the map will center to user
        if (!firstcall) {
            isMapSet = true
        }
    }

    /**
     * Function for creating test marker with hardcoded values
     */
    private fun setTestMarker() {
        val map = view.findViewById<MapView>(R.id.map)
        val testMarker1 = Marker(map)
        testMarker1.icon = AppCompatResources.getDrawable(
            requireContext(),
            R.drawable.ic_baseline_star_24
        )
        testMarker1.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        testMarker1.position = GeoPoint(60.2238005, 24.7589279)
        map?.overlays?.add(testMarker1)
    }

    /**
     * Set locations where it's possible to enter AR event
     */
    private fun setInteractionLocations(geoPoint: GeoPoint) {
        val interactionLocations = ArrayList<GeoPoint>()
        val radius = 5000
        for (i in 1..360) {
            val rnds = (0..radius).random().toDouble()
            interactionLocations.add(
                GeoPoint(geoPoint.latitude, geoPoint.longitude).destinationPoint(
                    rnds,
                    i.toDouble()
                )
            )
        }
        interactionLocations.shuffle()

        //Take first 20 elements of the shuffled arraylist
        val chosenPoints = interactionLocations.take(15)


        chosenPoints.forEach {
            val address = getAddress(it.latitude, it.longitude)
            if(address.contains("Unnamed Road")) {
                return
            }
            val loopMarker = Marker(map)
            loopMarker.icon = AppCompatResources.getDrawable(
                requireContext(),
                R.drawable.ic_baseline_pets_24
            )
            loopMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            loopMarker.position = GeoPoint(it.latitude, it.longitude)

            loopMarker.setOnMarkerClickListener { marker, mapView ->
                Log.d("test", marker.position.latitude.toString())
                activeDestination.latitude = marker.position.latitude
                activeDestination.longitude = marker.position.longitude

                // Change the icon on marker click...
                /*
                marker.icon = AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_baseline_location_on_24
                )
                 */
                // Or draw a Polygon around it
                drawPolygon(GeoPoint(marker.position.latitude, marker.position.longitude))
                getDistanceToMarker(activeDestination)
                map.invalidate()
                marker.showInfoWindow()
                return@setOnMarkerClickListener true
            }

            loopMarker.title = address
            map.overlays.add(loopMarker)
        }
    }

    /**
     * Calculate distance to active destination
     */
    private fun getDistanceToMarker(destinationLocation: Location) {
        val currentLocation = Location("currentLocation")
        currentLocation.latitude = ownLocationmarker.position.latitude
        currentLocation.longitude = ownLocationmarker.position.longitude

        val distance = currentLocation.distanceTo(destinationLocation)

        view.findViewById<TextView>(R.id.textView).text = distance.toString()

        // Set isEnable to true or false
        view.findViewById<Button>(R.id.navigate_to_game_AR_btn).isEnabled = distance < 10

    }

    /**
     * Update user location on map
     */
    private fun updateUserLocation(geoPoint: GeoPoint) {

        if (activeDestination.latitude != 0.0) {
            getDistanceToMarker(activeDestination)
        }

        // Remove the previous ownLocationMarker, own location is updated constantly
        map.overlays.forEach {
            if (it is Marker && it.id == "ownLocationMarker") {
                map.overlays.remove(it)
            }
        }

        Log.d("context", "${requireContext()}")

        ownLocationmarker.id = "ownLocationMarker"
        ownLocationmarker.icon = AppCompatResources.getDrawable(
            requireContext(),
            R.drawable.ic_baseline_person_24
        )
        ownLocationmarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        ownLocationmarker.position = GeoPoint(geoPoint.latitude, geoPoint.longitude)
        /*ownLocationmarker.title = "${
            getAddress(
                location.latitude,
                location.longitude
            )
        } Latitude: ${location.latitude}, Longitude: ${location.longitude} Altitude: ${location.altitude}"

         */
        //ownLocationmarker.closeInfoWindow()
        map.overlays.add(ownLocationmarker)
        //displays the ownLocationmarker as soon as it has been added.
        map.invalidate()
    }

    private fun getAddress(lat: Double, lon: Double): String {
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = geocoder.getFromLocation(lat, lon, 1)
        //Log.d("address", "List info: ${addresses}")
        return addresses[0].getAddressLine(0)
    }
}