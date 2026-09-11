package com.gotobootcamp.games.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gotobootcamp.games.data.GameRepository
import com.gotobootcamp.games.model.Game
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

sealed interface GameUiState {
    object Loading : GameUiState
    data class Success(val games: List<Game>) : GameUiState
    data class Error(val message: String) : GameUiState
}

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = GameRepository()
    private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Loading)
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        loadGames()
    }

    fun loadGames() {
        viewModelScope.launch {
            _uiState.value = GameUiState.Loading
            try {
                setDelay(2.seconds)
                val games = repository.getGames()
                _uiState.value = GameUiState.Success(games)
            } catch (e: Exception) {
                _uiState.value = GameUiState.Error(e.message ?: "Failed to load games")
            }
        }
    }

    private suspend fun setDelay(duration: Duration) {
        delay(duration)
    }
}
