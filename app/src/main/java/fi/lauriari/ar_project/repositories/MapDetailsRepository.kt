package fi.lauriari.ar_project.repositories

import fi.lauriari.ar_project.MapDetails
import fi.lauriari.ar_project.Dao.MapDetailsDao
import fi.lauriari.ar_project.MapDetailsWithAllLatLngValues
import fi.lauriari.ar_project.MapLatLng

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

}