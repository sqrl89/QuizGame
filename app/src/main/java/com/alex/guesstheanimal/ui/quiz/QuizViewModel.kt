package com.alex.guesstheanimal.ui.quiz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alex.guesstheanimal.database.Animal
import com.alex.guesstheanimal.ui.Screens
import com.alex.guesstheanimal.ui.quiz.core.QuizInteractor
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
class QuizViewModel @Inject constructor(
    private val router: Router,
    private val interactor: QuizInteractor,
) : ViewModel() {

    // TODO: statedataclass ???
    private val _category = MutableStateFlow("")

    private val _animalList = MutableStateFlow<MutableList<Animal>?>(null)
    val animalList = _animalList.asStateFlow()

    private val _rightAnswer = MutableStateFlow<Animal?>(null)
    var rightAnswer = _rightAnswer.asStateFlow()

    private val _failRound = MutableStateFlow(false)
    var failRound = _failRound.asStateFlow()

    private val _isDoubleBackPressed = MutableStateFlow(false)
    var isDoubleBackPressed = _isDoubleBackPressed.asStateFlow()

    private val _rightAnswersCount = MutableStateFlow(0)
    var rightAnswersCount = _rightAnswersCount.asStateFlow()

    private val _quizCount = MutableStateFlow(1)
    var quizCount = _quizCount.asStateFlow()

    private val _locale = MutableStateFlow("ru")
    var locale = _locale.asStateFlow()

    fun setCategory(category: String) {
        _category.value = category
    }

    fun setRightAnswer(answer: Animal) {
        _rightAnswer.value = answer
    }

    fun isFailRound(isFail: Boolean) {
        _failRound.value = isFail
    }

    fun doubleBackPressed(){
        viewModelScope.launch {
            _isDoubleBackPressed.value = true
            delay(3000)
            _isDoubleBackPressed.value = false
        }
    }

    fun increaseRightAnswersCount(){
        _rightAnswersCount.value++
    }

    fun increaseQuizCount(){
        _quizCount.value++
    }

    fun onChosenCategory() =
        _category.flatMapLatest { it ->
            interactor.getData(it)
        }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(2000))

    fun setAnimalList(animalList: MutableList<Animal>){
        _animalList.value = animalList
    }

    fun setLocale(locale: String) {
        _locale.value = locale
    }

    fun onResult(result: Int) {
        router.replaceScreen(Screens.Result(result))
    }

    fun onBackPressed() {
        router.exit()
    }

}