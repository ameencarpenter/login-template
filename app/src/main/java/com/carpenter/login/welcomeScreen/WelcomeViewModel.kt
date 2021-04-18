@file:Suppress("SpellCheckingInspection")

package com.carpenter.login.welcomeScreen

import android.app.Application
import androidx.lifecycle.*
import com.carpenter.login.login.AuthRepository
import com.carpenter.login.utils.connectedOrThrow
import kotlinx.coroutines.launch

class WelcomeViewModel(
    private val app: Application,
    private val authRepository: AuthRepository
) : AndroidViewModel(app) {

    //note: You have to add SHA1 for google login to work.

    //to get SHA1 for debug mode, run this: keytool -list -v -keystore <debug keystore path> -alias androiddebugkey -storepass android -keystore android
    //debug keystore path example: C:\Users\sheri\.android\debug.keystore (replace "\Users\sheri\" with your username on your computer.

    //to get SHA1 for release mode, run this: keytool -list -v -alias keytstore -keystore <keystore path> -alias <alias name>
    //keystore path example: F:\Ameen\upload-keystore.jks
    //keystore alias name example: upload

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _signedIn = MutableLiveData(false)
    val signedIn: LiveData<Boolean> = _signedIn

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    fun removeError() {
        _error.value = null
    }

    fun loginWithGoogle(idToken: String) = viewModelScope.launch {
        try {
            app.connectedOrThrow()
            _loading.postValue(true)
            authRepository.loginWithGoogle(idToken)
            _signedIn.postValue(true)
        } catch (e: Exception) {
            _error.postValue(e.localizedMessage)
        } finally {
            _loading.postValue(false)
        }
    }

}

class WelcomeViewModelFactory(
    private val app: Application,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WelcomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WelcomeViewModel(app, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}