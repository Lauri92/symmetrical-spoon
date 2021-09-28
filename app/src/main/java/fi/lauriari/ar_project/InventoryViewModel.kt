package fi.lauriari.ar_project

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    val inventoryRepository: InventoryRepository

    init {
        val inventoryDao = SpoonDB.getDatabase(application).inventoryDao()
        inventoryRepository = InventoryRepository(inventoryDao)
    }

    fun insertInventory(inventory: Inventory): Long {
        var inventoryId: Long
        runBlocking {
            inventoryId = inventoryRepository.insertInventory(inventory)
        }
        return inventoryId
    }

    fun updateInventory(inventory: Inventory) {
        viewModelScope.launch(Dispatchers.IO) {
            inventoryRepository.updateInventory(inventory)
        }
    }

    fun getInventory(): Inventory {
        var inventory: Inventory
        runBlocking {
            inventory = inventoryRepository.getInventory()
        }
        return inventory
    }

}