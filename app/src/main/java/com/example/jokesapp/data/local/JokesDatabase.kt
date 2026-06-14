package com.example.jokesapp.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FavoriteJokeEntity::class], version = 1, exportSchema = false)
abstract class JokesDatabase : RoomDatabase() {

    abstract fun favoriteJokeDao(): FavoriteJokeDao

    companion object {
        @Volatile
        private var INSTANCE: JokesDatabase? = null

        fun getDatabase(context: Context): JokesDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    JokesDatabase::class.java,
                    "jokes_database"
                ).build().also { INSTANCE = it }
            }
    }
}
