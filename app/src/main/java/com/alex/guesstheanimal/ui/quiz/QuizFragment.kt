package com.alex.guesstheanimal.ui.quiz

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
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
import com.alex.guesstheanimal.databinding.FragmentGameBinding
import com.alex.guesstheanimal.utils.Const.LOCALE_EN
import com.alex.guesstheanimal.utils.Const.LOCALE_RU
import com.alex.guesstheanimal.utils.Const.QUIZ_COUNT
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
class QuizFragment : Fragment(R.layout.fragment_game) {
    private val viewModel: QuizViewModel by viewModels()
    private val viewBinding: FragmentGameBinding by viewBinding()
    private lateinit var mediaPlayer: MediaPlayer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString(QUIZ_KEY).also {
            if (it != null) viewModel.setCategory(it)
        }
        setLocale()
        onBackPressed()
        collectData()
    }

    private fun collectData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.onChosenCategory().collectLatest { list ->
                    list.shuffle()
                    viewModel.setAnimalList(list)
                    setUi()
                }
            }
        }
    }

    // TODO: check if default EN
    private fun setLocale() {
        when (resources.configuration.locales[0].toString()) {
            LOCALE_RU -> viewModel.setLocale("ru")
            LOCALE_EN -> viewModel.setLocale("en")
        }
    }

    private fun setUi() {
        viewBinding.apply {
            initListeners()
            val list = viewModel.animalList.value!!
            list.shuffle()
            val randomFourList = mutableListOf(list[0], list[1], list[2], list[3])
            val animalAnswer = randomFourList.random()
            Log.e(TAG, "setUi: answer = ${animalAnswer.nameRu}")
            appearScaleAnimation(
                1000, imTopLeft, imTopRight, imBottomLeft, imBottomRight
            )
            showOrGone(0, true, imTopLeft, imTopRight, imBottomLeft, imBottomRight)
            viewModel.setRightAnswer(animalAnswer)
            when (viewModel.locale.value) {
                // TODO: soundpool???
                LOCALE_RU -> animalAnswer.soundUriRu?.let { speakOut(true, 500, it) }
                LOCALE_EN -> animalAnswer.soundUriEN?.let { speakOut(true, 700, it) }
            }
            tvTopLeft.text = randomFourList[0].nameRu
            Glide.with(requireContext()).load(randomFourList[0].imageUri?.toUri())
                .into(imTopLeft)
            tvTopRight.text = randomFourList[1].nameRu
            Glide.with(requireContext()).load(randomFourList[1].imageUri?.toUri())
                .into(imTopRight)
            tvBottomLeft.text = randomFourList[2].nameRu
            Glide.with(requireContext()).load(randomFourList[2].imageUri?.toUri())
                .into(imBottomLeft)
            tvBottomRight.text = randomFourList[3].nameRu
            Glide.with(requireContext()).load(randomFourList[3].imageUri?.toUri())
                .into(imBottomRight)
            list.remove(animalAnswer)
        }
    }

    private fun initListeners() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(1000)
            viewBinding.apply {
                tvTopLeft.setOnClickListener { checkAnswer(tvTopLeft, wrongTopLeft) }
                tvTopRight.setOnClickListener { checkAnswer(tvTopRight, wrongTopRight) }
                tvBottomLeft.setOnClickListener { checkAnswer(tvBottomLeft, wrongBottomLeft) }
                tvBottomRight.setOnClickListener { checkAnswer(tvBottomRight, wrongBottomRight) }
            }
        }
    }

    private fun disableInitListeners() {
        viewBinding.apply {
            tvTopLeft.setOnClickListener { }
            tvTopRight.setOnClickListener { }
            tvBottomLeft.setOnClickListener { }
            tvBottomRight.setOnClickListener { }
        }
    }

    private fun checkAnswer(view: TextView, wrongAnswer: ImageView) {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewBinding.apply {
                    val btnText = view.text.toString()
                    if (!viewModel.failRound.value) {
                        if (btnText == viewModel.rightAnswer.value?.nameRu) {
                            viewModel.increaseRightAnswersCount()
                            viewModel.isFailRound(false)
                            checkQuizCount()
                            disappearScaleAnimation(
                                1000, imTopLeft, imTopRight, imBottomLeft, imBottomRight
                            )
                            disableInitListeners()
                            showOrGone(
                                1000,
                                false, imTopLeft, imTopRight, imBottomLeft, imBottomRight
                            )
                            viewModel.rightAnswer.value?.let { showRightAnswer(it) }
                        } else {
                            viewModel.isFailRound(true)
                            showOrGone(0, true, wrongAnswer)
                            appearScaleAnimation(250, wrongAnswer)
                        }
                    } else {
                        if (btnText == viewModel.rightAnswer.value?.nameRu) {
                            viewModel.isFailRound(false)
                            checkQuizCount()
                            disappearScaleAnimation(
                                1000, imTopLeft, imTopRight, imBottomLeft, imBottomRight
                            )
                            disableInitListeners()
                            showOrGone(
                                1000, false, imTopLeft, imTopRight, imBottomLeft, imBottomRight
                            )
                            viewModel.rightAnswer.value?.let { showRightAnswer(it) }
                        } else {
                            showOrGone(0, true, wrongAnswer)
                            appearScaleAnimation(250, wrongAnswer)
                        }
                    }
                }
            }
            delay(5000)
        }
    }

    private fun showRightAnswer(animalAnswer: Animal) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewBinding.apply {
                Glide.with(requireContext()).load(animalAnswer.imageUri).into(imRightAnswer)
                showOrGone(0, true, imRightAnswer)
                appearScaleAnimation(1000, imRightAnswer)
                delay(1000)
                when (viewModel.locale.value) {
                    LOCALE_RU -> animalAnswer.soundUriRu?.let { speakOut(false, 0, it) }
                    LOCALE_EN -> animalAnswer.soundUriEN?.let { speakOut(false, 0, it) }
                }
                delay(3000)
                disappearScaleAnimation(1000, imRightAnswer)
                showOrGone(0, false, imRightAnswer)
            }
        }
    }

    // TODO: check
    private fun checkQuizCount() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewBinding.apply {
                if (viewModel.quizCount.value == QUIZ_COUNT) {
                    // TODO: ???
                    delay(4000)
                    viewModel.onResult(viewModel.rightAnswersCount.value)
                } else {
                    viewModel.increaseQuizCount()
                    showOrGone(
                        100, false, wrongTopLeft, wrongTopRight, wrongBottomLeft, wrongBottomRight
                    )
                }
                // TODO: ???
                delay(4000)
                setUi()
            }
        }
    }

    private fun speakOut(isQuestion: Boolean, delay: Long, path: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(500)
            if (isQuestion) {
                mediaPlayer = if (viewModel.locale.value == "ru") MediaPlayer.create(
                    requireContext(),
                    R.raw.ru_where
                )
                else MediaPlayer.create(requireContext(), R.raw.en_where)
                mediaPlayer.start()
                delay(delay)
                mediaPlayer = MediaPlayer.create(requireContext(), path.toUri())
                mediaPlayer.start()
            } else {
                mediaPlayer = MediaPlayer.create(requireContext(), path.toUri())
                mediaPlayer.start()
            }
        }
    }

    private fun onBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (viewModel.isDoubleBackPressed.value) viewModel.onBackPressed()
                else showToast(getString(R.string.tap_again))
                viewModel.doubleBackPressed()
            }
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, callback)
    }

    override fun onDestroyView() {
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        super.onDestroyView()
    }

    companion object {
        private const val TAG = "e"
        private const val QUIZ_KEY = "quiz_key"

        fun newInstance(category: String) = QuizFragment().apply {
            arguments = Bundle().apply {
                putString(QUIZ_KEY, category)
            }
        }
    }
}