package com.alex.guesstheanimal.di

import com.alex.guesstheanimal.data.resource.Resource
import com.alex.guesstheanimal.data.resource.ResourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ResourceModule {

    @Binds
    abstract fun  bindResourceImpl(resourceImpl: ResourceImpl): Resource
}
