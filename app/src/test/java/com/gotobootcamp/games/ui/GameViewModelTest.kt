package com.gotobootcamp.games.ui

import android.app.Application
import android.util.Log
import com.gotobootcamp.games.R
import com.gotobootcamp.games.data.GameRepository
import com.gotobootcamp.games.model.Game
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GameViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var application: Application
    private lateinit var repository: GameRepository
    private lateinit var viewModel: GameViewModel

    private val mockGames = listOf(
        Game(id = "1", title = "Game 1")
    )

    @BeforeEach
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        Dispatchers.setMain(testDispatcher)
        application = mockk(relaxed = true)
        repository = mockk()

        every { application.getString(R.string.log_tag_viewmodel) } returns "GameViewModel"
        every { application.getString(R.string.toast_games_loaded) } returns "Games loaded successfully"
        every { application.getString(R.string.error_failed_to_load) } returns "Failed to load games"
    }

    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun givenViewModelInitialized_whenLoadGamesStarts_thenSetLoadingState() = runTest(testDispatcher) {
        coEvery { repository.getGames() } returns mockGames

        viewModel = GameViewModel(application, repository, testDispatcher)

        // Initial state is Loading before scheduled coroutines execute
        assertTrue(viewModel.uiState.value is GameUiState.Loading)
    }

    @Test
    fun givenRepositoryReturnsGames_whenLoadGamesCompletes_thenSetSuccessState() = runTest(testDispatcher) {
        coEvery { repository.getGames() } returns mockGames

        viewModel = GameViewModel(application, repository, testDispatcher)

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is GameUiState.Success)
        assertEquals(mockGames, (viewModel.uiState.value as GameUiState.Success).games)
    }

    @Test
    fun givenRepositoryThrowsException_whenLoadGamesFails_thenSetErrorState() = runTest(testDispatcher) {
        val errorMessage = "Network error"
        coEvery { repository.getGames() } throws Exception(errorMessage)

        viewModel = GameViewModel(application, repository, testDispatcher)

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is GameUiState.Error)
        assertEquals(errorMessage, (viewModel.uiState.value as GameUiState.Error).message)
    }
}
