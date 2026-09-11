package com.gotobootcamp.games.data

import com.gotobootcamp.games.model.Game
import retrofit2.http.GET

interface GameApiService {
    @GET("api1/games")
    suspend fun getGames(): List<Game>
}
