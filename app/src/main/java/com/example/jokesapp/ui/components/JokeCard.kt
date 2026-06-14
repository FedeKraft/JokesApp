package com.example.jokesapp.ui.components

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jokesapp.R
import com.example.jokesapp.model.Joke
import com.example.jokesapp.model.JokeRarity
import com.example.jokesapp.model.mockJokes
import com.example.jokesapp.ui.theme.JokesAppTheme

private fun rarityColor(rarity: JokeRarity): Color = when (rarity) {
    JokeRarity.COMMON    -> Color.Transparent
    JokeRarity.RARE      -> Color(0xFF1565C0)
    JokeRarity.EPIC      -> Color(0xFF6A1B9A)
    JokeRarity.LEGENDARY -> Color(0xFFFFD700)
}

@Composable
fun JokeCard(
    joke: Joke,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    rarity: JokeRarity = JokeRarity.COMMON,
    modifier: Modifier = Modifier,
) {
    val color = rarityColor(rarity)
    val cornerRadius = dimensionResource(R.dimen.card_corner_radius)

    ElevatedCard(
        shape = RoundedCornerShape(cornerRadius),
        modifier = modifier
            .fillMaxWidth()
            .animateContentSize()
            .then(
                if (rarity != JokeRarity.COMMON)
                    Modifier.border(2.dp, color, RoundedCornerShape(cornerRadius))
                else Modifier
            ),
    ) {
        Column(modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium))) {

            // Header row: category chip | rarity badge | favorite button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                SuggestionChip(
                    onClick = {},
                    label = {
                        Text(
                            text = stringResource(joke.category.labelRes),
                            style = MaterialTheme.typography.labelSmall,
                        )
                    },
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        labelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    ),
                    border = null,
                )
                if (rarity != JokeRarity.COMMON) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${rarity.emoji} ${rarity.label}",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = color,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onFavoriteToggle) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = if (isFavorite) {
                            stringResource(R.string.cd_remove_favorite)
                        } else {
                            stringResource(R.string.cd_add_favorite)
                        },
                        tint = if (isFavorite) MaterialTheme.colorScheme.primary
                               else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.padding_small)))

            when (joke) {
                is Joke.Single -> Text(
                    text = joke.joke,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                )
                is Joke.TwoPart -> {
                    Text(text = joke.setup, style = MaterialTheme.typography.bodyLarge)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "💀  ${joke.delivery}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Common")
@Composable
private fun CommonPreview() {
    JokesAppTheme { JokeCard(joke = mockJokes.first(), isFavorite = false, onFavoriteToggle = {}, rarity = JokeRarity.COMMON) }
}

@Preview(showBackground = true, name = "Legendary")
@Composable
private fun LegendaryPreview() {
    JokesAppTheme { JokeCard(joke = mockJokes.first(), isFavorite = true, onFavoriteToggle = {}, rarity = JokeRarity.LEGENDARY) }
}
