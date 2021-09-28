package fi.lauriari.ar_project

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class DailyQuest(@PrimaryKey(autoGenerate = true)
                 val id: Long, val content: String, var isCompleted: Boolean) {
}