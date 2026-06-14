package com.example.jokesapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteJokeDao {

    @Query("SELECT * FROM favorite_jokes")
    fun getAllFavorites(): Flow<List<FavoriteJokeEntity>>

    @Query("SELECT id FROM favorite_jokes")
    fun getAllFavoriteIds(): Flow<List<Int>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(joke: FavoriteJokeEntity)

    @Query("DELETE FROM favorite_jokes WHERE id = :jokeId")
    suspend fun deleteFavoriteById(jokeId: Int)
}
