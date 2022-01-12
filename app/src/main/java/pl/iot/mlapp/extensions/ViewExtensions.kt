package pl.iot.mlapp.extensions

import android.view.View
import com.google.android.material.snackbar.Snackbar
import pl.iot.mlapp.R

fun View.showSnackbar(
    message: String,
    duration: Int = Snackbar.LENGTH_SHORT,
    actionTextColor: Int? = null,
    backgroundColor: Int? = null
) {
    val snackbar = Snackbar.make(this, message, duration)
    snackbar.setAction(this.context.getString(R.string.ok)) { snackbar.dismiss() }
    snackbar.anchorView = this
    actionTextColor?.let { snackbar.setActionTextColor(it) }
    backgroundColor?.let { snackbar.setBackgroundTint(it) }
    snackbar.show()
}