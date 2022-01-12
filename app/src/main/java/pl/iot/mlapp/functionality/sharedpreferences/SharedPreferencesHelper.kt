package pl.iot.mlapp.functionality.sharedpreferences

import android.content.Context

class SharedPreferencesHelper(context: Context) {
    private val sharedPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun getStringValue(key: String, defaultValue: String = ""): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun saveStringValue(key: String, value: String) = with(sharedPreferences.edit()) {
        putString(key, value)
        apply()
    }

    companion object {
        private const val SHARED_PREFERENCES_NAME = "IOT_PROJECT_SHARED_PREFERENCES"
    }
}