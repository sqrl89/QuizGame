package com.alex.guesstheanimal.ui.quiz

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.LANG_MISSING_DATA
import android.speech.tts.TextToSpeech.LANG_NOT_SUPPORTED
import android.speech.tts.TextToSpeech.OnInitListener
import android.speech.tts.TextToSpeech.QUEUE_FLUSH
import android.speech.tts.TextToSpeech.SUCCESS
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
import com.alex.guesstheanimal.data.database.Animal
import com.alex.guesstheanimal.databinding.FragmentGameBinding
import com.alex.guesstheanimal.utils.Const.LOCALE_EN
import com.alex.guesstheanimal.utils.Const.LOCALE_RU
import com.alex.guesstheanimal.utils.Const.QUIZ_COUNT
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
class QuizFragment : Fragment(R.layout.fragment_game), OnInitListener {
    private val viewModel: QuizViewModel by viewModels()
    private val viewBinding: FragmentGameBinding by viewBinding()
    private var textToSpeech: TextToSpeech? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getString(QUIZ_KEY).also {
            if (it != null) viewModel.setCategory(it)
        }
        textToSpeech = TextToSpeech(requireContext(), null)
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
            val randomFourList = mutableListOf(list[0], list[1], list[2], list[3])
            val animalAnswer = randomFourList.random()
            Log.e(TAG, "setUi: answer = ${animalAnswer.nameRu}")
            appearScaleAnimation(
                1000, imTopLeft, imTopRight, imBottomLeft, imBottomRight
            )
            showOrGone(0, true, imTopLeft, imTopRight, imBottomLeft, imBottomRight)
            viewModel.setRightAnswer(animalAnswer)
            when (viewModel.locale.value) {
                LOCALE_RU -> speakOut(true, animalAnswer.nameRu)
                LOCALE_EN -> speakOut(true, animalAnswer.nameEn)
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
//                speakOut(false, animalAnswer.nameRu)
                when (viewModel.locale.value) {
                    LOCALE_RU -> speakOut(false, animalAnswer.nameRu)
                    LOCALE_EN -> speakOut(false, animalAnswer.nameEn)
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

    // TODO: не парься, TTS временно
    override fun onInit(status: Int) {
        if (status == SUCCESS) {
            val locale = Locale(viewModel.locale.value)
            val result: Int = textToSpeech!!.setLanguage(locale)
            if (result == LANG_MISSING_DATA || result == LANG_NOT_SUPPORTED)
                showSnackbar(resources.getString(R.string.unsupported_language))
            // TODO: isEnabled ???
            else viewBinding.gameContainer.isEnabled = true

        } else showSnackbar(resources.getString(R.string.error))
    }

    // TODO: будет же плеер, а не TTS
    private fun speakOut(isQuestion: Boolean, text: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(500)
            if (isQuestion) textToSpeech!!.speak("Где $text ?", QUEUE_FLUSH, null, "")
            else textToSpeech!!.speak(text, QUEUE_FLUSH, null, "")
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
        super.onDestroyView()
        if (textToSpeech != null) {
            textToSpeech!!.stop()
            textToSpeech!!.shutdown()
        }
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