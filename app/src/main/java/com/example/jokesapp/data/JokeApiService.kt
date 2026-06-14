package com.example.jokesapp.data

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JokeApiService {
    @GET("joke/{category}?safe-mode")
    suspend fun getJoke(
        @Path("category") category: String,
        @Query("lang") lang: String? = null,
    ): JokeApiResponse
}
