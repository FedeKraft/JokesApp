package com.example.jokesapp.ui.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.jokesapp.R
import com.example.jokesapp.model.JokeCategory
import com.example.jokesapp.ui.theme.ChristmasColor
import com.example.jokesapp.ui.theme.JokesAppTheme
import com.example.jokesapp.ui.theme.MiscColor
import com.example.jokesapp.ui.theme.ProgrammingColor
import com.example.jokesapp.ui.theme.PunColor
import com.example.jokesapp.ui.theme.SpookyColor

data class CategoryItem(
    val category: JokeCategory,
    val icon: ImageVector,
    val color: Color
)

private val categoryItems = listOf(
    CategoryItem(JokeCategory.PROGRAMMING, Icons.Filled.Code, ProgrammingColor),
    CategoryItem(JokeCategory.MISC, Icons.Filled.Shuffle, MiscColor),
    CategoryItem(JokeCategory.PUN, Icons.Filled.EmojiEmotions, PunColor),
    CategoryItem(JokeCategory.SPOOKY, Icons.Filled.AutoAwesome, SpookyColor),
    CategoryItem(JokeCategory.CHRISTMAS, Icons.Filled.CardGiftcard, ChristmasColor)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(
    outerPadding: PaddingValues = PaddingValues(),
    onCategoryClick: (JokeCategory) -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(stringResource(R.string.nav_categories)) })
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = dimensionResource(R.dimen.padding_medium),
                end = dimensionResource(R.dimen.padding_medium),
                top = dimensionResource(R.dimen.padding_medium),
                bottom = dimensionResource(R.dimen.padding_medium) + outerPadding.calculateBottomPadding()
            ),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.card_spacing)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.card_spacing)),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(categoryItems) { item ->
                CategoryCard(item = item, onClick = { onCategoryClick(item.category) })
            }
        }
    }
}

@Composable
fun CategoryCard(item: CategoryItem, onClick: () -> Unit = {}) {
    ElevatedCard(
        onClick = onClick,
        shape = RoundedCornerShape(dimensionResource(R.dimen.card_corner_radius)),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimensionResource(R.dimen.padding_medium)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(dimensionResource(R.dimen.card_spacing)),
                color = item.color.copy(alpha = 0.15f),
                modifier = Modifier.size(dimensionResource(R.dimen.category_icon_container))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = stringResource(item.category.labelRes),
                        tint = item.color,
                        modifier = Modifier.size(dimensionResource(R.dimen.category_icon_size))
                    )
                }
            }
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.card_spacing)))
            Text(
                text = stringResource(item.category.labelRes),
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = stringResource(R.string.category_tap_to_explore),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Preview(showBackground = true, name = "CategoriesScreen")
@Composable
private fun CategoriesScreenPreview() {
    JokesAppTheme {
        CategoriesScreen()
    }
}

@Preview(showBackground = true, name = "CategoryCard")
@Composable
private fun CategoryCardPreview() {
    JokesAppTheme {
        CategoryCard(item = categoryItems.first())
    }
}
