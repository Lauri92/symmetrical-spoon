package fi.lauriari.ar_project.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "collected_item_table")
data class CollectedItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    // should preview image be a saved image or fetched by internet?
    val thumbnail: String,
    val description: String,
    val collectedTime: Long
)