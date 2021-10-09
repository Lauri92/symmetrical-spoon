package fi.lauriari.ar_project.Dao

import androidx.room.*
import fi.lauriari.ar_project.DailyQuest
import fi.lauriari.ar_project.MapDetails
import fi.lauriari.ar_project.MapDetailsWithAllLatLngValues
import fi.lauriari.ar_project.MapLatLng

@Dao
interface MapDetailsDao {

    // Returning livedata doesn't require suspend keyword!!!!

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMapDetails(mapdetails: MapDetails): Long

    @Update
    suspend fun updateMapDetails(mapdetails: MapDetails)

    @Query("SELECT * FROM mapdetails_table ORDER BY id ASC")
    suspend fun getAllMapDetails(): List<MapDetails>

    @Query("SELECT id, MAX(time) as time, collectedEmeralds, collectedRubies, collectedSapphires, collectedTopazes, collectedDiamonds FROM mapdetails_table")
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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDailyQuest(dailyQuest: DailyQuest): Long

    @Update
    suspend fun updateDailyQuest(dailyQuest: DailyQuest)

    @Query("SELECT * FROM dailyquest_table WHERE mapDetailsId= :mapDetailsId")
    suspend fun getDailyQuestsByMapDetailsId(mapDetailsId: Long): List<DailyQuest>

    @Query("SELECT * FROM dailyquest_table WHERE id= :id")
    suspend fun getDailyQuestByDailyQuestId(id: Long): List<DailyQuest>

}