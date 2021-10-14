package fi.lauriari.ar_project.repositories

import android.util.Log
import fi.lauriari.ar_project.datamodels.ImageQuestion
import fi.lauriari.ar_project.datamodels.ImageSelectionQuestion
import fi.lauriari.ar_project.datamodels.QuizQuestion
import fi.lauriari.ar_project.network.ImageQuestionsRetrofitInstance
import fi.lauriari.ar_project.network.TriviaApiRetrofitInstance
import retrofit2.Response

class TriviaRepository {

    suspend fun getQuiz(): Response<QuizQuestion> {
        Log.d("DBG", "in repo")
        return TriviaApiRetrofitInstance.api.getQuiz()
    }

    suspend fun getImageQuestions(): MutableList<ImageQuestion> {
        return ImageQuestionsRetrofitInstance.api.getImageQuestions()
    }

    suspend fun getImageSelectionQuestions(): MutableList<ImageSelectionQuestion> {
        return ImageQuestionsRetrofitInstance.api.getImageSelectionQuestions()
    }
}