package fi.lauriari.ar_project

import androidx.room.*

@Dao
interface MapDetailsDao {

    // Returning livedata doesn't require suspend keyword!!!!

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMapDetails(mapdetails: MapDetails): Long

    @Query("SELECT * FROM mapdetails_table ORDER BY id ASC")
    suspend fun getAllMapDetails(): List<MapDetails>

    @Query("SELECT id, MAX(time) as time FROM mapdetails_table")
    suspend fun getLatestMapDetails(): MapDetails

    @Query("SELECT * FROM mapdetails_table WHERE id = :id")
    suspend fun getMapDetailsWithAllLatLngValues(id: Long): MapDetailsWithAllLatLngValues

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMapLatLng(mapLatLng: MapLatLng): Long

    @Update
    suspend fun updateMapLatLng(mapLatLng: MapLatLng)

    @Query("SELECT * FROM maplatlng_table WHERE id = :id")
    suspend fun getMapLatPointLngById(id: Long): MapLatLng

    @Query("SELECT * FROM maplatlng_table WHERE mapDetailsId = :mapDetailsId")
    suspend fun getMapLatLngPointsByMapDetailsId(mapDetailsId: Long): List<MapLatLng>

}