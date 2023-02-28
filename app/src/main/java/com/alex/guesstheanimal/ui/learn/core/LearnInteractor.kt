package com.alex.guesstheanimal.ui.learn.core

import com.alex.guesstheanimal.database.Animal
import kotlinx.coroutines.flow.Flow

interface LearnInteractor {

    suspend fun getData(category: String): Flow<MutableList<Animal>>

    suspend fun getAnimalDetails(animalId: Int): Animal
}