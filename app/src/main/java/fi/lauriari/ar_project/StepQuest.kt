package fi.lauriari.ar_project

import android.content.Context
import android.widget.TextView
import fi.lauriari.ar_project.entities.DailyQuest

class StepQuest(
    private var currentStep: Int, private val stepQuest: DailyQuest
) {
    fun setDescriptionText(descriptionTv: TextView) {
        descriptionTv.text = stepQuest.description
    }

    fun setProgressText(context: Context, progressTv: TextView) {
        val requiredStep = stepQuest.requiredSteps
        if (currentStep >= requiredStep) currentStep = requiredStep
        progressTv.text =
            context.getString(R.string.step_count, currentStep, requiredStep)
    }

    fun checkIsCompleted() = currentStep >= stepQuest.requiredSteps

    fun getIsCompleted() = stepQuest.isCompleted
}