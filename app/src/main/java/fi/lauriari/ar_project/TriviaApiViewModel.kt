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

    fun getImageQuestions(): MutableList<ImageQuestion> {
        var response: MutableList<ImageQuestion>
        runBlocking {
            val apiResponse = triviaRepository.getImageQuestions()
            response = apiResponse
        }
        return response
    }

    fun getImageSelectionQuestions(): MutableList<ImageSelectionQuestion> {
        var response: MutableList<ImageSelectionQuestion>
        runBlocking {
            val apiResponse = triviaRepository.getImageSelectionQuestions()
            response = apiResponse
        }
        return response
    }

}