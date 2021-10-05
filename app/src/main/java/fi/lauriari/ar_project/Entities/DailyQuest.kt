package fi.lauriari.ar_project

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "dailyquest_table")
data class DailyQuest(
    @PrimaryKey(autoGenerate = true)
    val id: Long,
    val mapDetailsId: Long,
    val requiredEmeralds: Int,
    val requiredRubies: Int,
    val requiredSapphires: Int,
    val requiredTopazes: Int,
    val requiredSteps: Int,
    val description: String,
    val rewardString: String,
    val rewardAmount: Int,
    var isCompleted: Boolean
) {
}