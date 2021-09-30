package fi.lauriari.ar_project

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

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