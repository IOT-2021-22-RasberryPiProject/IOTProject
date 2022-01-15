package pl.iot.mlapp.functionality.notifications.presentation.extension

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations

fun <InputT, OutputT> LiveData<InputT>.map(mapper: (InputT) -> OutputT): LiveData<OutputT> =
    Transformations.map(this, mapper)