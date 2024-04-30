package jr.brian.rickandmortyrest.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jr.brian.rickandmortyrest.model.AppState
import jr.brian.rickandmortyrest.model.Repository
import jr.brian.rickandmortyrest.model.local.Character
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: Repository) : ViewModel() {
    private val _state = MutableStateFlow<AppState<Character?>>(AppState.Idle)
    val state = _state.asStateFlow()

    fun getCharacterById(id: String) {
        _state.value = AppState.Loading
        viewModelScope.launch {
            repository.getUserById(id).collect {
                _state.emit(it)
            }
        }
    }

    fun getCharacterByName(name: String) {
        _state.value = AppState.Loading
        viewModelScope.launch {
            repository.getUserByName(name).collect {
                _state.emit(it)
            }
        }
    }
}