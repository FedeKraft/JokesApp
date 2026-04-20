package com.example.jokesapp.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import com.example.jokesapp.R
import com.example.jokesapp.model.Joke
import com.example.jokesapp.model.mockJokes
import com.example.jokesapp.ui.theme.JokesAppTheme

@Composable
fun JokeCard(
    joke: Joke,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        shape = RoundedCornerShape(dimensionResource(R.dimen.card_corner_radius)),
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SuggestionChip(
                    onClick = {},
                    label = {
                        Text(
                            text = stringResource(joke.category.labelRes),
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onFavoriteToggle) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorite) {
                            stringResource(R.string.cd_remove_favorite)
                        } else {
                            stringResource(R.string.cd_add_favorite)
                        },
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))

            when (joke) {
                is Joke.Single -> {
                    Text(
                        text = joke.joke,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                is Joke.TwoPart -> {
                    Text(
                        text = joke.setup,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    HorizontalDivider(modifier = Modifier.padding(vertical = dimensionResource(R.dimen.padding_small)))
                    Text(
                        text = joke.delivery,
                        style = MaterialTheme.typography.bodyLarge,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "JokeCard - TwoPart")
@Composable
private fun JokeCardTwoPartPreview() {
    JokesAppTheme {
        JokeCard(
            joke = mockJokes.first(),
            isFavorite = false,
            onFavoriteToggle = {}
        )
    }
}

@Preview(showBackground = true, name = "JokeCard - Favorited")
@Composable
private fun JokeCardFavoritedPreview() {
    JokesAppTheme {
        JokeCard(
            joke = mockJokes[1],
            isFavorite = true,
            onFavoriteToggle = {}
        )
    }
}
