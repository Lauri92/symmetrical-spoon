package fi.lauriari.ar_project

import androidx.room.*

@Dao
interface InventoryDao {

    // Returning livedata doesn't require suspend keyword!!!!

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventory(inventory: Inventory): Long

    @Update
    suspend fun updateInventory(inventory: Inventory)

    @Query("SELECT * FROM inventory_table")
    suspend fun getInventory(): Inventory
}