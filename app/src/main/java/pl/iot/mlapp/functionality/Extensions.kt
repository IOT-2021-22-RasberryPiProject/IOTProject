package pl.iot.mlapp.functionality

import android.view.View
import com.google.android.material.snackbar.Snackbar
import pl.iot.mlapp.R

fun View.showSnackbar(message: String) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_INDEFINITE)
    snackbar.setAction(this.context.getString(R.string.ok)) { snackbar.dismiss() }
    snackbar.show()
}