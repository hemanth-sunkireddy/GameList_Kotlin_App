package com.gotobootcamp.games.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.gotobootcamp.games.model.Game
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GameRepository() {

    private val apiService: GameApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.mmobomb.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GameApiService::class.java)
    }

    suspend fun getGames(): List<Game> {
        val gamesList = apiService.getGames()
        Log.d("GameRepository", Gson().toJson(gamesList))
        return gamesList
    }
}
