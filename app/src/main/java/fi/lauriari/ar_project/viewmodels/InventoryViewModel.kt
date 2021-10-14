package fi.lauriari.ar_project.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import fi.lauriari.ar_project.entities.Inventory
import fi.lauriari.ar_project.SpoonDB
import fi.lauriari.ar_project.repositories.InventoryRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class InventoryViewModel(application: Application) : AndroidViewModel(application) {

    private val inventoryRepository: InventoryRepository

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

    fun getInventory(): LiveData<Inventory> {
        var inventory: LiveData<Inventory>
        runBlocking {
            inventory = inventoryRepository.getInventory()
        }
        return inventory
    }

    fun getInventoryList(): List<Inventory> {
        var inventoryL: List<Inventory>
        runBlocking {
            inventoryL = inventoryRepository.getList()
        }
        return inventoryL
    }

    fun getInventoryNormal(): Inventory {
        var inventory: Inventory
        runBlocking {
            inventory = inventoryRepository.getInventoryNormal()
        }
        return inventory
    }
}