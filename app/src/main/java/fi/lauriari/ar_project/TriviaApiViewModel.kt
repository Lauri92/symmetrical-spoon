package fi.lauriari.ar_project

import androidx.lifecycle.ViewModel
import fi.lauriari.ar_project.repositories.TriviaRepository
import kotlinx.coroutines.runBlocking
import retrofit2.Response

class TriviaApiViewModel(private val triviaRepository: TriviaRepository) : ViewModel() {

    var response: Response<QuizQuestion>? = null

    fun getQuiz(): Response<QuizQuestion>? {
        runBlocking {
            val apiResponse = triviaRepository.getQuiz()
            response = apiResponse
        }
        return response
    }

}