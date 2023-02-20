package com.alex.guesstheanimal.data.resource

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResourceImpl @Inject constructor(@ApplicationContext val context: Context) : Resource {
    override fun getString(id: Int): String {
        return context.resources.getString(id)
    }
}