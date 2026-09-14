package com.gotobootcamp.games.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gotobootcamp.games.data.GameDataStore
import com.gotobootcamp.games.data.GameRepository
import com.gotobootcamp.games.model.Game
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface GameUiState {
    object Loading : GameUiState

    data class Toast(val message: String): GameUiState
    data class Success(val games: List<Game>) : GameUiState
    data class Error(val message: String) : GameUiState
}

sealed interface GameUiEvent {
    data class ShowToast(val message: String) : GameUiEvent
}

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val dataStore = GameDataStore(application)

    val savedGameIds: StateFlow<Set<String>> = dataStore.savedGameIds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )
    private val repository = GameRepository()
    private val _uiState = MutableStateFlow<GameUiState>(GameUiState.Loading)
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()
    private val _uiEvent = MutableSharedFlow<GameUiEvent>()
    val uiEvent: SharedFlow<GameUiEvent> = _uiEvent.asSharedFlow()

    init {
        loadGames()
    }

    fun loadGames() {
        viewModelScope.launch {
            _uiState.value = GameUiState.Loading
            try {
                val games = repository.getGames()

                _uiState.value = GameUiState.Success(games)
                _uiEvent.emit(GameUiEvent.ShowToast("Games loaded successfully"))
            } catch (e: Exception) {
                _uiState.value = GameUiState.Error(e.message ?: "Failed to load games")
            }
        }
    }

    fun onBookmarkClicked(gameId: String) {
        viewModelScope.launch {
            dataStore.toggleSaveGame(gameId)
        }
    }
}
