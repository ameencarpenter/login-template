package com.carpenter.login

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

//data store
val Context.dataStore by preferencesDataStore(name = "settings")

//dark theme key
val IS_DARK_THEME = booleanPreferencesKey("IS_DARK_THEME")

//read dark theme
fun Context.isDarkTheme(): Flow<Boolean?> {
    return dataStore.data.map { it[IS_DARK_THEME] }
}

//write to dark theme
suspend fun Context.setIsDarkTheme(isDarkTheme: Boolean) = withContext(IO) {
    dataStore.edit { it[IS_DARK_THEME] = isDarkTheme }
}
