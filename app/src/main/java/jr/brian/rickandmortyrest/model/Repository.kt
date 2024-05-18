package jr.brian.rickandmortyrest.model

import jr.brian.rickandmortyrest.model.remote.ApiService
import jr.brian.rickandmortyrest.model.remote.callApi
import javax.inject.Inject

class Repository @Inject constructor(private val apiService: ApiService) {
    fun getCharactersByName(name: String) = callApi {
        apiService.getCharacterByName(name)
    }

    fun getAllCharacters(pageNumber: String) = callApi {
        apiService.getAllCharacters(pageNumber)
    }
}