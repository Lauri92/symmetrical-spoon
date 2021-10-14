package fi.lauriari.ar_project.network

import fi.lauriari.ar_project.datamodels.ImageQuestion
import fi.lauriari.ar_project.datamodels.ImageSelectionQuestion
import retrofit2.http.GET

interface ImageQuestionsApi {

    @GET("imagequestions.json")
    suspend fun getImageQuestions(): MutableList<ImageQuestion>

    @GET("multipleimagequestions.json")
    suspend fun getImageSelectionQuestions(): MutableList<ImageSelectionQuestion>
}