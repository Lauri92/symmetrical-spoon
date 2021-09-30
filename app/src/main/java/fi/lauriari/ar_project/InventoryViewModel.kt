package fi.lauriari.ar_project

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
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

    fun updateEmeralds(emerald: Int){
        viewModelScope.launch(Dispatchers.IO) {
            inventoryRepository.updateEmeralds(emerald)
        }
    }

    fun updateRubies(ruby: Int){
        viewModelScope.launch(Dispatchers.IO) {
            inventoryRepository.updateRubies(ruby)
        }
    }

    fun updateSapphires(sapphire: Int){
        viewModelScope.launch(Dispatchers.IO) {
            inventoryRepository.updateSapphires(sapphire)
        }
    }

    fun updateTopazes(topaz: Int){
        viewModelScope.launch(Dispatchers.IO) {
            inventoryRepository.updateTopazes(topaz)
        }
    }

    fun updateDiamonds(diamond: Int){
        viewModelScope.launch(Dispatchers.IO) {
            inventoryRepository.updateDiamonds(diamond)
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