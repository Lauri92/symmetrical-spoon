package fi.lauriari.ar_project.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import fi.lauriari.ar_project.entities.Inventory

@Dao
interface InventoryDao {

    // Returning livedata doesn't require suspend keyword!!!!

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventory(inventory: Inventory): Long

    @Query("UPDATE inventory_table SET emeralds = :emerald where id=1")
    suspend fun updateEmeralds(emerald: Int)

    @Query("UPDATE inventory_table SET sapphires = :sapphire where id=1")
    suspend fun updateSapphires(sapphire: Int)

    @Query("UPDATE inventory_table SET rubies = :ruby where id=1")
    suspend fun updateRubies(ruby: Int)

    @Query("UPDATE inventory_table SET topazes = :topaz where id=1")
    suspend fun updateTopazes(topaz: Int)

    @Query("UPDATE inventory_table SET diamonds = :diamond where id=1")
    suspend fun updateDiamonds(diamond: Int)

    @Update
    suspend fun updateInventory(inventory: Inventory)

    @Query("SELECT * FROM inventory_table")
    fun getInventory(): LiveData<Inventory>

    @Query("SELECT * FROM inventory_table")
    suspend fun getInventoryNormal(): Inventory

    // for checking if there is a row saved in the inventory table
    @Query("SELECT * FROM inventory_table")
    suspend fun getInventoryList(): List<Inventory>
}