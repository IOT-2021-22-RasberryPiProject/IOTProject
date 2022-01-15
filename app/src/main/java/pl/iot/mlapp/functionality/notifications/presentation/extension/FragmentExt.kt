package pl.iot.mlapp.functionality.notifications.presentation.extension

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData

fun <T> Fragment.observe(liveData: LiveData<T>, observer: (T) -> (Unit)) {
    liveData.observe(viewLifecycleOwner, { item -> observer(item) })
}