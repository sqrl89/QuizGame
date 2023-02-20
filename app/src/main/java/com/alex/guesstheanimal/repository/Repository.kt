package com.alex.guesstheanimal.repository

import com.alex.guesstheanimal.data.database.Animal
import kotlinx.coroutines.flow.Flow

interface Repository {

    suspend fun getData(): Flow<List<Animal>>

    suspend fun getAnimalDetails(animalId: Int): Animal

}