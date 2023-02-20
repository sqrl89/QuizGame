package com.alex.guesstheanimal.ui.quizresult

import androidx.lifecycle.ViewModel
import com.alex.guesstheanimal.R
import com.alex.guesstheanimal.data.resource.Resource
import com.alex.guesstheanimal.ui.Screens
import com.alex.guesstheanimal.utils.Const.ONE_STAR_RESULT
import com.alex.guesstheanimal.utils.Const.THREE_STARS_RESULT
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class QuizResultViewModel @Inject constructor(
    private val router: Router,
    private val resource: Resource
) : ViewModel() {

    private val _result = MutableStateFlow<Int?>(null)
    val result: StateFlow<Int?> = _result.asStateFlow()

    private val _congrats = MutableStateFlow("")
    val congrats: StateFlow<String?> = _congrats.asStateFlow()

    fun setResult(result: Int) {
        _result.value = result
        when(result){
            0 -> _congrats.value = resource.getString(R.string.congrats_0)
            in 1..ONE_STAR_RESULT -> _congrats.value = resource.getString(R.string.congrats_1)
            THREE_STARS_RESULT -> _congrats.value = resource.getString(R.string.congrats_3)
            else ->  _congrats.value = resource.getString(R.string.congrats_2)
        }
    }

    fun onBackPressed() {
        router.backTo(Screens.Main())
    }
}