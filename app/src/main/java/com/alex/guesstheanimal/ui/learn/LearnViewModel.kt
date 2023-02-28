package com.alex.guesstheanimal.ui.learn

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alex.guesstheanimal.database.Animal
import com.alex.guesstheanimal.ui.Screens
import com.alex.guesstheanimal.ui.learn.core.LearnInteractor
import com.alex.guesstheanimal.utils.Const.CATEGORY_DIGITS
import com.alex.guesstheanimal.utils.Const.CATEGORY_LETTERS_EN
import com.alex.guesstheanimal.utils.Const.CATEGORY_LETTERS_RU
import com.alex.guesstheanimal.utils.Const.LEARN_COUNT
import com.alex.guesstheanimal.utils.Const.LEARN_DIGITS_COUNT
import com.alex.guesstheanimal.utils.Const.LEARN_LETTERS_EN_COUNT
import com.alex.guesstheanimal.utils.Const.LEARN_LETTERS_RU_COUNT
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LearnViewModel @Inject constructor(
    private val router: Router,
    private val interactor: LearnInteractor,
) : ViewModel() {

    private val _category = MutableStateFlow("")
    val category = _category.asStateFlow()

    private val _animalList = MutableStateFlow<MutableList<Animal>?>(null)
    val animalList = _animalList.asStateFlow()

    private val _isDoubleBackPressed = MutableStateFlow(false)
    var isDoubleBackPressed = _isDoubleBackPressed.asStateFlow()

    private val _learnCount = MutableStateFlow(0)
    var learnCount = _learnCount.asStateFlow()

    private val _constLearnCount = MutableStateFlow(0)
    var constLearnCount = _constLearnCount.asStateFlow()

    private val _locale = MutableStateFlow("ru")
    var locale = _locale.asStateFlow()

    // TODO: check
    fun setCategory(category: String) {
        _category.value = category
        when (category) {
            CATEGORY_DIGITS -> _constLearnCount.value = LEARN_DIGITS_COUNT //20: 0-20
            CATEGORY_LETTERS_EN -> _constLearnCount.value = LEARN_LETTERS_EN_COUNT //25: A-Z
            CATEGORY_LETTERS_RU -> _constLearnCount.value = LEARN_LETTERS_RU_COUNT // 32: А-Я
            else -> _constLearnCount.value = LEARN_COUNT // 19: 20 шт.
        }
    }

    fun onChosenCategory() =
        _category.flatMapLatest { it ->
            interactor.getData(it)
        }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(2000))

    fun setAnimalList(animalList: MutableList<Animal>) {
        _animalList.value = animalList
    }

    fun setLocale(locale: String) {
        _locale.value = locale
    }

    fun doubleBackPressed() {
        viewModelScope.launch {
            _isDoubleBackPressed.value = true
            delay(3000)
            _isDoubleBackPressed.value = false
        }
    }

    fun onStartQuiz(category: String) {
        router.replaceScreen(Screens.Quiz(category))
    }

    fun increaseLearnCount() {
        _learnCount.value++
    }

    fun decreaseLearnCount() {
        _learnCount.value--
    }

    fun onBackPressed() {
        router.exit()
    }
}