package com.example.jokesapp.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.jokesapp.model.JokeCategory
import com.example.jokesapp.model.mockJokes
import com.example.jokesapp.ui.components.JokeCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(outerPadding: PaddingValues = PaddingValues()) {
    var selectedCategory by remember { mutableStateOf<JokeCategory?>(null) }
    var currentJoke by remember { mutableStateOf(mockJokes.random()) }

    val categories = JokeCategory.entries.toList()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("JokesApp") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(bottom = outerPadding.calculateBottomPadding())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(4.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedCategory == null,
                        onClick = { selectedCategory = null },
                        label = { Text("All") }
                    )
                }
                items(categories) { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category },
                        label = { Text(category.displayName) }
                    )
                }
            }

            JokeCard(joke = currentJoke)

            Button(
                onClick = {
                    val filtered = if (selectedCategory == null) mockJokes
                    else mockJokes.filter { it.category == selectedCategory }
                    currentJoke = (filtered.ifEmpty { mockJokes }).random()
                },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text("Get Joke")
            }
        }
    }
}
