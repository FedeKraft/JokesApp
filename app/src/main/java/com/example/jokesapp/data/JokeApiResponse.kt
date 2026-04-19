package com.example.jokesapp.data

import com.example.jokesapp.model.Joke
import com.example.jokesapp.model.JokeCategory

data class JokeApiResponse(
    val error: Boolean,
    val category: String,
    val type: String,
    val joke: String?,
    val setup: String?,
    val delivery: String?,
    val id: Int
) {
    fun toJoke(): Joke {
        if (error) throw Exception("API returned an error")

        val cat = when (category) {
            "Programming" -> JokeCategory.PROGRAMMING
            "Pun"         -> JokeCategory.PUN
            "Spooky"      -> JokeCategory.SPOOKY
            "Christmas"   -> JokeCategory.CHRISTMAS
            else          -> JokeCategory.MISC
        }

        return if (type == "single") {
            Joke.Single(id = id, category = cat, joke = joke ?: "")
        } else {
            Joke.TwoPart(id = id, category = cat, setup = setup ?: "", delivery = delivery ?: "")
        }
    }
}
