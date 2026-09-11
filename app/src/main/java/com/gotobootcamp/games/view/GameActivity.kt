package com.gotobootcamp.games.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.gotobootcamp.games.data.GameRepository
import com.gotobootcamp.games.model.Game
import com.gotobootcamp.games.ui.theme.GamesTheme

class GameActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val repository = GameRepository(this)
        val games = repository.getGames()
        setContent {
            GamesTheme {
                GameUI(games = games)
            }
        }
    }
}

@Composable
fun GameUI(games: List<Game>) {
    Text(
        text = "Total games: ${games.size}",
        modifier = Modifier.padding(50.dp),
        fontSize = 30.sp
    )
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
