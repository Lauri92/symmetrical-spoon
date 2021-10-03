package fi.lauriari.ar_project

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import fi.lauriari.ar_project.Dao.CollectedItemDao
import fi.lauriari.ar_project.Dao.InventoryDao
import fi.lauriari.ar_project.Entities.CollectedItem
import fi.lauriari.ar_project.Entities.Inventory


@Database(entities = [(MapDetails::class), (MapLatLng::class), (Inventory::class), (CollectedItem::class)], version = 1, exportSchema = false)
abstract class SpoonDB : RoomDatabase() {

    abstract fun mapDetailsDao(): MapDetailsDao
    abstract fun inventoryDao(): InventoryDao
    abstract fun collectedItemDao(): CollectedItemDao

    companion object {
        @Volatile
        private var INSTANCE: SpoonDB? = null

        fun getDatabase(context: Context): SpoonDB {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SpoonDB::class.java,
                    "spoon_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }

}