package jr.brian.rickandmortyrest.model

import jr.brian.rickandmortyrest.model.local.Character

//sealed class AppState {
//    data class Success(val data: Character) : AppState()
//    data class Error(val message: String) : AppState()
//    data object Loading : AppState()
//    data object Idle : AppState()
//}

sealed class AppState<out T> {
    data object Loading : AppState<Nothing>()
    data object Idle : AppState<Nothing>()
    data class Error<out T>(val data: String) : AppState<T>()
    data class Success<out T>(val data: T) : AppState<T>()
}