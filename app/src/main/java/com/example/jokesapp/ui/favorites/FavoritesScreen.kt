package com.example.jokesapp.ui.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.jokesapp.R
import com.example.jokesapp.model.Joke
import com.example.jokesapp.model.mockJokes
import com.example.jokesapp.ui.components.JokeCard
import com.example.jokesapp.ui.theme.JokesAppTheme
import com.example.jokesapp.viewmodel.JokeViewModel

@Composable
fun FavoritesScreen(
    outerPadding: PaddingValues = PaddingValues(),
    viewModel: JokeViewModel,
    onGoFindJokes: () -> Unit
) {
    val favoriteJokes by viewModel.favoriteJokes.collectAsState()
    val favorites by viewModel.favorites.collectAsState()

    FavoritesScreenContent(
        favoriteJokes = favoriteJokes,
        favorites = favorites,
        onToggleFavorite = viewModel::toggleFavorite,
        onGoFindJokes = onGoFindJokes,
        outerPadding = outerPadding
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavoritesScreenContent(
    favoriteJokes: List<Joke>,
    favorites: Set<Int>,
    onToggleFavorite: (Int) -> Unit,
    onGoFindJokes: () -> Unit,
    outerPadding: PaddingValues
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("${stringResource(R.string.nav_favorites)} (${favoriteJokes.size})") })
        }
    ) { innerPadding ->
        if (favoriteJokes.isEmpty()) {
            EmptyFavorites(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(bottom = outerPadding.calculateBottomPadding()),
                onGoFindJokes = onGoFindJokes
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(
                    start = dimensionResource(R.dimen.padding_medium),
                    end = dimensionResource(R.dimen.padding_medium),
                    top = dimensionResource(R.dimen.padding_medium),
                    bottom = dimensionResource(R.dimen.padding_medium) + outerPadding.calculateBottomPadding()
                ),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.card_spacing)),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(favoriteJokes) { joke ->
                    JokeCard(
                        joke = joke,
                        isFavorite = joke.id in favorites,
                        onFavoriteToggle = { onToggleFavorite(joke.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyFavorites(
    modifier: Modifier = Modifier,
    onGoFindJokes: () -> Unit
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Favorite,
            contentDescription = null,
            modifier = Modifier.size(dimensionResource(R.dimen.empty_icon_size)),
            tint = MaterialTheme.colorScheme.outlineVariant
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
        Text(
            text = stringResource(R.string.no_favorites_title),
            style = MaterialTheme.typography.titleLarge
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
        Text(
            text = stringResource(R.string.no_favorites_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_large)))
        Button(onClick = onGoFindJokes) {
            Text(stringResource(R.string.btn_go_find_jokes))
        }
    }
}

@Preview(showBackground = true, name = "FavoritesScreen - Empty")
@Composable
private fun FavoritesEmptyPreview() {
    JokesAppTheme {
        FavoritesScreenContent(
            favoriteJokes = emptyList(),
            favorites = emptySet(),
            onToggleFavorite = {},
            onGoFindJokes = {},
            outerPadding = PaddingValues()
        )
    }
}

@Preview(showBackground = true, name = "FavoritesScreen - With jokes")
@Composable
private fun FavoritesWithJokesPreview() {
    JokesAppTheme {
        FavoritesScreenContent(
            favoriteJokes = mockJokes.take(2),
            favorites = setOf(mockJokes[0].id, mockJokes[1].id),
            onToggleFavorite = {},
            onGoFindJokes = {},
            outerPadding = PaddingValues()
        )
    }
}
