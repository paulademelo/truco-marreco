package com.example.choramarreco.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.example.choramarreco.R

fun Context.showGameDialog(
    title: String,
    message: String,
    positiveText: String,
    negativeText: String? = null,
    neutralText: String? = null,
    cancelable: Boolean = true,
    onPositiveClick: () -> Unit = {},
    onNegativeClick: () -> Unit = {},
    onNeutralClick: () -> Unit = {},
) {
    val dialog = AlertDialog.Builder(this, R.style.DialogTheme)
        .setTitle(title)
        .setMessage(message)
        .setPositiveButton(positiveText) { _, _ ->
            onPositiveClick()
        }
        .apply {
            if (negativeText != null) {
                setNegativeButton(negativeText) { _, _ ->
                    onNegativeClick()
                }
            }

            if (neutralText != null) {
                setNeutralButton(neutralText) { _, _ ->
                    onNeutralClick()
                }
            }
        }
        .setCancelable(cancelable)
        .create()

    dialog.setOnShowListener {
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            ?.setTextColor(ContextCompat.getColor(this, R.color.secondary_yellow))

        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            ?.setTextColor(ContextCompat.getColor(this, R.color.dialog_text))

        dialog.getButton(AlertDialog.BUTTON_NEUTRAL)
            ?.setTextColor(ContextCompat.getColor(this, R.color.secondary_yellow))
    }

    dialog.show()
}