package com.example.jokesapp.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.jokesapp.R
import com.example.jokesapp.data.JokeRepository
import com.example.jokesapp.data.local.JokesDatabase
import com.example.jokesapp.data.local.toEntity
import com.example.jokesapp.model.Joke
import com.example.jokesapp.model.JokeCategory
import com.example.jokesapp.model.JokeRarity
import com.example.jokesapp.model.mockJokes
import com.example.jokesapp.model.rollRarity
import com.example.jokesapp.model.rollRarityBoosted
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class JokeUiState {
    object Loading : JokeUiState()
    data class Success(val joke: Joke, val rarity: JokeRarity = JokeRarity.COMMON) : JokeUiState()
    data class Error(val message: String) : JokeUiState()
}

class JokeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = JokeRepository()
    private val dao = JokesDatabase.getDatabase(application).favoriteJokeDao()
    private val prefs = application.getSharedPreferences("jokes_prefs", Context.MODE_PRIVATE)

    private val _jokesRead = MutableStateFlow(prefs.getInt("jokes_read", 0))
    val jokesRead: StateFlow<Int> = _jokesRead.asStateFlow()

    private val _coins = MutableStateFlow(prefs.getInt("coins", 0))
    val coins: StateFlow<Int> = _coins.asStateFlow()

    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak.asStateFlow()

    // id -> expiry timestamp in ms (temporary upgrades)
    private val _activeUpgrades = MutableStateFlow<Map<String, Long>>(emptyMap())
    val activeUpgrades: StateFlow<Map<String, Long>> = _activeUpgrades.asStateFlow()

    private val _uiState = MutableStateFlow<JokeUiState>(JokeUiState.Loading)
    val uiState: StateFlow<JokeUiState> = _uiState.asStateFlow()

    private val _selectedCategory = MutableStateFlow<JokeCategory?>(null)
    val selectedCategory: StateFlow<JokeCategory?> = _selectedCategory.asStateFlow()

    private val _allJokes = MutableStateFlow(mockJokes)

    val favorites: StateFlow<Set<Int>> = dao.getAllFavoriteIds()
        .map { it.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptySet())

    val favoriteJokes: StateFlow<List<Joke>> = dao.getAllFavorites()
        .map { entities -> entities.map { it.toJoke() } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _spanishFallbackEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val spanishFallbackEvent: SharedFlow<Unit> = _spanishFallbackEvent.asSharedFlow()

    private val _milestoneEvent = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val milestoneEvent: SharedFlow<String> = _milestoneEvent.asSharedFlow()

    private val _floatTextEvent = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val floatTextEvent: SharedFlow<String> = _floatTextEvent.asSharedFlow()

    private val _cursedEscapeEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val cursedEscapeEvent: SharedFlow<Unit> = _cursedEscapeEvent.asSharedFlow()

    private val _memeEvent = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val memeEvent: SharedFlow<String> = _memeEvent.asSharedFlow()

    private val memeFiles: Array<String> by lazy {
        application.assets.list("memes")?.takeIf { it.isNotEmpty() } ?: emptyArray()
    }

    private val milestones = mapOf(
        1    to "First joke consumed. No going back. 🤡",
        5    to "5 jokes. Your friends would not understand.",
        10   to "10 jokes. Go touch grass 🌿",
        25   to "25 jokes. Seek help.",
        50   to "50 jokes. Have you considered therapy?",
        69   to "69 jokes. Nice. 😏",
        100  to "100 JOKES. You are the chosen one. 💀",
        200  to "200 jokes. Are you okay?",
        420  to "420 jokes. 🍃",
        1000 to "1000 JOKES. This cannot be healthy.",
    )

    init {
        fetchJoke()
        viewModelScope.launch {
            delay(400)
            checkStreak()
        }
    }

    private fun isActive(id: String) =
        System.currentTimeMillis() < (_activeUpgrades.value[id] ?: 0L)

    private fun checkStreak() {
        val todayEpoch = System.currentTimeMillis() / 86_400_000L
        val lastEpoch  = prefs.getLong("last_open_epoch", -1L)
        val saved      = prefs.getInt("streak", 0)

        val newStreak = when {
            lastEpoch == -1L            -> 1
            lastEpoch == todayEpoch     -> saved
            lastEpoch == todayEpoch - 1 -> saved + 1
            else                        -> 1
        }

        if (lastEpoch != todayEpoch) {
            when {
                saved > 1 && newStreak == 1 ->
                    _milestoneEvent.tryEmit("💀 Streak perdido. De $saved a 0. Decepcionante.")
                newStreak > 1 ->
                    _milestoneEvent.tryEmit("🔥 Day $newStreak streak! Don't stop now.")
            }
            prefs.edit()
                .putLong("last_open_epoch", todayEpoch)
                .putInt("streak", newStreak)
                .apply()
        }

        _streak.value = newStreak
    }

    fun buyUpgrade(id: String, cost: Int) {
        if (_coins.value < cost) return
        _coins.value -= cost
        prefs.edit().putInt("coins", _coins.value).apply()
        // Reset expiry to now + 10 seconds (stacks/resets if already active)
        _activeUpgrades.value = _activeUpgrades.value + (id to System.currentTimeMillis() + 10_000L)
    }

    fun fetchJoke(category: JokeCategory? = _selectedCategory.value) {
        viewModelScope.launch {
            _uiState.value = JokeUiState.Loading
            try {
                val apiCategory = when (category) {
                    JokeCategory.PROGRAMMING -> "Programming"
                    JokeCategory.PUN         -> "Pun"
                    JokeCategory.SPOOKY      -> "Spooky"
                    JokeCategory.CHRISTMAS   -> "Christmas"
                    JokeCategory.MISC        -> "Misc"
                    null                     -> "Any"
                }
                val locale = getApplication<Application>().resources.configuration.locales[0].language
                val lang = if (locale == "es") "es" else null
                val joke = try {
                    repository.fetchJoke(apiCategory, lang)
                } catch (e: Exception) {
                    if (lang != null) {
                        _spanishFallbackEvent.tryEmit(Unit)
                        repository.fetchJoke(apiCategory, null)
                    } else throw e
                }

                if (_allJokes.value.none { it.id == joke.id }) {
                    _allJokes.value = _allJokes.value + joke
                }

                val rarity     = if (isActive("lucky_charm")) rollRarityBoosted() else rollRarity()
                val baseCoins  = (rarity.minCoins..rarity.maxCoins).random()
                val multiplier = when {
                    isActive("coin_tripler") -> 3
                    isActive("coin_doubler") -> 2
                    else                     -> 1
                }
                val earned = baseCoins * multiplier

                _uiState.value = JokeUiState.Success(joke, rarity)
                _jokesRead.value++
                prefs.edit().putInt("jokes_read", _jokesRead.value).apply()
                milestones[_jokesRead.value]?.let { _milestoneEvent.tryEmit(it) }

                _coins.value += earned
                prefs.edit().putInt("coins", _coins.value).apply()

                _floatTextEvent.tryEmit(
                    when (rarity) {
                        JokeRarity.LEGENDARY -> "+$earned 🌟"
                        JokeRarity.EPIC      -> "+$earned 🟪"
                        JokeRarity.RARE      -> "+$earned 🟦"
                        JokeRarity.COMMON    -> "+$earned 🪙"
                    }
                )

                if (rarity != JokeRarity.COMMON) {
                    _milestoneEvent.tryEmit(
                        when (rarity) {
                            JokeRarity.RARE      -> "🟦 Rare joke! +$earned 🪙"
                            JokeRarity.EPIC      -> "🟪 EPIC!! +$earned 🪙"
                            JokeRarity.LEGENDARY -> "🌟 LEGENDARY!!! +$earned 🪙 🎊"
                            else                 -> ""
                        }
                    )
                }

                val escapeChance = if (isActive("chaos_mode")) 60 else 15
                if ((1..100).random() <= escapeChance) _cursedEscapeEvent.tryEmit(Unit)

                if (memeFiles.isNotEmpty() && (1..100).random() <= 15) {
                    _memeEvent.tryEmit(memeFiles.random())
                }

            } catch (e: Exception) {
                _uiState.value = JokeUiState.Error(
                    getApplication<Application>().getString(R.string.error_no_connection)
                )
            }
        }
    }

    fun selectCategory(category: JokeCategory?) {
        _selectedCategory.value = category
        fetchJoke(category)
    }

    fun toggleFavorite(jokeId: Int) {
        viewModelScope.launch {
            if (jokeId in favorites.value) {
                dao.deleteFavoriteById(jokeId)
            } else {
                val joke = _allJokes.value.find { it.id == jokeId }
                joke?.let { dao.insertFavorite(it.toEntity()) }
            }
        }
    }
}
