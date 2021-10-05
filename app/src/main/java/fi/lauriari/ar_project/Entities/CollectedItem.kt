package fi.lauriari.ar_project.Entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "collected_item_table")
data class CollectedItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val thumbnail: String,
    val objectUrl: String,
    val description: String,
    val collectedTime: Long
):Parcelable