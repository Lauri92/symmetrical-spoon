package fi.lauriari.ar_project.repositories

import fi.lauriari.ar_project.dao.CollectedItemDao
import fi.lauriari.ar_project.entities.CollectedItem

class CollectedItemRepository(private val collectedItemDao: CollectedItemDao) {

    suspend fun getCollectedItems(): List<CollectedItem> {
        return collectedItemDao.getCollectedItems()
    }

    suspend fun insertCollectedItem(collectedItem: CollectedItem): Long {
        return collectedItemDao.insertCollectedItem(collectedItem)
    }
}