package fi.lauriari.ar_project

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class GameMapViewModel(application: Application) : AndroidViewModel(application) {

    val mapDetailsRepository: MapDetailsRepository

    init {
        val mapDetailsDao = SpoonDB.getDatabase(application).mapDetailsDao()
        mapDetailsRepository = MapDetailsRepository(mapDetailsDao)
    }


    fun insertMapDetails(mapDetails: MapDetails): Long {
        var insertedRowId: Long
        runBlocking {
            insertedRowId = mapDetailsRepository.insertMapDetails(mapDetails)
        }
        return insertedRowId
    }

    fun insertMapLatLng(mapLatLng: MapLatLng): Long {
        var insertedRowId: Long
        runBlocking {
            insertedRowId = mapDetailsRepository.insertMapLatLng(mapLatLng)
        }
        return insertedRowId
    }

    fun getLatestMapDetails(): MapDetails {
        var selectedMapinfo: MapDetails
        runBlocking {
            selectedMapinfo = mapDetailsRepository.getLatestMapDetails()
        }
        return selectedMapinfo
    }

    fun getMapLatPointLngById(id: Long): MapLatLng {
        var selectedLatLngPoint: MapLatLng
        runBlocking {
            selectedLatLngPoint = mapDetailsRepository.getMapLatPointLngById(id)
        }
        return selectedLatLngPoint
    }

    fun getMapLatLngPointsByMapDetailsId(mapDetailsId: Long): List<MapLatLng> {
        var selectedLatLng: List<MapLatLng>
        runBlocking {
            selectedLatLng = mapDetailsRepository.getMapLatLngPointsByMapDetailsId(mapDetailsId)
        }
        return selectedLatLng
    }

    fun getMapInfoWithAllLtLngValues(id: Long): MapDetailsWithAllLatLngValues {
        var mapInfoWithAllLtLngValues: MapDetailsWithAllLatLngValues
        runBlocking {
            mapInfoWithAllLtLngValues = mapDetailsRepository.getMapDetailsWithAllLatLngValues(id)
        }
        return mapInfoWithAllLtLngValues
    }

}