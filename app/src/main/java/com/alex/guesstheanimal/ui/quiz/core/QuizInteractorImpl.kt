package com.alex.guesstheanimal.ui.quiz.core

import com.alex.guesstheanimal.repository.Repository
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class QuizInteractorImpl @Inject constructor(
    private val repository: Repository
) : QuizInteractor {

    override suspend fun getData(category: String) =
        repository.getData().map { list ->
            list.filter { it.category == category }.toMutableList()
    }

}