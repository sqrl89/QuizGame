package com.alex.guesstheanimal.ui.learn.core

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class LearnScreenModule {
    @Binds
    abstract fun bindInteractor(interactorImpl: LearnInteractorImpl): LearnInteractor
}