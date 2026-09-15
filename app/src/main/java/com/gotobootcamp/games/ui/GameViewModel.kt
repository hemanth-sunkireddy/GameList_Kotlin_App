package com.gotobootcamp.games.ui

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.gotobootcamp.games.R
import com.gotobootcamp.games.data.GameDataStore
import com.gotobootcamp.games.data.GameRepository
import com.gotobootcamp.games.data.GameApiService
import com.gotobootcamp.games.model.Game
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface GameUiState {
    object Loading : GameUiState

    data class Toast(val message: String): GameUiState
    data class Success(val games: List<Game>) : GameUiState
    data class Error(val message: String) : GameUiState
}

sealed interface GameUiEvent {
    data class ShowToast(val message: String) : GameUiEvent
}

class GameViewModel @JvmOverloads constructor(
    application: Application,
    private val repository: GameRepository = GameRepository(
        apiService = Retrofit.Builder()
            .baseUrl(application.getString(R.string.base_url))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GameApiService::class.java),
        cacheDir = application.cacheDir
    ),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : AndroidViewModel(application) {
    private val TAG = application.getString(R.string.log_tag_viewmodel)

    private val dataStore = GameDataStore(application)

    val savedGameIds: StateFlow<Set<String>> = dataStore.savedGameIds
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptySet()
        )
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
            Log.d(TAG, "Before withContext - Running on thread: ${Thread.currentThread().name}")
            try {
                val games = withContext(ioDispatcher) {
                    Log.d(TAG, "Inside withContext - Fetching games on thread: ${Thread.currentThread().name}")
                    repository.getGames()
                }
                Log.d(TAG, "After withContext - Resumed on thread: ${Thread.currentThread().name}")
                _uiState.value = GameUiState.Success(games)
                _uiEvent.emit(GameUiEvent.ShowToast(getApplication<Application>().getString(R.string.toast_games_loaded)))
            } catch (e: Exception) {
                _uiState.value = GameUiState.Error(e.message ?: getApplication<Application>().getString(
                    R.string.error_failed_to_load))
            }
        }
    }

    fun onBookmarkClicked(gameId: String) {
        viewModelScope.launch {
            dataStore.toggleSaveGame(gameId)
        }
    }
}
