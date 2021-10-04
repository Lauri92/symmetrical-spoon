package fi.lauriari.ar_project.repositories

import fi.lauriari.ar_project.Dao.CollectedItemDao
import fi.lauriari.ar_project.Entities.CollectedItem

class CollectedItemRepository(private val collectedItemDao: CollectedItemDao) {

    suspend fun getCollectedItems():List<CollectedItem>{
        return collectedItemDao.getCollectedItems()
    }

    suspend fun insertCollectedItem(collectedItem: CollectedItem):Long{
        return collectedItemDao.insertCollectedItem(collectedItem)
    }
}