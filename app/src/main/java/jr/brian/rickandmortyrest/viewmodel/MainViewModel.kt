package jr.brian.rickandmortyrest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jr.brian.rickandmortyrest.model.AppState
import jr.brian.rickandmortyrest.model.Repository
import jr.brian.rickandmortyrest.model.local.CharacterResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val _state = MutableStateFlow<AppState<CharacterResult?>>(AppState.Idle)
    val state = _state.asStateFlow()
    fun getCharacterByName(name: String) {
        viewModelScope.launch {
            repository.getCharacterByName(name).collect {
                _state.emit(it)
            }
        }
    }

    fun getAllCharacters(pageNumber: String) {
        viewModelScope.launch {
            repository.getAllCharacters(pageNumber).collect {
                _state.emit(it)
            }
        }
    }
}