package com.alex.guesstheanimal.repository

import com.alex.guesstheanimal.database.Animal
import com.alex.guesstheanimal.database.Dao
import com.alex.guesstheanimal.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
    private val dao: Dao
) : Repository {

    override suspend fun getData(): Flow<List<Animal>> {
        return dao.getAnimals()
    }

    override suspend fun getAnimalDetails(animalId: Int): Animal {
        return withContext(ioDispatcher) {
            dao.getAnimalDetails(animalId)
        }
    }
}