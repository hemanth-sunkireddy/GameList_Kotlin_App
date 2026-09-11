package com.gotobootcamp.games.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.gotobootcamp.games.model.Game

class GameRepository(private val context: Context) {

    fun getGames(): List<Game> {
        val assetManager = context.assets

        val inputStream = assetManager.open("games.json")

        val jsonString = inputStream.bufferedReader().use { it.readText() }
        Log.d("GameRepository", jsonString)

        val gson = Gson()
        val typeToken = object : TypeToken<List<Game>>() {}.type
        val gamesList: List<Game> = gson.fromJson(jsonString, typeToken)

        return gamesList
    }
}
