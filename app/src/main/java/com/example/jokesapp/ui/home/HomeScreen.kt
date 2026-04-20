package com.example.jokesapp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
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
import com.example.jokesapp.model.JokeCategory
import com.example.jokesapp.model.mockJokes
import com.example.jokesapp.ui.components.JokeCard
import com.example.jokesapp.ui.theme.JokesAppTheme
import com.example.jokesapp.viewmodel.JokeUiState
import com.example.jokesapp.viewmodel.JokeViewModel

@Composable
fun HomeScreen(outerPadding: PaddingValues = PaddingValues(), viewModel: JokeViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()

    HomeScreenContent(
        uiState = uiState,
        favorites = favorites,
        selectedCategory = selectedCategory,
        onSelectCategory = viewModel::selectCategory,
        onFetchJoke = { viewModel.fetchJoke() },
        onToggleFavorite = viewModel::toggleFavorite,
        outerPadding = outerPadding
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    uiState: JokeUiState,
    favorites: Set<Int>,
    selectedCategory: JokeCategory?,
    onSelectCategory: (JokeCategory?) -> Unit,
    onFetchJoke: () -> Unit,
    onToggleFavorite: (Int) -> Unit,
    outerPadding: PaddingValues
) {
    val categories = JokeCategory.entries.toList()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.app_name)) })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = outerPadding.calculateBottomPadding())
                .padding(horizontal = dimensionResource(R.dimen.padding_medium)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium))
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small) / 2))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.chip_spacing))) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { onSelectCategory(null) },
                        label = { Text(stringResource(R.string.filter_all)) }
                    )
                }
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { onSelectCategory(category) },
                        label = { Text(stringResource(category.labelRes)) }
                    )
                }
            }

            when (uiState) {
                is JokeUiState.Loading -> {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is JokeUiState.Success -> {
                    val joke = uiState.joke
                    JokeCard(
                        joke = joke,
                        isFavorite = joke.id in favorites,
                        onFavoriteToggle = { onToggleFavorite(joke.id) }
                    )
                }
                is JokeUiState.Error -> {
                    Box(
                        modifier = Modifier.weight(1f).fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = uiState.message,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
                            Button(onClick = onFetchJoke) {
                                Text(stringResource(R.string.btn_retry))
                            }
                        }
                    }
                }
            }

            Button(
                onClick = onFetchJoke,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(stringResource(R.string.btn_get_joke))
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))
        }
    }
}

@Preview(showBackground = true, name = "HomeScreen - Success")
@Composable
private fun HomeScreenSuccessPreview() {
    JokesAppTheme {
        HomeScreenContent(
            uiState = JokeUiState.Success(mockJokes.first()),
            favorites = emptySet(),
            selectedCategory = null,
            onSelectCategory = {},
            onFetchJoke = {},
            onToggleFavorite = {},
            outerPadding = PaddingValues()
        )
    }
}

@Preview(showBackground = true, name = "HomeScreen - Loading")
@Composable
private fun HomeScreenLoadingPreview() {
    JokesAppTheme {
        HomeScreenContent(
            uiState = JokeUiState.Loading,
            favorites = emptySet(),
            selectedCategory = null,
            onSelectCategory = {},
            onFetchJoke = {},
            onToggleFavorite = {},
            outerPadding = PaddingValues()
        )
    }
}

@Preview(showBackground = true, name = "HomeScreen - Error")
@Composable
private fun HomeScreenErrorPreview() {
    JokesAppTheme {
        HomeScreenContent(
            uiState = JokeUiState.Error("No connection. Try again!"),
            favorites = emptySet(),
            selectedCategory = null,
            onSelectCategory = {},
            onFetchJoke = {},
            onToggleFavorite = {},
            outerPadding = PaddingValues()
        )
    }
}
