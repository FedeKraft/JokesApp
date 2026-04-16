package com.example.jokesapp.data

import com.example.jokesapp.model.Joke
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class JokeRepository {

    private val service: JokeApiService = Retrofit.Builder()
        .baseUrl("https://v2.jokeapi.dev/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(JokeApiService::class.java)

    suspend fun fetchJoke(category: String = "Any"): Joke =
        service.getJoke(category).toJoke()
}
