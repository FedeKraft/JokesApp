package com.example.jokesapp.model

import androidx.annotation.StringRes
import com.example.jokesapp.R

enum class JokeCategory(@StringRes val labelRes: Int) {
    PROGRAMMING(R.string.category_programming),
    MISC(R.string.category_misc),
    PUN(R.string.category_pun),
    SPOOKY(R.string.category_spooky),
    CHRISTMAS(R.string.category_christmas)
}

sealed class Joke {
    abstract val id: Int
    abstract val category: JokeCategory

    data class Single(
        override val id: Int,
        override val category: JokeCategory,
        val joke: String
    ) : Joke()

    data class TwoPart(
        override val id: Int,
        override val category: JokeCategory,
        val setup: String,
        val delivery: String
    ) : Joke()
}

val mockJokes = listOf(
    Joke.TwoPart(
        id = 1,
        category = JokeCategory.PROGRAMMING,
        setup = "Why do programmers prefer dark mode?",
        delivery = "Because light attracts bugs!"
    ),
    Joke.Single(
        id = 2,
        category = JokeCategory.PROGRAMMING,
        joke = "A SQL query walks into a bar, walks up to two tables and asks... 'Can I join you?'"
    ),
    Joke.TwoPart(
        id = 3,
        category = JokeCategory.PUN,
        setup = "What do you call a fish without eyes?",
        delivery = "A fsh!"
    ),
    Joke.Single(
        id = 4,
        category = JokeCategory.MISC,
        joke = "I told my wife she should embrace her mistakes. She gave me a hug."
    ),
    Joke.Single(
        id = 6,
        category = JokeCategory.SPOOKY,
        joke = "I would tell you a joke about ghosts, but you probably wouldn't get it. You never did have any spirit."
    ),
    Joke.TwoPart(
        id = 7,
        category = JokeCategory.CHRISTMAS,
        setup = "What do you call an elf that sings?",
        delivery = "A wrapper!"
    ),
    Joke.Single(
        id = 8,
        category = JokeCategory.PUN,
        joke = "I used to hate facial hair, but then it grew on me."
    )
)
