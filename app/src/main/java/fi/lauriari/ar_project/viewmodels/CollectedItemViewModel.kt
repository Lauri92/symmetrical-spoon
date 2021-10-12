package fi.lauriari.ar_project.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import fi.lauriari.ar_project.repositories.CollectedItemRepository
import fi.lauriari.ar_project.entities.CollectedItem
import fi.lauriari.ar_project.SpoonDB
import kotlinx.coroutines.runBlocking

class CollectedItemViewModel(application: Application) : AndroidViewModel(application) {

    private val collectedItemRepository: CollectedItemRepository

    init {
        val collectedItemDao = SpoonDB.getDatabase(application).collectedItemDao()
        collectedItemRepository = CollectedItemRepository(collectedItemDao)
    }

    fun getCollectedItems(): List<CollectedItem> {
        var collectedItems: List<CollectedItem>
        runBlocking {
            collectedItems = collectedItemRepository.getCollectedItems()
        }
        return collectedItems
    }

    fun insertCollectedItem(collectedItem: CollectedItem) {
        runBlocking {
            collectedItemRepository.insertCollectedItem(collectedItem)
        }
    }
}