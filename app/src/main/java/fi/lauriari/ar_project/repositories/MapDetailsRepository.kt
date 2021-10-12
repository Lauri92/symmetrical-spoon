package fi.lauriari.ar_project.repositories

import fi.lauriari.ar_project.entities.DailyQuest
import fi.lauriari.ar_project.dao.MapDetailsDao
import fi.lauriari.ar_project.entities.MapDetails
import fi.lauriari.ar_project.entities.MapDetailsWithAllLatLngValues
import fi.lauriari.ar_project.entities.MapLatLng

class MapDetailsRepository(private val mapDetailsDao: MapDetailsDao) {

    suspend fun getAllMapDetails(): List<MapDetails> {
        return mapDetailsDao.getAllMapDetails()
    }

    suspend fun insertMapDetails(mapDetails: MapDetails): Long {
        return mapDetailsDao.insertMapDetails(mapDetails)
    }

    suspend fun getLatestMapDetails(): MapDetails {
        return mapDetailsDao.getLatestMapDetails()
    }

    suspend fun getMapDetailsWithAllLatLngValues(id: Long): MapDetailsWithAllLatLngValues {
        return mapDetailsDao.getMapDetailsWithAllLatLngValues(id)
    }

    suspend fun insertMapLatLng(mapLatLng: MapLatLng): Long {
        return mapDetailsDao.insertMapLatLng(mapLatLng)
    }

    suspend fun updateMapLatLng(mapLatLng: MapLatLng) {
        mapDetailsDao.updateMapLatLng(mapLatLng)
    }

    suspend fun updateMapDetails(mapDetails: MapDetails) {
        mapDetailsDao.updateMapDetails(mapDetails)
    }

    suspend fun getMapLatPointLngById(id: Long): MapLatLng {
        return mapDetailsDao.getMapLatPointLngById(id)
    }

    suspend fun getMapLatLngPointsByMapDetailsId(mapdetailsId: Long): List<MapLatLng> {
        return mapDetailsDao.getMapLatLngPointsByMapDetailsId(mapdetailsId)
    }

    suspend fun insertDailyQuest(dailyQuest: DailyQuest): Long {
        return mapDetailsDao.insertDailyQuest(dailyQuest)
    }

    suspend fun updateDailyQuest(dailyQuest: DailyQuest) {
        mapDetailsDao.updateDailyQuest(dailyQuest)
    }

    suspend fun getDailyQuestsByMapDetailsId(mapdetailsId: Long): List<DailyQuest> {
        return mapDetailsDao.getDailyQuestsByMapDetailsId(mapdetailsId)
    }

    suspend fun getDailyQuestByDailyQuestId(id: Long): List<DailyQuest> {
        return mapDetailsDao.getDailyQuestByDailyQuestId(id)
    }
}