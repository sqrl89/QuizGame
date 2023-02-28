package com.alex.guesstheanimal.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Animal::class], version = 1, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun getDao(): Dao
}