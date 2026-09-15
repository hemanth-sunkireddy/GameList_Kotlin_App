package com.gotobootcamp.games.data

import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gotobootcamp.games.model.Game
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class GameRepository(
    private val apiService: GameApiService,
    private val cacheDir: File
) {

    private val gson = Gson()
    private val cacheFileName = "games_cache.json"

    suspend fun getGames(): List<Game> {
        return try {
            val gamesList = apiService.getGames()
            saveToCache(gamesList)
            gamesList
        } catch (e: Exception) {
            Log.e("GameRepository", "Network fetch failed, loading from cache", e)
            loadFromCache() ?: throw e
        }
    }

    private fun saveToCache(games: List<Game>) {
        try {
            val json = gson.toJson(games)
            File(cacheDir, cacheFileName).writeText(json)
        } catch (e: Exception) {
            Log.e("GameRepository", "Failed to save to cache", e)
        }
    }

    private fun loadFromCache(): List<Game>? {
        return try {
            val file = File(cacheDir, cacheFileName)
            if (file.exists()) {
                val json = file.readText()
                val type = object : TypeToken<List<Game>>() {}.type
                gson.fromJson(json, type)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("GameRepository", "Failed to load from cache", e)
            null
        }
    }
}
