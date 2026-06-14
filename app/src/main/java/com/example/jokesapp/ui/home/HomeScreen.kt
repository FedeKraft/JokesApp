package com.example.jokesapp.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.jokesapp.R
import com.example.jokesapp.model.JokeCategory
import com.example.jokesapp.model.JokeRarity
import com.example.jokesapp.model.mockJokes
import com.example.jokesapp.ui.components.JokeCard
import com.example.jokesapp.ui.theme.JokesAppTheme
import com.example.jokesapp.viewmodel.JokeUiState
import com.example.jokesapp.viewmodel.JokeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

private val curseLabels = listOf(
    "Get Joke", "More Joke", "JOKE ME", "FEED ME",
    "I NEED IT", "GIVE 🍪", "MORE", "PLEASE",
    "CAN'T STOP", "SEND HELP", "MUST CLICK", "NO THOUGHTS",
    "BRAIN EMPTY", "JOKE NOW", "💀", "YES",
)

@Composable
fun HomeScreen(outerPadding: PaddingValues = PaddingValues(), viewModel: JokeViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val favorites by viewModel.favorites.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val jokesRead by viewModel.jokesRead.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val fallbackMessage = stringResource(R.string.spanish_jokes_unavailable)

    LaunchedEffect(Unit) {
        launch { viewModel.spanishFallbackEvent.collect { snackbarHostState.showSnackbar(fallbackMessage) } }
        launch { viewModel.milestoneEvent.collect { snackbarHostState.showSnackbar(it) } }
    }

    HomeScreenContent(
        uiState = uiState,
        favorites = favorites,
        selectedCategory = selectedCategory,
        jokesRead = jokesRead,
        floatTextEvent = viewModel.floatTextEvent,
        cursedEscapeEvent = viewModel.cursedEscapeEvent,
        memeEvent = viewModel.memeEvent,
        onSelectCategory = viewModel::selectCategory,
        onFetchJoke = { viewModel.fetchJoke() },
        onToggleFavorite = viewModel::toggleFavorite,
        outerPadding = outerPadding,
        snackbarHostState = snackbarHostState,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreenContent(
    uiState: JokeUiState,
    favorites: Set<Int>,
    selectedCategory: JokeCategory?,
    jokesRead: Int,
    floatTextEvent: SharedFlow<String> = MutableSharedFlow(),
    cursedEscapeEvent: SharedFlow<Unit> = MutableSharedFlow(),
    memeEvent: SharedFlow<String> = MutableSharedFlow(),
    onSelectCategory: (JokeCategory?) -> Unit,
    onFetchJoke: () -> Unit,
    onToggleFavorite: (Int) -> Unit,
    outerPadding: PaddingValues,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    var floatText by remember { mutableStateOf("") }
    var showFloat by remember { mutableStateOf(false) }
    var escapeX by remember { mutableFloatStateOf(0f) }
    var escapeY by remember { mutableFloatStateOf(0f) }
    val animEscapeX by animateFloatAsState(escapeX, spring(Spring.DampingRatioMediumBouncy), label = "ex")
    val animEscapeY by animateFloatAsState(escapeY, spring(Spring.DampingRatioMediumBouncy), label = "ey")
    var currentMemeFile by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        launch {
            floatTextEvent.collect { text ->
                floatText = text
                showFloat = true
                delay(350)
                showFloat = false
            }
        }
        launch {
            cursedEscapeEvent.collect {
                escapeX = listOf(-110f, -80f, 80f, 110f).random()
                escapeY = listOf(-35f, 35f).random()
                delay(1400)
                escapeX = 0f
                escapeY = 0f
            }
        }
        launch {
            memeEvent.collect { filename -> currentMemeFile = filename }
        }
    }

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val buttonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.88f else 1f,
        animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMedium),
        label = "scale",
    )
    val buttonLabel = curseLabels[jokesRead % curseLabels.size]

    if (currentMemeFile != null) {
        Dialog(
            onDismissRequest = { currentMemeFile = null },
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.95f))
                    .clickable { currentMemeFile = null },
                contentAlignment = Alignment.Center,
            ) {
                AsyncImage(
                    model = "file:///android_asset/memes/$currentMemeFile",
                    contentDescription = "meme",
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentScale = ContentScale.Fit,
                )
                Text(
                    text = "tap to close",
                    color = Color.White.copy(alpha = 0.5f),
                    style = MaterialTheme.typography.labelSmall,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(stringResource(R.string.app_name), fontWeight = FontWeight.ExtraBold)
            })
        },
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                )
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = outerPadding.calculateBottomPadding())
                .padding(horizontal = dimensionResource(R.dimen.padding_medium)),
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.chip_spacing))) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { onSelectCategory(null) },
                        label = { Text(stringResource(R.string.filter_all)) },
                    )
                }
                items(JokeCategory.entries.toList()) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { onSelectCategory(category) },
                        label = { Text(stringResource(category.labelRes)) },
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))

            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                when (uiState) {
                    is JokeUiState.Loading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                    is JokeUiState.Success -> JokeCard(
                        joke = uiState.joke,
                        rarity = uiState.rarity,
                        isFavorite = uiState.joke.id in favorites,
                        onFavoriteToggle = { onToggleFavorite(uiState.joke.id) },
                    )
                    is JokeUiState.Error -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("😵", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(uiState.message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_medium)))
                        Button(onClick = onFetchJoke) { Text(stringResource(R.string.btn_retry)) }
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Column(
                modifier = Modifier.fillMaxWidth().height(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                AnimatedVisibility(
                    visible = showFloat,
                    enter = fadeIn(tween(80)),
                    exit = fadeOut(tween(500)) + slideOutVertically { -it * 5 },
                ) {
                    Text(
                        text = floatText,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                    )
                }
            }

            Button(
                onClick = onFetchJoke,
                interactionSource = interactionSource,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .scale(buttonScale)
                    .offset(x = animEscapeX.dp, y = animEscapeY.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
            ) {
                Text(text = buttonLabel, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
            uiState = JokeUiState.Success(mockJokes.first(), JokeRarity.LEGENDARY),
            favorites = emptySet(),
            selectedCategory = null,
            jokesRead = 7,
            onSelectCategory = {},
            onFetchJoke = {},
            onToggleFavorite = {},
            outerPadding = PaddingValues(),
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
            jokesRead = 0,
            onSelectCategory = {},
            onFetchJoke = {},
            onToggleFavorite = {},
            outerPadding = PaddingValues(),
        )
    }
}
