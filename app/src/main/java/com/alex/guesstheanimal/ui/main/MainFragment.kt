package com.alex.guesstheanimal.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.alex.guesstheanimal.R
import com.alex.guesstheanimal.databinding.FragmentMainBinding
import com.alex.guesstheanimal.utils.Const.CATEGORY_AFRICA
import com.alex.guesstheanimal.utils.Const.CATEGORY_COLORS
import com.alex.guesstheanimal.utils.Const.CATEGORY_DIGITS
import com.alex.guesstheanimal.utils.Const.CATEGORY_DOMESTIC
import com.alex.guesstheanimal.utils.Const.CATEGORY_FIGURES
import com.alex.guesstheanimal.utils.Const.CATEGORY_FOREST
import com.alex.guesstheanimal.utils.Const.CATEGORY_FRUITS
import com.alex.guesstheanimal.utils.Const.CATEGORY_HOME
import com.alex.guesstheanimal.utils.Const.CATEGORY_LETTERS_EN
import com.alex.guesstheanimal.utils.Const.CATEGORY_LETTERS_RU
import com.alex.guesstheanimal.utils.Const.LOCALE_EN
import com.alex.guesstheanimal.utils.Const.LOCALE_EN_UK
import com.alex.guesstheanimal.utils.Const.LOCALE_EN_US
import com.alex.guesstheanimal.utils.Const.LOCALE_RU
import dagger.hilt.android.AndroidEntryPoint
import java.util.Locale

@AndroidEntryPoint
class MainFragment : Fragment(R.layout.fragment_main) {
    private val viewBinding: FragmentMainBinding by viewBinding()
    private val viewModel: MainViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        viewBinding.apply {
            imDomestic.setOnClickListener { viewModel.onChooseCategory(CATEGORY_DOMESTIC) }
            imForest.setOnClickListener { viewModel.onChooseCategory(CATEGORY_FOREST) }
            imAfrica.setOnClickListener { viewModel.onChooseCategory(CATEGORY_AFRICA) }
            imColors.setOnClickListener { viewModel.onChooseCategory(CATEGORY_COLORS) }
            imDigits.setOnClickListener { viewModel.onChooseCategory(CATEGORY_DIGITS) }
            imFurniture.setOnClickListener { viewModel.onChooseCategory(CATEGORY_HOME) }
            imFruits.setOnClickListener { viewModel.onChooseCategory(CATEGORY_FRUITS) }
            imFigures.setOnClickListener { viewModel.onChooseCategory(CATEGORY_FIGURES) }
            imLetters.setOnClickListener { checkLocaleForLetters() }
            imLanguageEN.setOnClickListener { setAppLocale(LOCALE_EN) }
            imLanguageRU.setOnClickListener { setAppLocale(LOCALE_RU) }
        }
    }

    private fun setAppLocale(language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        config.setLayoutDirection(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
        viewModel.updateScreen()
        requireContext().createConfigurationContext(config)
    }

    private fun checkLocaleForLetters() {
        when (resources.configuration.locales[0].toString()) {
            LOCALE_EN, LOCALE_EN_UK, LOCALE_EN_US -> viewModel.onChooseCategory(CATEGORY_LETTERS_EN)
            else -> viewModel.onChooseCategory(CATEGORY_LETTERS_RU)
        }
    }
}