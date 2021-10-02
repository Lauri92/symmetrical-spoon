package fi.lauriari.ar_project

import android.util.Log
import retrofit2.Response

class TriviaRepository {

    suspend fun getQuiz(): Response<QuizQuestion> {
        Log.d("DBG", "in repo")
        return TriviaApiRetrofitInstance.api.getQuiz()
    }

    suspend fun getImageQuestions(): Response<List<ImageQuestion>> {
        return ImageQuestionsRetrofitInstance.api.getImageQuestions()
    }
}