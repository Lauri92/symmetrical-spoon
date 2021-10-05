package fi.lauriari.ar_project

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import fi.lauriari.ar_project.repositories.MapDetailsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MapDetailsViewModel(application: Application) : AndroidViewModel(application) {

    val mapDetailsRepository: MapDetailsRepository

    init {
        val mapDetailsDao = SpoonDB.getDatabase(application).mapDetailsDao()
        mapDetailsRepository = MapDetailsRepository(mapDetailsDao)
    }

    fun getAllMapDetails(): List<MapDetails> {
        var allMapDetails: List<MapDetails>
        runBlocking {
            allMapDetails = mapDetailsRepository.getAllMapDetails()
        }
        return allMapDetails
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

    fun updateMapLatLng(mapLatLng: MapLatLng) {
        viewModelScope.launch(Dispatchers.IO) {
            mapDetailsRepository.updateMapLatLng(mapLatLng)
        }
    }

    fun updateMapDetails(mapDetails: MapDetails) {
        viewModelScope.launch(Dispatchers.IO) {
            mapDetailsRepository.updateMapDetails(mapDetails)
        }
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

    fun insertDailyQuest(dailyQuest: DailyQuest) {
        viewModelScope.launch(Dispatchers.IO) {
            mapDetailsRepository.insertDailyQuest(dailyQuest)
        }
    }

    fun updateDailyQuest(dailyQuest: DailyQuest) {
        viewModelScope.launch(Dispatchers.IO) {
            mapDetailsRepository.updateDailyQuest(dailyQuest)
        }
    }

    fun getDailyQuestsByMapDetailsId(mapDetailsId: Long): List<DailyQuest> {
        var dailyQuests: List<DailyQuest>
        runBlocking {
            dailyQuests = mapDetailsRepository.getDailyQuestsByMapDetailsId(mapDetailsId)
        }
        return dailyQuests
    }

}