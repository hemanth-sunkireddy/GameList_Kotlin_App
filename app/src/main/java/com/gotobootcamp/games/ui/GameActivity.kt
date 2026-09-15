package com.gotobootcamp.games.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.gotobootcamp.games.R
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.gotobootcamp.games.model.Game
import com.gotobootcamp.games.ui.theme.GamesTheme

class GameActivity : ComponentActivity() {
    private val viewModel: GameViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            GamesTheme {
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                val context = LocalContext.current

                LaunchedEffect(Unit) {
                    viewModel.uiEvent.collect { event ->
                        when (event) {
                            is GameUiEvent.ShowToast -> {
                                Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }

                when (val current = state) {
                    is GameUiState.Loading -> CircularProgressIndicator(
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
                    )
                    is GameUiState.Success -> GameUI(
                        games = current.games,
                        savedIds = current.savedGameIds,
                        onBookmarkClick = { gameId -> viewModel.onBookmarkClicked(gameId) }
                    )
                    is GameUiState.Error -> Text(
                        text = current.message,
                        fontSize = dimensionResource(id = R.dimen.text_size_error).value.sp,
                        modifier = Modifier.padding(dimensionResource(id = R.dimen.padding_large))
                    )
                }
            }
        }
    }
}

private fun String?.isNullOrEmptyOrBlank(): Boolean = this.isNullOrBlank()

@Composable
fun GameUI(
    games: List<Game>,
    savedIds: Set<String>,
    onBookmarkClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.padding(
            top = dimensionResource(id = R.dimen.padding_large),
            start = dimensionResource(id = R.dimen.padding_medium),
            end = dimensionResource(id = R.dimen.padding_medium)
        )
    ) {
        item {
            Text(
                text = stringResource(id = R.string.label_games),
                fontSize = dimensionResource(id = R.dimen.text_size_header).value.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = dimensionResource(id = R.dimen.padding_medium))
            )
        }
        items(games) { game ->
            GameItemCard(
                game = game,
                isSaved = savedIds.contains(game.id),
                onBookmarkClick = { onBookmarkClick(game.id) }
            )
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun GameItemCard(
    game: Game,
    isSaved: Boolean,
    onBookmarkClick: () -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = dimensionResource(id = R.dimen.padding_extra_small))
            .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_medium)))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(dimensionResource(id = R.dimen.padding_small))
    ) {
        GlideImage(
            model = game.thumbnail ?: "",
            contentDescription = game.title ?: stringResource(id = R.string.desc_game_thumbnail),
            modifier = Modifier
                .fillMaxWidth(0.25f)
                .height(dimensionResource(id = R.dimen.game_thumbnail_height))
                .clip(RoundedCornerShape(dimensionResource(id = R.dimen.corner_radius_small))),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.padding(start = dimensionResource(id = R.dimen.padding_small)))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = game.title ?: stringResource(id = R.string.fallback_untitled),
                fontSize = dimensionResource(id = R.dimen.text_size_title).value.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))

            Text(
                text = game.shortDescription ?: "",
                fontSize = dimensionResource(id = R.dimen.text_size_body).value.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_medium)))

            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(id = R.string.label_genre, game.genre ?: stringResource(id = R.string.fallback_na)),
                    fontSize = dimensionResource(id = R.dimen.text_size_caption).value.sp,
                )
                Text(
                    text = stringResource(id = R.string.label_platform, game.platform ?: stringResource(id = R.string.fallback_na)),
                    fontSize = dimensionResource(id = R.dimen.text_size_caption).value.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))

            Text(
                text = stringResource(id = R.string.label_developer, game.developer ?: stringResource(id = R.string.fallback_unknown)),
                fontSize = dimensionResource(id = R.dimen.text_size_caption).value.sp,
                color = MaterialTheme.colorScheme.outline
            )

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_extra_small)))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = stringResource(id = R.string.label_release_date, game.releaseDate ?: stringResource(id = R.string.fallback_na)),
                    fontSize = dimensionResource(id = R.dimen.text_size_caption).value.sp,
                    color = MaterialTheme.colorScheme.outline
                )

                IconButton(onClick = onBookmarkClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.outline_award_star_24),
                        contentDescription = stringResource(id = R.string.desc_favourite_game),
                        tint = if (isSaved) colorResource(id = R.color.gold) else MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_extra_small)))

            Text(
                text = stringResource(id = R.string.label_publisher, game.publisher ?: stringResource(id = R.string.fallback_unknown)),
                fontSize = dimensionResource(id = R.dimen.text_size_caption).value.sp,
                color = MaterialTheme.colorScheme.outline
            )

            if (!game.gameUrl.isNullOrEmptyOrBlank() || !game.profileUrl.isNullOrEmptyOrBlank()) {
                Spacer(modifier = Modifier.height(dimensionResource(id = R.dimen.spacing_small)))

                Row(modifier = Modifier.fillMaxWidth()) {
                    if (!game.gameUrl.isNullOrEmptyOrBlank()) {
                        Text(
                            text = stringResource(id = R.string.label_game_url),
                            fontSize = dimensionResource(id = R.dimen.text_size_caption).value.sp,
                            color = colorResource(id = R.color.blue),
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(game.gameUrl))
                                context.startActivity(intent)
                            }
                        )
                    }

                    if (!game.gameUrl.isNullOrEmptyOrBlank() && !game.profileUrl.isNullOrBlank()) {
                        Text(
                            text = stringResource(id = R.string.label_separator),
                            fontSize = dimensionResource(id = R.dimen.text_size_caption).value.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    if (!game.profileUrl.isNullOrEmptyOrBlank()) {
                        Text(
                            text = stringResource(id = R.string.label_profile_url),
                            fontSize = dimensionResource(id = R.dimen.text_size_caption).value.sp,
                            color = colorResource(id = R.color.blue),
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
                publisher = "CD Project",
                developer = "CD Project Red",
                releaseDate = "2020-12-10",
                profileUrl = ""
            ),
            Game(
                id = "2",
                title = "Apex Legends",
                thumbnail = "",
                shortDescription = "Battle Royal",
                gameUrl = "",
                genre = "Shooter",
                platform = "PC",
                publisher = "EA",
                developer = "Respawn",
                releaseDate = "2019-02-04",
                profileUrl = ""
            )
        )

        GameUI(games = mockGames, savedIds = emptySet(), onBookmarkClick = {})
    }
}
