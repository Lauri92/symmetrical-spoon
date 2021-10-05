package fi.lauriari.ar_project.Dao

import androidx.room.*
import fi.lauriari.ar_project.Entities.CollectedItem

@Dao
interface CollectedItemDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCollectedItem(collectedItem:CollectedItem): Long

    @Update
    suspend fun updateCollectedItem(collectedItem:CollectedItem)

    @Query("SELECT * FROM collected_item_table")
    suspend fun getCollectedItems(): List<CollectedItem>
}