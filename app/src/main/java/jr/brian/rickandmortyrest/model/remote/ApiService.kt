package jr.brian.rickandmortyrest.model.remote

import jr.brian.rickandmortyrest.model.local.Character
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("character/{id}")
    suspend fun getCharacterById(@Path("id") id: String): Response<Character>

    @GET("character/")
    suspend fun getCharacterByName(@Query("name") name: String): Response<Character>
}