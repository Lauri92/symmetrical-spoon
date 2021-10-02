package fi.lauriari.ar_project

import retrofit2.Response
import retrofit2.http.GET

interface TriviaApi {

    @GET("api.php?amount=5&type=multiple")
    suspend fun getQuiz(): Response<QuizQuestion>

}