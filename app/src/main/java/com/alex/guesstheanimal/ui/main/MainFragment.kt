package com.alex.guesstheanimal.ui.main

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.alex.guesstheanimal.R
import com.alex.guesstheanimal.databinding.FragmentMainBinding
import com.alex.guesstheanimal.utils.Const.CATEGORY_AFRICA
import com.alex.guesstheanimal.utils.Const.CATEGORY_DOMESTIC
import com.alex.guesstheanimal.utils.Const.CATEGORY_FOREST
import com.alex.guesstheanimal.utils.showSnackbar
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
            imDino.setOnClickListener {
//                viewModel.onChooseCategory(CATEGORY_DINO)}
                showSnackbar("В разработке")
            }
            imColors.setOnClickListener {
//                viewModel.onChooseCategory(CATEGORY_COLOR)
                showSnackbar("В разработке")
            }
            imDigits.setOnClickListener {
//                viewModel.onChooseCategory(CATEGORY_DIGIT)
                showSnackbar("В разработке")
            }
            imLetters.setOnClickListener {
//                viewModel.onChooseCategory(CATEGORY_LETTER)
                showSnackbar("В разработке")
            }
            imFurniture.setOnClickListener {
//                viewModel.onChooseCategory(CATEGORY_FURNITURE)
                showSnackbar("В разработке")
            }
            imLanguageEN.setOnClickListener {
                setAppLocale("en")
            }
            imLanguageRU.setOnClickListener {
                setAppLocale("ru")
            }
        }
    }

    // TODO: check memory leak
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
}