package com.gotobootcamp.games.data

import android.util.Log
import com.google.gson.Gson
import com.gotobootcamp.games.model.Game
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class GameRepositoryTest {

    @TempDir
    lateinit var cacheDir: File

    private lateinit var apiService: GameApiService
    private lateinit var repository: GameRepository

    private val mockGames = listOf(
        Game(id = "1", title = "Game 1"),
        Game(id = "2", title = "Game 2")
    )

    @BeforeEach
    fun setup() {
        mockkStatic(Log::class)
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0

        apiService = mockk()
        repository = GameRepository(apiService, cacheDir)
    }

    @Test
    fun givenNetworkAvailable_whenGetGames_thenFetchFromNetworkAndSaveToCache() = runTest {
        coEvery { apiService.getGames() } returns mockGames

        val result = repository.getGames()

        assertEquals(mockGames, result)

        val cacheFile = File(cacheDir, "games_cache.json")
        val cachedContent = cacheFile.readText()
        val expectedJson = Gson().toJson(mockGames)
        assertEquals(expectedJson, cachedContent)

        coVerify(exactly = 1) { apiService.getGames() }
    }

    @Test
    fun givenNetworkUnavailableAndCacheExists_whenGetGames_thenLoadFromCache() = runTest {
        val cacheFile = File(cacheDir, "games_cache.json")
        cacheFile.writeText(Gson().toJson(mockGames))
        coEvery { apiService.getGames() } throws Exception("Network error")

        val result = repository.getGames()

        assertEquals(mockGames, result)
        coVerify(exactly = 1) { apiService.getGames() }
    }

    @Test
    fun givenNetworkUnavailableAndCacheEmpty_whenGetGames_thenThrowException() = runTest {
        coEvery { apiService.getGames() } throws Exception("Network error")

        val result = runCatching {
            repository.getGames()
        }

        assertEquals("Network error", result.exceptionOrNull()?.message)
    }
}
