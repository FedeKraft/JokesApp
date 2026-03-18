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
import androidx.compose.material.icons.filled.DarkMode
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
import androidx.compose.ui.unit.dp
import com.example.jokesapp.model.JokeCategory
import com.example.jokesapp.model.mockJokes
import com.example.jokesapp.ui.theme.ChristmasColor
import com.example.jokesapp.ui.theme.DarkColor
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
    CategoryItem(JokeCategory.DARK, Icons.Filled.DarkMode, DarkColor),
    CategoryItem(JokeCategory.PUN, Icons.Filled.EmojiEmotions, PunColor),
    CategoryItem(JokeCategory.SPOOKY, Icons.Filled.AutoAwesome, SpookyColor),
    CategoryItem(JokeCategory.CHRISTMAS, Icons.Filled.CardGiftcard, ChristmasColor)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriesScreen(outerPadding: PaddingValues = PaddingValues()) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Categories") })
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 16.dp + outerPadding.calculateBottomPadding()
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            items(categoryItems) { item ->
                CategoryCard(item = item)
            }
        }
    }
}

@Composable
fun CategoryCard(item: CategoryItem) {
    val jokeCount = mockJokes.count { it.category == item.category }

    ElevatedCard(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = item.color.copy(alpha = 0.15f),
                modifier = Modifier.size(64.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.category.displayName,
                        tint = item.color,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = item.category.displayName,
                style = MaterialTheme.typography.titleMedium
            )
            Text(
                text = "$jokeCount jokes",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
