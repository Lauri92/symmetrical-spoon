package fi.lauriari.ar_project.Dao

import androidx.room.Dao
import androidx.room.Query
import fi.lauriari.ar_project.Entities.RewardItem

@Dao
interface RewardItemDao {
    @Query("SELECT * FROM reward_item_table")
    suspend fun getRewardItems(): RewardItem
}