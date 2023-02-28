package com.alex.guesstheanimal.ui.quiz.core

import com.alex.guesstheanimal.database.Animal
import kotlinx.coroutines.flow.Flow

interface QuizInteractor {

    suspend fun getData(category: String): Flow<MutableList<Animal>>

}