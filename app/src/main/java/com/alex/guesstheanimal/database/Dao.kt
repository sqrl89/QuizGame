package com.alex.guesstheanimal.database

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {

    @Query("SELECT * FROM animals")
    fun getAnimals(): Flow<List<Animal>>

    @Query("SELECT * FROM animals WHERE id = :animalId")
    suspend fun getAnimalDetails(animalId: Int): Animal

}