package com.alex.guesstheanimal.ui.learn.core

import com.alex.guesstheanimal.database.Animal
import com.alex.guesstheanimal.repository.Repository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LearnInteractorImpl @Inject constructor(
    private val repository: Repository
) : LearnInteractor {

    override suspend fun getData(category: String) =
        repository.getData().map { list ->
            list.filter { it.category == category }.toMutableList()
        }

    override suspend fun getAnimalDetails(animalId: Int): Animal {
        return repository.getAnimalDetails(animalId)
    }
}