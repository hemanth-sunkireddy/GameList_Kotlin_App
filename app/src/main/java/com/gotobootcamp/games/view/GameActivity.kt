package com.gotobootcamp.games.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.gotobootcamp.games.model.Game
import com.gotobootcamp.games.ui.theme.GamesTheme
import com.gotobootcamp.games.viewModel.GameUiState
import com.gotobootcamp.games.viewModel.GameViewModel

class GameActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GamesTheme {
                val state by viewModel.uiState.collectAsStateWithLifecycle()

                when (val current = state) {
                    is GameUiState.Loading -> CircularProgressIndicator(modifier = Modifier.padding(50.dp))
                    is GameUiState.Success -> GameUI(games = current.games)
                    is GameUiState.Error -> Text(text = current.message, fontSize = 20.sp, modifier = Modifier.padding(50.dp))
                }
            }
        }
    }
}

@Composable
fun GameUI(games: List<Game>) {
    LazyColumn(
        modifier = Modifier.padding(top = 50.dp, start = 16.dp, end = 16.dp)
    ) {
        item {
            Text(
                text = "Games",
                fontSize = 30.sp,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        items(games) { game ->
            Text(
                text = "Game ID: ${game.id}",
                fontSize = 20.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GamesPreview() {
    GamesTheme {
        val mockGames = listOf(
            Game(
                id = "1",
                title = "Cyberpunk 2077",
                thumbnail = "",
                shortDescription = "Open-world RPG",
                gameUrl = "",
                genre = "RPG",
                platform = "PC",
                publisher = "CD Projekt",
                developer = "CD Projekt Red",
                releaseDate = "2020-12-10",
                profileUrl = ""
            ),
            Game(
                id = "2",
                title = "Apex Legends",
                thumbnail = "",
                shortDescription = "Battle Royale",
                gameUrl = "",
                genre = "Shooter",
                platform = "PC",
                publisher = "EA",
                developer = "Respawn",
                releaseDate = "2019-02-04",
                profileUrl = ""
            )
        )

        GameUI(games = mockGames)
    }
}
