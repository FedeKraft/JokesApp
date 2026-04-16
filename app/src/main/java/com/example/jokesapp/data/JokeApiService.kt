package com.example.jokesapp.data

import retrofit2.http.GET
import retrofit2.http.Path

interface JokeApiService {
    @GET("joke/{category}?safe-mode")
    suspend fun getJoke(@Path("category") category: String): JokeApiResponse
}
