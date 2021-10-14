package fi.lauriari.ar_project.viewmodelfactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fi.lauriari.ar_project.repositories.TriviaRepository
import fi.lauriari.ar_project.viewmodels.TriviaApiViewModel

class TriviaViewModelFactory(private val repository: TriviaRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return TriviaApiViewModel(repository) as T
    }
}