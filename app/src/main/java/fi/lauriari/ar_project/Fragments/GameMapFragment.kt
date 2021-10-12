package fi.lauriari.ar_project.Fragments

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
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
import android.view.Window
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.gms.location.*
import fi.lauriari.ar_project.*
import fi.lauriari.ar_project.R
import fi.lauriari.ar_project.viewmodels.InventoryViewModel
import fi.lauriari.ar_project.viewmodels.MapDetailsViewModel
import kotlinx.coroutines.*
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class GameMapFragment : Fragment() {

    private lateinit var fusedLocationClient:
            FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var view: ConstraintLayout
    private lateinit var map: MapView
    private lateinit var ownLocationmarker: Marker
    private lateinit var geocoder: Geocoder
    private var isMapSet: Boolean = false
    private var isInteractionsLocationsSet: Boolean = false
    private var activeDestination = Location("activeDestination")
    private val mMapDetailsViewModel: MapDetailsViewModel by viewModels()
    private val mInventoryViewModel: InventoryViewModel by viewModels()
    private var locationIdAction: Long? = null
    private var locationStringAction: String? = null
    private var locationRequest: LocationRequest? = null


    @DelicateCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        Configuration.getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_game_map, container, false) as ConstraintLayout
        map = view.findViewById(R.id.map)
        ownLocationmarker = Marker(map)
        geocoder = Geocoder(requireContext(), Locale.getDefault())


        locationRequest = LocationRequest
            .create()
            .setInterval(1000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return

                for (location in locationResult.locations) {
                    val geoPoint = GeoPoint(location.latitude, location.longitude)
                    if (!isMapSet) {
                        setMap(geoPoint)
                    }
                    updateUserLocation(geoPoint)
                    if (!isInteractionsLocationsSet) {
                        setInteractionLocations(geoPoint)
                        isInteractionsLocationsSet = !isInteractionsLocationsSet
                        view.findViewById<RelativeLayout>(R.id.loadingPanel).visibility = View.GONE
                    }
                }
            }
        }

        checkSelfPermissions()

        setUserCollectedGems()
        val dailyQuestButton = view.findViewById<ImageView>(R.id.daily_quest_fab)
        val locateMyselfButton = view.findViewById<ImageView>(R.id.locate_myself_btn)
        val navigateToARButton = view.findViewById<Button>(R.id.navigate_to_game_AR_btn)
        dailyQuestButton.isEnabled = false
        locateMyselfButton.isEnabled = false

        dailyQuestButton.setOnClickListener {
            openDailyQuestDialog()
        }

        locateMyselfButton.setOnClickListener {
            locateUser()
        }

        navigateToARButton.setOnClickListener {
            val action = GameMapFragmentDirections.actionGameMapFragmentToGameARFragment(
                locationIdAction!!,
                locationStringAction!!
            )
            findNavController().navigate(action)
        }

        return view
    }

    /**
     * Function for creating and opening dialog to display daily quest for the active day
     */
    private fun openDailyQuestDialog() {
        val mapDetailsId = mMapDetailsViewModel.getLatestMapDetails().id
        val dailyQuests = mMapDetailsViewModel.getDailyQuestsByMapDetailsId(mapDetailsId)
        val mapDetails = mMapDetailsViewModel.getMapInfoWithAllLtLngValues(mapDetailsId)
        Log.d("daily", dailyQuests.toString())

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.daily_quest_dialog)
        val descriptionTv = dialog.findViewById<TextView>(R.id.task1_description_tv)
        val progressTv = dialog.findViewById<TextView>(R.id.task1_progress_tv)
        val rewardTv = dialog.findViewById<TextView>(R.id.task1_reward_tv)
        val completeTv = dialog.findViewById<ImageView>(R.id.task1_complete_tv)
        if (dailyQuests.isNotEmpty()) {
            Log.d("dailsy", "old one existed")
            val helper = DailyQuestHelper(
                dailyQuests[0].requiredEmeralds,
                dailyQuests[0].requiredRubies,
                dailyQuests[0].requiredSapphires,
                dailyQuests[0].requiredTopazes,
                mapDetails.mapDetails!!.collectedEmeralds,
                mapDetails.mapDetails!!.collectedRubies,
                mapDetails.mapDetails!!.collectedSapphires,
                mapDetails.mapDetails!!.collectedTopazes,
                dailyQuests[0].description,
                dailyQuests[0].rewardString,
                dailyQuests[0].isCompleted
            )

            val progressLabel = "Progress:\n"
            val emeraldProgress =
                if (helper.hasEmeralds) "Emeralds collected: ${helper.collectedEmeralds}/${helper.requiredEmeralds}\n" else ""
            val rubiesProgress =
                if (helper.hasRubies) "Rubies collected: ${helper.collectedRubies}/${helper.requiredRubies}\n" else ""
            val sapphiresProgress =
                if (helper.hasSapphires) "Sapphires collected: ${helper.collectedSapphires}/${helper.requiredSapphires}\n" else ""
            val topazesProgress =
                if (helper.hasTopazes) "Topazes collected: ${helper.collectedTopazes}/${helper.requiredTopazes}\n" else ""

            val progressString =
                progressLabel + emeraldProgress + rubiesProgress + sapphiresProgress + topazesProgress
            descriptionTv.text = helper.description
            progressTv.text = progressString
            rewardTv.text = helper.rewardString
            if (helper.isCompleted) {
                completeTv.setBackgroundResource(R.drawable.ic_baseline_check_24)
            } else {
                completeTv.setBackgroundResource(R.drawable.ic_baseline_close_24)
            }
            dialog.show()
            // TODO Construct new daily in else block if one hasn't been created for some reason?
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
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
        requestPermissions(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSIONS_REQUEST_LOCATION
        )
    }

    /**
     * Check if permissions are granted
     */
    private fun checkSelfPermissions() {

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
            AlertDialog.Builder(requireContext())
                .setTitle("Location Permission Needed")
                .setMessage("This application needs the Location permission, please accept to use location functionality")
                .setPositiveButton(
                    "OK"
                ) { _, _ ->
                    //Prompt the user once explanation has been shown
                    requestPermissions()
                }
                .create()
                .show()

        } else {
            // Permissions are granted so start requesting location updates
            Log.i("test", "Passed")

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        setMap(GeoPoint(location.latitude, location.longitude), firstcall = true)
                    }
                }
            fusedLocationClient.requestLocationUpdates(
                locationRequest!!,
                locationCallback, Looper.getMainLooper()
            )

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    if (ContextCompat.checkSelfPermission(
                            requireContext(),
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationClient.requestLocationUpdates(
                            locationRequest!!,
                            locationCallback, Looper.getMainLooper()
                        )

                    }
                }
            }
        }
    }

    companion object {
        private const val PERMISSIONS_REQUEST_LOCATION = 0
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

        val geoPoints = ArrayList<GeoPoint>()
        val polygon = Polygon()
        val radius = 30.0
        for (i in 1..360) {
            geoPoints.add(GeoPoint(geoPoint).destinationPoint(radius, i.toDouble()))
        }
        polygon.id = "activityRange"
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
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
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
    @DelicateCoroutinesApi
    private fun setInteractionLocations(geoPoint: GeoPoint) {

        val dateNow = Calendar.getInstance().timeInMillis
        val latestMapDetails = mMapDetailsViewModel.getLatestMapDetails()


        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val latestDbDate = simpleDateFormat.format(latestMapDetails.time)
        val dateNowString = simpleDateFormat.format(dateNow)

        // Check if latest DB date matches with current date
        if (latestDbDate == dateNowString) {
            //if (dateNow == latestMapDetails.time) {
            // Dates match -> set old values
            val latLngList =
                mMapDetailsViewModel.getMapLatLngPointsByMapDetailsId(latestMapDetails.id)
            setLocationsOnMap(latLngList, geoPoint)
        } else {
            // Dates don't match -> generate new values
            val chosenPoints = createInteractionLocations(geoPoint)
            createNewDbRowsForMapDetailsLatLngPointsDailyQuests(chosenPoints)
        }

    }

    private fun createInteractionLocations(geoPoint: GeoPoint): List<GeoPoint> {
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
        return interactionLocations.take(15)
    }

    /**
     * Set locations on map from a list queried from DB
     */
    @DelicateCoroutinesApi
    private fun setLocationsOnMap(chosenPoints: List<MapLatLng>, geoPoint: GeoPoint? = null) {
        view.findViewById<ImageView>(R.id.daily_quest_fab).isEnabled = true
        view.findViewById<ImageView>(R.id.locate_myself_btn).isEnabled = true
        // TODO if [chosenPoints] is empty createNewLocationsForSameDay()... -> Come back here with new list
        val activeLatLngCheck = chosenPoints.filter {
            it.isActive
        }
        Log.d("tesr", activeLatLngCheck.size.toString())
        if (activeLatLngCheck.isNotEmpty()) {
            chosenPoints.forEach {
                if (it.isActive) {
                    val loopMarker = Marker(map)
                    var iconDrawable = R.drawable.ic_baseline_pets_24
                    when (it.reward) {
                        "Emerald" -> iconDrawable = R.drawable.emerald
                        "Ruby" -> iconDrawable = R.drawable.ruby
                        "Sapphire" -> iconDrawable = R.drawable.sapphire
                        "Topaz" -> iconDrawable = R.drawable.topaz
                    }
                    loopMarker.icon = AppCompatResources.getDrawable(
                        requireContext(),
                        iconDrawable
                    )
                    loopMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    loopMarker.position = GeoPoint(it.lat, it.lng)

                    loopMarker.setOnMarkerClickListener { marker, _ ->
                        // Value that is passed to GameARFragment
                        locationIdAction = it.id
                        locationStringAction = it.gameType
                        Log.d("test", marker.position.latitude.toString())
                        activeDestination.latitude = marker.position.latitude
                        activeDestination.longitude = marker.position.longitude

                        // Draw a Polygon around it
                        drawPolygon(GeoPoint(marker.position.latitude, marker.position.longitude))
                        getDistanceToMarker(activeDestination)
                        map.controller.animateTo(GeoPoint(it.lat, it.lng))
                        map.invalidate()
                        marker.showInfoWindow()
                        return@setOnMarkerClickListener true
                    }

                    loopMarker.title = "${it.address}\n${it.gameType}"
                    map.overlays.add(loopMarker)
                }
            }
        } else {
            Log.d("tesr", "before null check")
            if (geoPoint != null) {
                createNewLocationsForSameDay(geoPoint)
            }
        }
    }

    @DelicateCoroutinesApi
    private fun createNewLocationsForSameDay(geoPoint: GeoPoint) {
        val chosenPoints = createInteractionLocations(geoPoint)
        val mapdetailsId = mMapDetailsViewModel.getLatestMapDetails().id


        GlobalScope.launch {
            val addLatLng = async(Dispatchers.IO) {
                chosenPoints.forEach {
                    val address = getAddress(it.latitude, it.longitude)
                    if (address.contains("Unnamed Road")) {
                        Log.d("Leave", "Leaving the loop!")
                        return@forEach
                    }
                    var collectableReward = ""
                    var collectable = R.drawable.ic_baseline_pets_24
                    var gameType: String? = null
                    when ((0..100).random()) {
                        in 0..24 -> {
                            collectable = R.drawable.topaz
                            collectableReward = "Topaz"
                            gameType = generateGameType()
                        }
                        in 25..49 -> {
                            collectable = R.drawable.ruby
                            collectableReward = "Ruby"
                            gameType = generateGameType()
                        }
                        in 50..75 -> {
                            collectable = R.drawable.sapphire
                            collectableReward = "Sapphire"
                            gameType = generateGameType()
                        }
                        in 76..100 -> {
                            collectable = R.drawable.emerald
                            collectableReward = "Emerald"
                            gameType = generateGameType()
                        }
                    }
                    Log.d("random", "Collectable: $collectable")
                    mMapDetailsViewModel.insertMapLatLng(
                        MapLatLng(
                            0,
                            mapdetailsId,
                            it.latitude,
                            it.longitude,
                            address,
                            collectableReward,
                            gameType!!,
                            true
                        )
                    )
                }
            }
            addLatLng.await()
            val updatedList = mMapDetailsViewModel.getMapLatLngPointsByMapDetailsId(mapdetailsId)
            activity?.runOnUiThread {
                setLocationsOnMap(updatedList)
            }
        }

    }

    /**
     * Generate new rows to DB and set locations on map
     */
    @DelicateCoroutinesApi
    private fun createNewDbRowsForMapDetailsLatLngPointsDailyQuests(chosenPoints: List<GeoPoint>) {
        var dateNow = Calendar.getInstance().timeInMillis
        val calendar: Calendar = Calendar.getInstance()
        val hours = calendar.get(Calendar.HOUR_OF_DAY) * 3_600_000
        val minutes = calendar.get(Calendar.MINUTE) * 60_000
        val seconds = calendar.get(Calendar.SECOND) * 1000
        // Do calculation to set time to mid day 43200000ms -> 12hours
        dateNow = dateNow - hours - minutes - seconds + 43_200_000
        // Returns the inserted rowId as well
        val newMapDetailsId =
            mMapDetailsViewModel.insertMapDetails(MapDetails(0, dateNow, 0, 0, 0, 0, 0))
        GlobalScope.launch {
            val addLatLng = async(Dispatchers.IO) {
                chosenPoints.forEach {
                    val address = getAddress(it.latitude, it.longitude)
                    if (address.contains("Unnamed Road")) {
                        Log.d("Leave", "Leaving the loop!")
                        return@forEach
                    }
                    var collectableReward = ""
                    var collectable = R.drawable.ic_baseline_pets_24
                    var gameType: String? = null
                    when ((0..100).random()) {
                        in 0..24 -> {
                            collectable = R.drawable.topaz
                            collectableReward = "Topaz"
                            gameType = generateGameType()
                        }
                        in 25..49 -> {
                            collectable = R.drawable.ruby
                            collectableReward = "Ruby"
                            gameType = generateGameType()
                        }
                        in 50..75 -> {
                            collectable = R.drawable.sapphire
                            collectableReward = "Sapphire"
                            gameType = generateGameType()
                        }
                        in 76..100 -> {
                            collectable = R.drawable.emerald
                            collectableReward = "Emerald"
                            gameType = generateGameType()
                        }
                    }
                    Log.d("random", "Collectable: $collectable")
                    mMapDetailsViewModel.insertMapLatLng(
                        MapLatLng(
                            0,
                            newMapDetailsId,
                            it.latitude,
                            it.longitude,
                            address,
                            collectableReward,
                            gameType!!,
                            true
                        )
                    )
                }
            }
            addLatLng.await()

            val addDailyQuestToDb = async {
                val newest = mMapDetailsViewModel.getMapInfoWithAllLtLngValues(newMapDetailsId)
                val latLngValues = newest.latLngValues
                val totalEmeralds = latLngValues?.filter {
                    it.reward == "Emerald"
                }
                val totalRubies = latLngValues?.filter {
                    it.reward == "Ruby"
                }
                val totalSapphires = latLngValues?.filter {
                    it.reward == "Sapphire"
                }
                val totalTopazes = latLngValues?.filter {
                    it.reward == "Topaz"
                }

                val taskList = mutableListOf<DailyQuest>()

                val taskCollectAllEmeralds = DailyQuest(
                    id = 0,
                    mapDetailsId = newMapDetailsId,
                    requiredEmeralds = totalEmeralds!!.size,
                    requiredRubies = 0,
                    requiredSapphires = 0,
                    requiredTopazes = 0,
                    requiredSteps = 0,
                    description = "Collect all Emeralds from the map!",
                    rewardString = "Reward: 3 Diamonds",
                    rewardAmount = 3,
                    isCompleted = false
                )
                if (totalEmeralds.isNotEmpty()) taskList.add(taskCollectAllEmeralds)

                val taskCollectAllRubies = DailyQuest(
                    id = 0,
                    mapDetailsId = newMapDetailsId,
                    requiredEmeralds = 0,
                    requiredRubies = totalRubies!!.size,
                    requiredSapphires = 0,
                    requiredTopazes = 0,
                    requiredSteps = 0,
                    description = "Collect all Rubies from the map!",
                    rewardString = "Reward: 3 Diamonds",
                    rewardAmount = 3,
                    isCompleted = false
                )
                if (totalRubies.isNotEmpty()) taskList.add(taskCollectAllRubies)

                val taskCollectAllSapphires = DailyQuest(
                    id = 0,
                    mapDetailsId = newMapDetailsId,
                    requiredEmeralds = 0,
                    requiredRubies = 0,
                    requiredSapphires = totalSapphires!!.size,
                    requiredTopazes = 0,
                    requiredSteps = 0,
                    description = "Collect all Sapphires from the map!",
                    rewardString = "Reward: 3 Diamonds",
                    rewardAmount = 3,
                    isCompleted = false
                )
                if (totalSapphires.isNotEmpty()) taskList.add(taskCollectAllSapphires)

                val taskCollectAllTopazes = DailyQuest(
                    id = 0,
                    mapDetailsId = newMapDetailsId,
                    requiredEmeralds = 0,
                    requiredRubies = 0,
                    requiredSapphires = 0,
                    requiredTopazes = totalTopazes!!.size,
                    requiredSteps = 0,
                    description = "Collect all Topazes from the map!",
                    rewardString = "Reward: 3 Diamonds",
                    rewardAmount = 3,
                    isCompleted = false
                )
                if (totalTopazes.isNotEmpty()) taskList.add(taskCollectAllTopazes)

                val taskCollectOneOfEachGem = DailyQuest(
                    id = 0,
                    mapDetailsId = newMapDetailsId,
                    requiredEmeralds = 1,
                    requiredRubies = 1,
                    requiredSapphires = 1,
                    requiredTopazes = 1,
                    requiredSteps = 0,
                    description = "Collect one of each gems!",
                    rewardString = "Reward: 3 Diamonds",
                    rewardAmount = 3,
                    isCompleted = false
                )
                if (totalEmeralds.isNotEmpty() && totalRubies.isNotEmpty() &&
                    totalSapphires.isNotEmpty() && totalTopazes.isNotEmpty()
                ) {
                    taskList.add(taskCollectOneOfEachGem)
                }

                val chosenDailyQuest = taskList.random()
                mMapDetailsViewModel.insertDailyQuest(chosenDailyQuest)
            }
            addDailyQuestToDb.await()
            activity?.runOnUiThread {
                view.findViewById<ImageView>(R.id.daily_quest_fab).isEnabled = true
            }
            val latestMapDetails = mMapDetailsViewModel.getLatestMapDetails()
            val latLngList =
                mMapDetailsViewModel.getMapLatLngPointsByMapDetailsId(latestMapDetails.id)
            activity?.runOnUiThread {
                setLocationsOnMap(latLngList)
            }
        }
    }

    /**
     * Calculate distance to active destination
     */
    private fun getDistanceToMarker(destinationLocation: Location) {
        val currentLocation = Location("currentLocation")
        currentLocation.latitude = ownLocationmarker.position.latitude
        currentLocation.longitude = ownLocationmarker.position.longitude

        val distance = currentLocation.distanceTo(destinationLocation).toInt()

        view.findViewById<TextView>(R.id.distance_tv).text = getString(
            R.string.distance_to_destination,
            distance
        )

        // Set isEnable to true or false
        view.findViewById<Button>(R.id.navigate_to_game_AR_btn).isEnabled = distance < 5000
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

    private fun locateUser() {
        val lat = ownLocationmarker.position.latitude
        val lng = ownLocationmarker.position.longitude
        map.controller.setZoom(13.0)
        map.controller.animateTo(GeoPoint(lat, lng))
    }

    /**
     * Geocoder used to get approximate address from given lat, lng values
     */
    private fun getAddress(lat: Double, lon: Double): String {
        return try {
            val addresses = geocoder.getFromLocation(lat, lon, 1)
            //Log.d("address", "List info: ${addresses}")
            addresses[0].getAddressLine(0)
        } catch (e: IOException) {
            "No address available"
        }
    }

    /**
     * Display the remaining gems for today
     */
    private fun setUserCollectedGems() {
        /*lifecycleScope.launch(context = Dispatchers.IO) {
            val gems = mInventoryViewModel.getInventoryNormal()
            view.findViewById<TextView>(R.id.emeralds_tv).text = gems.emeralds.toString()
            view.findViewById<TextView>(R.id.rubies_tv).text = gems.rubies.toString()
            view.findViewById<TextView>(R.id.sapphires_tv).text = gems.sapphires.toString()
            view.findViewById<TextView>(R.id.topazes_tv).text = gems.topazes.toString()
            view.findViewById<TextView>(R.id.diamonds_tv).text = gems.diamonds.toString()
        }

         */
        activity?.runOnUiThread {
            val gems = mInventoryViewModel.getInventoryNormal()
            view.findViewById<TextView>(R.id.emeralds_tv).text = gems.emeralds.toString()
            view.findViewById<TextView>(R.id.rubies_tv).text = gems.rubies.toString()
            view.findViewById<TextView>(R.id.sapphires_tv).text = gems.sapphires.toString()
            view.findViewById<TextView>(R.id.topazes_tv).text = gems.topazes.toString()
            view.findViewById<TextView>(R.id.diamonds_tv).text = gems.diamonds.toString()
        }
    }

    /**
     * Generate random gametype for a latlng value
     */
    private fun generateGameType(): String {
        return when ((0..3).random()) {
            0 -> {
                "normalQuiz"
            }
            1 -> {
                "imageQuiz"
            }
            2 -> {
                "multipleImageQuiz"
            }
            else -> {
                "sphereTask"
            }
        }
    }
}