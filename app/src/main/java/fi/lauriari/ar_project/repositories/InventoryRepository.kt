package fi.lauriari.ar_project.repositories

import androidx.lifecycle.LiveData
import fi.lauriari.ar_project.Dao.InventoryDao
import fi.lauriari.ar_project.Entities.Inventory

class InventoryRepository(private val inventoryDao: InventoryDao) {

    suspend fun insertInventory(inventory: Inventory):Long {
        return inventoryDao.insertInventory(inventory)
    }

    suspend fun updateInventory(inventory: Inventory) {
        inventoryDao.updateInventory(inventory)
    }

    suspend fun updateEmeralds(emerald: Int){
        inventoryDao.updateEmeralds(emerald)
    }

    suspend fun updateRubies(ruby: Int){
        inventoryDao.updateEmeralds(ruby)
    }

    suspend fun updateSapphires(sapphire: Int){
        inventoryDao.updateEmeralds(sapphire)
    }

    suspend fun updateTopazes(topaz: Int){
        inventoryDao.updateEmeralds(topaz)
    }

    suspend fun updateDiamonds(diamond: Int){
        inventoryDao.updateEmeralds(diamond)
    }

     fun getInventory(): LiveData<Inventory> {
        return inventoryDao.getInventory()
    }

    suspend fun getInventoryNormal(): Inventory {
        return inventoryDao.getInventoryNormal()
    }

    suspend fun getList():List<Inventory>{
        return inventoryDao.getInventoryList()
    }

}