package pl.iot.mlapp.extensions

import android.view.View
import com.google.android.material.snackbar.Snackbar
import pl.iot.mlapp.R

fun View.showSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_LONG,
    textColor: Int? = null
) {
    Snackbar.make(this, message, duration).apply {
        setAction(this.context.getString(R.string.ok)) { dismiss() }
        textColor?.let { setTextColor(it) }
        show()
    }
}