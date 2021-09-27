package fi.lauriari.ar_project

class MapDetailsRepository(private val mapDetailsDao: MapDetailsDao) {

    suspend fun getAllMapDetails(): List<MapDetails> {
        return mapDetailsDao.getAllMapDetails()
    }

    suspend fun insertMapDetails(mapDetails: MapDetails): Long {
        //Log.d("repo", "testering: $testering")
        //val latest = getLatestMapDetails()
        //Log.d("repo", "mapDetails: $mapDetails")
        //Log.d("repo", "latest: $latest")
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

    suspend fun getMapLatPointLngById(id: Long): MapLatLng {
        return mapDetailsDao.getMapLatPointLngById(id)
    }

    suspend fun getMapLatLngPointsByMapDetailsId(mapdetailsId: Long): List<MapLatLng> {
        return mapDetailsDao.getMapLatLngPointsByMapDetailsId(mapdetailsId)
    }

}