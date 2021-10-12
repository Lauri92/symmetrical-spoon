package fi.lauriari.ar_project.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "inventory_table")
data class Inventory(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    var emeralds: Int,
    var rubies: Int,
    var sapphires: Int,
    var topazes: Int,
    var diamonds: Int,
)