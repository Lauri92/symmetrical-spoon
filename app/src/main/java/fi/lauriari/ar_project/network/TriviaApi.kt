package fi.lauriari.ar_project.network

import fi.lauriari.ar_project.datamodels.QuizQuestion
import retrofit2.Response
import retrofit2.http.GET

interface TriviaApi {

    @GET("api.php?amount=5&type=multiple")
    suspend fun getQuiz(): Response<QuizQuestion>

}