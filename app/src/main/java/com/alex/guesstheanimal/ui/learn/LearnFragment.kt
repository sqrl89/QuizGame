package com.alex.guesstheanimal.ui.learn

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.LANG_MISSING_DATA
import android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED
import android.speech.tts.TextToSpeech.OnInitListener
import android.speech.tts.TextToSpeech.SUCCESS
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.alex.guesstheanimal.R
import com.alex.guesstheanimal.data.database.Animal
import com.alex.guesstheanimal.databinding.FragmentLearnBinding
import com.alex.guesstheanimal.utils.Const.LOCALE_EN
import com.alex.guesstheanimal.utils.Const.LOCALE_RU
import com.alex.guesstheanimal.utils.appearScaleAnimation
import com.alex.guesstheanimal.utils.disappearScaleAnimation
import com.alex.guesstheanimal.utils.showOrGone
import com.alex.guesstheanimal.utils.showSnackbar
import com.alex.guesstheanimal.utils.showToast
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class LearnFragment : Fragment(R.layout.fragment_learn), OnInitListener {
    private val viewModel: LearnViewModel by viewModels()
    private val viewBinding: FragmentLearnBinding by viewBinding()
    private var textToSpeech: TextToSpeech? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString(LEARN_KEY).also {
            if (it != null) viewModel.setCategory(it)
        }
        textToSpeech = TextToSpeech(requireContext(), null)
        initListeners()
        onBackPressed()
        setLocale()
        setFirstAnimal()
    }

    // TODO: check if default EN
    private fun setLocale() {
        when (resources.configuration.locales[0].toString()) {
            LOCALE_RU -> viewModel.setLocale("ru")
            LOCALE_EN -> viewModel.setLocale("en")
        }
    }

    private fun setFirstAnimal() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.onChosenCategory().collectLatest { list ->
                    list.shuffle()
                    viewModel.setAnimalList(list)
                    Glide.with(requireContext()).load(list[0].imageUri?.toUri())
                        .into(viewBinding.imMain)
                    when (viewModel.locale.value) {
                        LOCALE_RU -> speakOut(list[0].nameRu)
                        LOCALE_EN -> speakOut(list[0].nameEn)
                    }
                }
            }
        }
    }

    private fun onNextAnimal(list: MutableList<Animal>) {
        viewModel.increaseLearnCount()
        val position = viewModel.learnCount.value
        Glide.with(requireContext()).load(list[position].imageUri?.toUri())
            .into(viewBinding.imMain)
        when (viewModel.locale.value) {
            LOCALE_RU -> speakOut(list[position].nameRu)
            LOCALE_EN -> speakOut(list[position].nameEn)
        }
        checkCount(viewModel.constLearnCount.value)
    }

    private fun onPrevAnimal(list: MutableList<Animal>) {
        viewModel.decreaseLearnCount()
        val position = viewModel.learnCount.value
        Glide.with(requireContext()).load(list[position].imageUri?.toUri())
            .into(viewBinding.imMain)
        when (viewModel.locale.value) {
            LOCALE_RU -> speakOut(list[position].nameRu)
            LOCALE_EN -> speakOut(list[position].nameEn)
        }
        checkCount(viewModel.constLearnCount.value)
    }

    private fun checkCount(limit: Int) {
        viewBinding.apply {
            when (viewModel.learnCount.value) {
                0 -> {
                    showOrGone(0, false, imPrev)
                    showOrGone(0, true, imNext)
                }

                limit -> {
                    showOrGone(0, false, imNext)
                    showOrGone(0, true, ibToMain, imPrev)
                }

                else -> {
                    showOrGone(0, false, ibToMain)
                    showOrGone(0, true, imPrev, imNext)
                }
            }
        }
    }

    private fun initListeners() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewBinding.apply {
                imNext.setOnClickListener {
                    viewModel.animalList.value?.let { onNextAnimal(it) }
                }
                imPrev.setOnClickListener {
                    viewModel.animalList.value?.let { onPrevAnimal(it) }
                }
                cbQuiz.setOnClickListener {
                    viewBinding.apply {
                        if (cbQuiz.isChecked) {
                            appearScaleAnimation(1000, tvStartQuiz)
                            showOrGone(0, true, tvStartQuiz)
                            showOrGone(0, false, imNext, imPrev)
                        } else {
                            disappearScaleAnimation(1000, tvStartQuiz)
                            showOrGone(0, false, tvStartQuiz)
                            checkCount(viewModel.constLearnCount.value)
                        }
                    }
                }

                ibToMain.setOnClickListener {
                    viewModel.onBackPressed()
                }
                tvStartQuiz.setOnClickListener {
                    viewModel.onStartQuiz(viewModel.category.value)
                }
            }
        }
    }

    // TODO: будет же плеер, а не TTS
    private fun speakOut(text: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(500)
            textToSpeech!!.speak(text, TextToSpeech.QUEUE_FLUSH, null, "")
        }
    }

    // TODO: не парься, TTS временно
    override fun onInit(status: Int) {
        if (status == SUCCESS) {
            val locale = Locale(viewModel.locale.value)
            val result: Int = textToSpeech!!.setLanguage(locale)
            if (result == LANG_MISSING_DATA || result == LANG_NOT_SUPPORTED)
                showSnackbar(resources.getString(R.string.unsupported_language))
            // TODO: isEnabled ???
            else viewBinding.imMain.isEnabled = true
        } else showSnackbar(resources.getString(R.string.error))
    }

    private fun onBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewBinding.cbQuiz.isChecked) {
                    disappearScaleAnimation(1000, viewBinding.tvStartQuiz)
                    showOrGone(0, false, viewBinding.tvStartQuiz)
                    viewBinding.cbQuiz.isChecked = false
                    checkCount(viewModel.constLearnCount.value)
                } else {
                    if (viewModel.isDoubleBackPressed.value) viewModel.onBackPressed()
                    else showToast(getString(R.string.tap_again))
                    viewModel.doubleBackPressed()
                }
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, callback)
    }

    companion object {
        private const val LEARN_KEY = "learn_key"

        fun newInstance(category: String) = LearnFragment().apply {
            arguments = Bundle().apply {
                putString(LEARN_KEY, category)
            }
        }
    }
}

