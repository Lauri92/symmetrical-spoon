package fi.lauriari.ar_project

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class TriviaViewModelFactory(private val repository: TriviaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TriviaApiViewModel(repository) as T
    }
}