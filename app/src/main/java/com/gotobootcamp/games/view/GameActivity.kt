package com.gotobootcamp.games.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
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

private fun String?.isNullOrEmptyOrBlank(): Boolean = this.isNullOrBlank()


@Composable
fun GameUI(games: List<Game>) {
    LazyColumn(
        modifier = Modifier.padding(top = 50.dp, start = 16.dp, end = 16.dp)
    ) {
        item {
            Text(
                text = "Games",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }
        items(games) { game ->
            GameItemCard(game = game)
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GameItemCard(game: Game) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp)
    ) {
        GlideImage(
            model = game.thumbnail ?: "",
            contentDescription = game.title ?: "Game Thumbnail",
            modifier = Modifier
                .fillMaxWidth(0.25f)
                .height(90.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.padding(start = 12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = game.title ?: "Untitled",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = game.shortDescription ?: "",
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Genre: ${game.genre ?: "N/A"}",
                    fontSize = 11.sp,
                )
                Text(
                    text = "  •  Platform: ${game.platform ?: "N/A"}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Developer: ${game.developer ?: "Unknown"}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Release Date: ${game.releaseDate ?: "N/A"}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "Publisher: ${game.publisher ?: "Unknown"}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.outline
            )

            if (!game.gameUrl.isNullOrEmptyOrBlank() || !game.profileUrl.isNullOrEmptyOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    if (!game.gameUrl.isNullOrEmptyOrBlank()) {
                        Text(
                            text = "Game URL",
                            fontSize = 11.sp,
                            color = Color.Blue,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(game.gameUrl))
                                context.startActivity(intent)
                            }
                        )
                    }

                    if (!game.gameUrl.isNullOrEmptyOrBlank() && !game.profileUrl.isNullOrBlank()) {
                        Text(
                            text = "  •  ",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    if (!game.profileUrl.isNullOrEmptyOrBlank()) {
                        Text(
                            text = "Profile URL",
                            fontSize = 11.sp,
                            color = Color.Blue,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(game.profileUrl))
                                context.startActivity(intent)
                            }
                        )
                    }
                }
            }
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
