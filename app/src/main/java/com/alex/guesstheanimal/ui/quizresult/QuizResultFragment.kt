package com.alex.guesstheanimal.ui.quizresult

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import by.kirich1409.viewbindingdelegate.viewBinding
import com.alex.guesstheanimal.R
import com.alex.guesstheanimal.databinding.FragmentQuizResultBinding
import com.alex.guesstheanimal.utils.Const.ONE_STAR_RESULT
import com.alex.guesstheanimal.utils.Const.QUIZ_COUNT
import com.alex.guesstheanimal.utils.Const.THREE_STARS_RESULT
import com.alex.guesstheanimal.utils.appearScaleAnimation
import com.alex.guesstheanimal.utils.putStars
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.PartyFactory
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Spread
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class QuizResultFragment : Fragment(R.layout.fragment_quiz_result) {
    private val viewBinding: FragmentQuizResultBinding by viewBinding()
    private val viewModel: QuizResultViewModel by viewModels()
    private lateinit var mediaPlayer: MediaPlayer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.getInt(RESULT_KEY, -1).also {
            if (it != -1) it?.let { it1 -> viewModel.setResult(it1) }
        }
        setUi()
        onBackPressed()
        autoCloseFragment()
    }

    private fun setUi() {
        viewBinding.apply {
            appearScaleAnimation(1000, imStar1, imStar2, imStar3)
            speakOut(R.raw.ru_ovations)
            parade()
            viewLifecycleOwner.lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.result.collect {
                        setText(it!!)
                        when (it) {
                            0 -> putStars()
                            in 1..ONE_STAR_RESULT -> putStars(imStar1)
                            THREE_STARS_RESULT -> putStars(imStar1, imStar2, imStar3)
                            else -> putStars(imStar1, imStar2)
                        }
                    }
                }
            }
        }
    }

    private fun setText(result: Int) {
        viewBinding.apply {
            when (result) {
                0 -> tvCongrats.text = resources.getString(R.string.congrats_0)
                in 1..ONE_STAR_RESULT -> tvCongrats.text = resources.getString(R.string.congrats_1)
                THREE_STARS_RESULT -> tvCongrats.text = resources.getString(R.string.congrats_3)
                else -> tvCongrats.text = resources.getString(R.string.congrats_2)
            }
            tvResult.text = buildString {
                append(resources.getString(R.string.result))
                append(result.toString())
                append(resources.getString(R.string.slash))
                append(QUIZ_COUNT)
            }
        }
    }

    private fun onBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.onBackPressed()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    private fun speakOut(path: Int) {
        mediaPlayer = MediaPlayer.create(requireContext(), path)
        mediaPlayer.start()
    }

    private fun parade() {
        val emitterConfig = Emitter(9, TimeUnit.SECONDS).perSecond(30)
        viewBinding.konfettiView.start(
            PartyFactory(emitterConfig)
                .angle(Angle.RIGHT - 45)
                .spread(Spread.WIDE)
                .shapes(
                    listOf(
                        nl.dionsegijn.konfetti.core.models.Shape.Square,
                        nl.dionsegijn.konfetti.core.models.Shape.Circle
                    )
                )
                .colors(listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                .setSpeedBetween(10f, 30f)
                .position(Position.Relative(0.0, 0.5))
                .build(),
            PartyFactory(emitterConfig)
                .angle(Angle.LEFT + 45)
                .spread(Spread.WIDE)
                .shapes(
                    listOf(
                        nl.dionsegijn.konfetti.core.models.Shape.Square,
                        nl.dionsegijn.konfetti.core.models.Shape.Circle
                    )
                )
                .colors(listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                .setSpeedBetween(10f, 30f)
                .position(Position.Relative(1.0, 0.5))
                .build(),
            PartyFactory(emitterConfig)
                .angle(Angle.TOP - 25)
                .spread(Spread.WIDE)
                .shapes(
                    listOf(
                        nl.dionsegijn.konfetti.core.models.Shape.Square,
                        nl.dionsegijn.konfetti.core.models.Shape.Circle
                    )
                )
                .colors(listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                .setSpeedBetween(10f, 30f)
                .position(Position.Relative(0.4, 0.5))
                .build(),
            PartyFactory(emitterConfig)
                .angle(Angle.TOP + 25)
                .spread(Spread.WIDE)
                .shapes(
                    listOf(
                        nl.dionsegijn.konfetti.core.models.Shape.Square,
                        nl.dionsegijn.konfetti.core.models.Shape.Circle
                    )
                )
                .colors(listOf(0xfce18a, 0xff726d, 0xf4306d, 0xb48def))
                .setSpeedBetween(10f, 30f)
                .position(Position.Relative(0.6, 0.5))
                .build()
        )
    }

    private fun autoCloseFragment() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(10000)
            viewModel.onBackPressed()
        }
    }

    override fun onDestroyView() {
        if (this::mediaPlayer.isInitialized) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
        super.onDestroyView()
    }

    companion object {
        private const val RESULT_KEY = "result_key"

        fun newInstance(result: Int) = QuizResultFragment().apply {
            arguments = Bundle().apply {
                putInt(RESULT_KEY, result)
            }
        }
    }
}