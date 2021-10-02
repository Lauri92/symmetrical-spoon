package fi.lauriari.ar_project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import retrofit2.Response

class TriviaApiViewModel(private val triviaRepository: TriviaRepository) : ViewModel() {


    fun getQuiz(): Response<QuizQuestion>? {
        var response: Response<QuizQuestion>? = null
        runBlocking {
            val apiResponse = triviaRepository.getQuiz()
            response = apiResponse
        }
        return response
    }

    fun getImageQuestions(): Response<List<ImageQuestion>> {
        var response: Response<List<ImageQuestion>>
        runBlocking {
            val apiResponse = triviaRepository.getImageQuestions()
            response = apiResponse
        }
        return response
    }

}