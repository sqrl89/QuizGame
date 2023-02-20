package com.alex.guesstheanimal.ui.quiz.core

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class QuizScreenModule {
    @Binds
    abstract fun bindInteractor(interactorImpl: QuizInteractorImpl): QuizInteractor
}