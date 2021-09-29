package fi.lauriari.ar_project

import androidx.lifecycle.LiveData

class InventoryRepository(private val inventoryDao: InventoryDao) {

    suspend fun insertInventory(inventory: Inventory):Long {
        return inventoryDao.insertInventory(inventory)
    }

    suspend fun updateInventory(inventory: Inventory) {
        inventoryDao.updateInventory(inventory)
    }

     fun getInventory(): LiveData<Inventory> {
        return inventoryDao.getInventory()
    }

    suspend fun getList():List<Inventory>{
        return inventoryDao.getInventoryList()
    }

}