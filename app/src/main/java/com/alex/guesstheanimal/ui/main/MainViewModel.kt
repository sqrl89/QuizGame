package com.alex.guesstheanimal.ui.main

import androidx.lifecycle.ViewModel
import com.alex.guesstheanimal.ui.Screens
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val router: Router
) : ViewModel() {

    fun onChooseCategory(category: String) {
        router.navigateTo(Screens.Learn(category))
    }

    fun updateScreen(){
        router.replaceScreen(Screens.Main())
    }
}