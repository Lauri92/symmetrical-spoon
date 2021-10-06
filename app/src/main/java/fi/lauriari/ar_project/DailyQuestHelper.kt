package fi.lauriari.ar_project

class DailyQuestHelper(
    val requiredEmeralds: Int,
    val requiredRubies: Int,
    val requiredSapphires: Int,
    val requiredTopazes: Int,
    var collectedEmeralds: Int,
    var collectedRubies: Int,
    var collectedSapphires: Int,
    var collectedTopazes: Int,
    val description: String,
    val rewardString: String,
    val isCompleted: Boolean
) {
    init {
        if (collectedEmeralds >= requiredEmeralds) {
            collectedEmeralds = requiredEmeralds
        }
        if (collectedRubies >= requiredRubies) {
            collectedRubies = requiredRubies
        }
        if (collectedSapphires >= requiredSapphires) {
            collectedSapphires = requiredSapphires
        }
        if (collectedTopazes >= requiredTopazes) {
            collectedTopazes = requiredTopazes
        }

    }

    val hasEmeralds: Boolean = requiredEmeralds > 0
    val hasRubies: Boolean = requiredRubies > 0
    val hasSapphires: Boolean = requiredSapphires > 0
    val hasTopazes: Boolean = requiredTopazes > 0

    fun isEmeraldsCollected(): Boolean {
        return this.collectedEmeralds >= this.requiredEmeralds
    }

    fun isRubiesCollected(): Boolean {
        return this.collectedRubies >= this.requiredRubies
    }

    fun isSapphiresCollected(): Boolean {
        return this.collectedSapphires >= this.requiredSapphires
    }

    fun isTopazesCollected(): Boolean {
        return this.collectedTopazes >= this.requiredTopazes
    }

    fun isAllRequiredGemsCollected(): Boolean {
        return isEmeraldsCollected() && isRubiesCollected() && isSapphiresCollected() && isTopazesCollected()
    }

}