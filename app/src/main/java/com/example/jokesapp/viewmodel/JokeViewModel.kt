package com.example.jokesapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jokesapp.R
import com.example.jokesapp.data.JokeRepository
import com.example.jokesapp.model.Joke
import com.example.jokesapp.model.JokeCategory
import com.example.jokesapp.model.mockJokes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class JokeUiState {
    object Loading : JokeUiState()
    data class Success(val joke: Joke) : JokeUiState()
    data class Error(val message: String) : JokeUiState()
}

class JokeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = JokeRepository()

    private val _uiState = MutableStateFlow<JokeUiState>(JokeUiState.Loading)
    val uiState: StateFlow<JokeUiState> = _uiState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<JokeCategory?>(null)
    val selectedCategory: StateFlow<JokeCategory?> = _selectedCategory.asStateFlow()

    private val _favorites = MutableStateFlow<Set<Int>>(emptySet())
    val favorites: StateFlow<Set<Int>> = _favorites.asStateFlow()

    private val _allJokes = MutableStateFlow(mockJokes)

    val favoriteJokes: StateFlow<List<Joke>> = combine(_allJokes, _favorites) { jokes, favIds ->
        jokes.filter { it.id in favIds }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        fetchJoke()
    }

    fun fetchJoke(category: JokeCategory? = _selectedCategory.value) {
        viewModelScope.launch {
            _uiState.value = JokeUiState.Loading
            try {
                val apiCategory = when (category) {
                    JokeCategory.PROGRAMMING -> "Programming"
                    JokeCategory.DARK        -> "Dark"
                    JokeCategory.PUN         -> "Pun"
                    JokeCategory.SPOOKY      -> "Spooky"
                    JokeCategory.CHRISTMAS   -> "Christmas"
                    JokeCategory.MISC        -> "Misc"
                    null                     -> "Any"
                }
                val joke = repository.fetchJoke(apiCategory)
                if (_allJokes.value.none { it.id == joke.id }) {
                    _allJokes.value = _allJokes.value + joke
                }
                _uiState.value = JokeUiState.Success(joke)
            } catch (e: Exception) {
                _uiState.value = JokeUiState.Error(getApplication<Application>().getString(R.string.error_no_connection))
            }
        }
    }

    fun selectCategory(category: JokeCategory?) {
        _selectedCategory.value = category
        fetchJoke(category)
    }

    fun toggleFavorite(jokeId: Int) {
        val current = _favorites.value
        _favorites.value = if (jokeId in current) current - jokeId else current + jokeId
    }
}
