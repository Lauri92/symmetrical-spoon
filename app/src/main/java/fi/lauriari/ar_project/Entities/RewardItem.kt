package fi.lauriari.ar_project.Entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reward_item_table")
data class RewardItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val name: String,
    val description: String,
    val emerald: Int,
    val ruby: Int,
    val sapphire: Int,
    val topaz: Int,
    val diamond: Int
)