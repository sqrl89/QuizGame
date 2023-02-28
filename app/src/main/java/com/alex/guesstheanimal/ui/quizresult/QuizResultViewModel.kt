package com.alex.guesstheanimal.ui.quizresult

import androidx.lifecycle.ViewModel
import com.alex.guesstheanimal.ui.Screens
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class QuizResultViewModel @Inject constructor(
    private val router: Router,
) : ViewModel() {

    private val _result = MutableStateFlow<Int?>(null)
    val result: StateFlow<Int?> = _result.asStateFlow()

    fun setResult(result: Int) {
        _result.value = result
    }

    fun onBackPressed() {
        router.backTo(Screens.Main())
    }
}