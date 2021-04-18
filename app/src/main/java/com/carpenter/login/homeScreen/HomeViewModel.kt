package com.carpenter.login.homeScreen

import android.app.Application
import androidx.lifecycle.*
import com.carpenter.login.isDarkTheme
import com.carpenter.login.setIsDarkTheme
import com.carpenter.login.utils.isSystemDarkMode
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel(private val app: Application) : AndroidViewModel(app) {

    val isDarkTheme: Flow<Boolean> = app.isDarkTheme().map { it ?: app.isSystemDarkMode() }

    private val _signedIn = MutableLiveData(Firebase.auth.currentUser != null)
    val signedIn: LiveData<Boolean> = _signedIn

    init {
        Firebase.auth.addAuthStateListener {
            _signedIn.value = it.currentUser != null
        }
    }

    fun setIsDarkTheme(isDarkTheme: Boolean) = viewModelScope.launch {
        app.setIsDarkTheme(isDarkTheme)
    }

    fun signOut() = Firebase.auth.signOut()

}