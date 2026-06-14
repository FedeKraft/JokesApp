package com.example.jokesapp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.jokesapp.model.Joke
import com.example.jokesapp.model.JokeCategory

@Entity(tableName = "favorite_jokes")
data class FavoriteJokeEntity(
    @PrimaryKey val id: Int,
    val category: String,
    val type: String,
    val joke: String?,
    val setup: String?,
    val delivery: String?
) {
    fun toJoke(): Joke {
        val cat = JokeCategory.valueOf(category)
        return if (type == "single") {
            Joke.Single(id = id, category = cat, joke = joke ?: "")
        } else {
            Joke.TwoPart(id = id, category = cat, setup = setup ?: "", delivery = delivery ?: "")
        }
    }
}

fun Joke.toEntity(): FavoriteJokeEntity = when (this) {
    is Joke.Single -> FavoriteJokeEntity(id, category.name, "single", joke, null, null)
    is Joke.TwoPart -> FavoriteJokeEntity(id, category.name, "two_part", null, setup, delivery)
}
