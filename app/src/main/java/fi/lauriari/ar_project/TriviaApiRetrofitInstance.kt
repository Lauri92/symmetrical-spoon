package fi.lauriari.ar_project

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TriviaApiRetrofitInstance {

    const val BASE_URL = "https://opentdb.com/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: TriviaApi by lazy {
        retrofit.create(TriviaApi::class.java)
    }

}
object ImageQuestionsRetrofitInstance {

    const val BASE_URL = "https://users.metropolia.fi/~lauriari/AR_project/"

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: ImageQuestionsApi by lazy {
        retrofit.create(ImageQuestionsApi::class.java)
    }

}
