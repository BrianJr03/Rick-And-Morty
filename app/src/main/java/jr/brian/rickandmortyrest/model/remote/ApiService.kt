package jr.brian.rickandmortyrest.model.remote

import jr.brian.rickandmortyrest.model.local.CharacterResult
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("character/")
    suspend fun getCharacterByName(@Query("name") name: String): Response<CharacterResult>
}