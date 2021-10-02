package fi.lauriari.ar_project

import retrofit2.Response
import retrofit2.http.GET

interface ImageQuestionsApi {

    @GET("imagequestions.json")
    suspend fun getImageQuestions(): MutableList<ImageQuestion>

    @GET("multipleimagequestions.json")
    suspend fun getImageSelectionQuestions(): MutableList<ImageSelectionQuestion>
}