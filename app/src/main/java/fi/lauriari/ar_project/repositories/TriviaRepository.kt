package fi.lauriari.ar_project.repositories

import android.util.Log
import fi.lauriari.ar_project.QuizQuestion
import fi.lauriari.ar_project.Network.RetrofitInstance
import retrofit2.Response

class TriviaRepository {

    suspend fun getQuiz(): Response<QuizQuestion> {
        Log.d("DBG", "in repo")
        return RetrofitInstance.api.getQuiz()
    }
}