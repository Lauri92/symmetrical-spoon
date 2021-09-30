package fi.lauriari.ar_project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
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