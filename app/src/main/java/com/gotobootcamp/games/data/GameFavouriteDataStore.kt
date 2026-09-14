package com.gotobootcamp.games.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "game_preferences")

class GameDataStore(private val context: Context) {
    companion object {
        private val SAVED_GAMES_KEY = stringSetPreferencesKey("favourite_game_ids")
    }

    val savedGameIds: Flow<Set<String>> = context.dataStore.data.map { preferences ->
        preferences[SAVED_GAMES_KEY] ?: emptySet()
    }

    suspend fun toggleSaveGame(gameId: String) {
        context.dataStore.edit { preferences ->
            val currentSet = preferences[SAVED_GAMES_KEY] ?: emptySet()
            if (currentSet.contains(gameId)) {
                preferences[SAVED_GAMES_KEY] = currentSet - gameId
            } else {
                preferences[SAVED_GAMES_KEY] = currentSet + gameId
            }
        }
    }
}
