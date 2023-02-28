package com.alex.guesstheanimal.ui.learn

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
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
import com.alex.guesstheanimal.database.Animal
import com.alex.guesstheanimal.databinding.FragmentLearnBinding
import com.alex.guesstheanimal.utils.Const.CATEGORY_DIGITS
import com.alex.guesstheanimal.utils.Const.CATEGORY_LETTERS_EN
import com.alex.guesstheanimal.utils.Const.CATEGORY_LETTERS_RU
import com.alex.guesstheanimal.utils.Const.LOCALE_EN
import com.alex.guesstheanimal.utils.Const.LOCALE_RU
import com.alex.guesstheanimal.utils.appearScaleAnimation
import com.alex.guesstheanimal.utils.disappearScaleAnimation
import com.alex.guesstheanimal.utils.showOrGone
import com.alex.guesstheanimal.utils.showToast
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LearnFragment : Fragment(R.layout.fragment_learn) {
    private val viewModel: LearnViewModel by viewModels()
    private val viewBinding: FragmentLearnBinding by viewBinding()
    private lateinit var mediaPlayer: MediaPlayer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString(LEARN_KEY).also {
            if (it != null) viewModel.setCategory(it)
        }
        initListeners()
        onBackPressed()
        setLocale()
        setFirstAnimal()
    }

    // TODO: check if default EN and in VM
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
                    // TODO: refactor
                    val cat = viewModel.category.value
                    if (cat == CATEGORY_DIGITS || cat == CATEGORY_LETTERS_RU || cat == CATEGORY_LETTERS_EN) {
                        Log.e("TAG", "setFirstAnimal: $cat")
                    } else list.shuffle()
                    viewModel.setAnimalList(list)
                    Glide.with(requireContext()).load(list[0].imageUri?.toUri())
                        .into(viewBinding.imMain)
                    when (viewModel.locale.value) {
                        LOCALE_RU -> list[0].soundUriRu?.let { speakOut(it) }
                        LOCALE_EN -> list[0].soundUriEN?.let { speakOut(it) }
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
            LOCALE_RU -> list[position].soundUriRu?.let { speakOut(it) }
            LOCALE_EN -> list[position].soundUriEN?.let { speakOut(it) }
        }
        checkCount(viewModel.constLearnCount.value)
    }

    private fun onPrevAnimal(list: MutableList<Animal>) {
        viewModel.decreaseLearnCount()
        val position = viewModel.learnCount.value
        Glide.with(requireContext()).load(list[position].imageUri?.toUri())
            .into(viewBinding.imMain)
        when (viewModel.locale.value) {
            LOCALE_RU -> list[position].soundUriRu?.let { speakOut(it) }
            LOCALE_EN -> list[position].soundUriEN?.let { speakOut(it) }
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
                            mediaPlayer = if (viewModel.locale.value == "ru") MediaPlayer.create(
                                requireContext(), R.raw.ru_play
                            )
                            else MediaPlayer.create(requireContext(), R.raw.en_play)
                            mediaPlayer.start()
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

    private fun speakOut(path: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(500)
            mediaPlayer = MediaPlayer.create(requireContext(), path.toUri())
            mediaPlayer.start()
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer.stop()
        mediaPlayer.release()
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

