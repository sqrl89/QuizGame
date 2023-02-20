package com.alex.guesstheanimal.ui

import com.alex.guesstheanimal.ui.learn.LearnFragment
import com.alex.guesstheanimal.ui.main.MainFragment
import com.alex.guesstheanimal.ui.quiz.QuizFragment
import com.alex.guesstheanimal.ui.quizresult.QuizResultFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen

object Screens {

    fun Main() = FragmentScreen {
        MainFragment()
    }

    fun Learn(category: String) = FragmentScreen {
        LearnFragment.newInstance(category)
    }

    fun Quiz(category: String) = FragmentScreen {
        QuizFragment.newInstance(category)
    }

    fun Result(result: Int) = FragmentScreen {
        QuizResultFragment.newInstance(result)
    }


}