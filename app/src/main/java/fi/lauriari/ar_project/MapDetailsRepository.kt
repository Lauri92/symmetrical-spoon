package fi.lauriari.ar_project

import android.util.Log

class MapDetailsRepository(private val mapDetailsDao: MapDetailsDao) {

    suspend fun getAllMapDetails(): List<MapDetails> {
        return mapDetailsDao.getAllMapDetails()
    }

    suspend fun insertMapDetails(mapDetails: MapDetails): Long {
        mapDetailsDao.insertMapDetails(mapDetails)
        val latest = getLatestMapDetails()
        Log.d("repo", "mapDetails: $mapDetails")
        Log.d("repo", "latest: $latest")
        return latest.id
    }

    suspend fun getLatestMapDetails(): MapDetails {
        return mapDetailsDao.getLatestMapDetails()
    }

    suspend fun getMapDetailsWithAllLatLngValues(id: Long): MapDetailsWithAllLatLngValues {
        return mapDetailsDao.getMapDetailsWithAllLatLngValues(id)
    }

    suspend fun insertMapLatLng(mapLatLng: MapLatLng) {
        mapDetailsDao.insertMapLatLng(mapLatLng)
    }

    suspend fun getMapLatLngPointsByMapDetailsId(mapdetailsId: Long): List<MapLatLng> {
        return mapDetailsDao.getMapLatLngPointsByMapDetailsId(mapdetailsId)
    }

}