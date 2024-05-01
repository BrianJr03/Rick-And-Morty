package jr.brian.rickandmortyrest.model

import jr.brian.rickandmortyrest.model.remote.ApiService
import jr.brian.rickandmortyrest.model.remote.callApi
import javax.inject.Inject

class Repository @Inject constructor(private val apiService: ApiService) {
    fun getUserByName(name: String) = callApi {
        apiService.getCharacterByName(name)
    }
}