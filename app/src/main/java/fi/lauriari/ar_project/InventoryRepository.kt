package fi.lauriari.ar_project

class InventoryRepository(private val inventoryDao: InventoryDao) {

    suspend fun insertInventory(inventory: Inventory):Long {
        return inventoryDao.insertInventory(inventory)
    }

    suspend fun updateInventory(inventory: Inventory) {
        inventoryDao.updateInventory(inventory)
    }

    suspend fun getInventory(): Inventory {
        return inventoryDao.getInventory()
    }

}