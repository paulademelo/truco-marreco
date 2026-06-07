package com.example.choramarreco.utils

import android.view.MotionEvent
import android.view.View
import android.widget.TextView

fun View.setAnimatedClickListener(
    onClick: () -> Unit
) {
    setOnTouchListener { view, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                view.animate()
                    .scaleX(0.9f)
                    .scaleY(0.9f)
                    .setDuration(100)
                    .start()
                true
            }

            MotionEvent.ACTION_UP -> {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .start()

                view.performClick()
                true
            }

            MotionEvent.ACTION_CANCEL -> {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(150)
                    .start()
                true
            }

            else -> false
        }
    }

    setOnClickListener {
        onClick()
    }
}

fun TextView.animateScoreChange() {
    animate()
        .scaleX(1.15f)
        .scaleY(1.15f)
        .setDuration(100)
        .withEndAction {
            animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(120)
                .start()
        }
        .start()
}