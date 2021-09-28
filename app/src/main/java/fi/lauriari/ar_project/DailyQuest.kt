package fi.lauriari.ar_project

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity
data class DailyQuest(@PrimaryKey(autoGenerate = true)
                 val id: Long, val content: String, val releasedDate: Date, val isCompleted: Boolean) {
}