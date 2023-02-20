package com.alex.guesstheanimal.utils

import android.view.View
import android.view.animation.Animation.RELATIVE_TO_SELF
import android.view.animation.ScaleAnimation
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.alex.guesstheanimal.R
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun Fragment.showSnackbar(text: String) {
    Snackbar.make(requireView(), text, Snackbar.LENGTH_SHORT).show()
}

fun Fragment.showToast(text: String) {
    Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
}

fun Fragment.showOrGone(delay: Long, show: Boolean, vararg views: View) {
    viewLifecycleOwner.lifecycleScope.launch {
        delay(delay)
        for (view in views) {
            view.visibility = if (show) View.VISIBLE
            else View.GONE
        }
    }
}

fun appearScaleAnimation(duration: Long, vararg views: View) {
    val scaleUp =
        ScaleAnimation(
            0.0f, 1.0f, 0.0f, 1.0f,
            RELATIVE_TO_SELF, 0.5f,
            RELATIVE_TO_SELF, 0.5f
        )
    scaleUp.duration = duration
    for (view in views) {
        view.startAnimation(scaleUp)
    }
}

fun disappearScaleAnimation(duration: Long, vararg views: View) {
    val scaleDown =
        ScaleAnimation(
            1.0f, 0.0f, 1.0f, 0.0f,
            RELATIVE_TO_SELF, 0.5f,
            RELATIVE_TO_SELF, 0.5f
        )
    scaleDown.duration = duration
    for (view in views) {
        view.startAnimation(scaleDown)
    }
}

fun Fragment.putStars(vararg views: ImageView) {
    for (view in views) {
        Glide.with(requireContext()).load(R.drawable.ic_star_full).into(view)
    }
}


