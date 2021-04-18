package com.carpenter.login.emailLoginScreen

import android.app.Application
import androidx.lifecycle.*
import com.carpenter.login.R
import com.carpenter.login.isDarkTheme
import com.carpenter.login.login.AuthRepository
import com.carpenter.login.utils.connectedOrThrow
import com.carpenter.login.utils.isSystemDarkMode
import com.carpenter.login.utils.validEmailOrThrow
import com.carpenter.login.utils.validPasswordOrThrow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class EmailLoginViewModel(
    private val app: Application,
    private val authRepository: AuthRepository
) : AndroidViewModel(app) {

    val isDarkTheme: Flow<Boolean> = app.isDarkTheme().map { it ?: app.isSystemDarkMode() }

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _signedIn = MutableLiveData(false)
    val signedIn: LiveData<Boolean> = _signedIn

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _page = MutableLiveData(Page.SIGN_UP)
    val page: LiveData<Page> = _page

    fun removeError() {
        _error.value = null
    }

    val changePage: (Page) -> Unit = {
        _page.value = it
    }

    fun signUp(email: String, password: String, confirmedPassword: String) = launchDataLoad {
        //validate inputs
        app.validEmailOrThrow(email)
        app.validPasswordOrThrow(password)
        if (confirmedPassword != password) throw IllegalArgumentException(app.getString(R.string.passwords_do_not_match))
        //sign up
        authRepository.signUpWithEmail(email, password)
        //notify user is signed in
        _signedIn.postValue(true)
    }

    fun signIn(email: String, password: String) = launchDataLoad {
        //validate inputs
        app.validEmailOrThrow(email)
        app.validPasswordOrThrow(password)
        authRepository.signInWithEmail(email, password)
        _signedIn.postValue(true)
    }

    fun sendPasswordResetEmail(email: String) = launchDataLoad {
        app.validEmailOrThrow(email)
        authRepository.sendPasswordResetEmail(email)
        _page.postValue(Page.SIGN_IN)
    }

    private fun launchDataLoad(block: suspend () -> Unit) = viewModelScope.launch {
        try {
            app.connectedOrThrow()
            _loading.postValue(true)
            block()
        } catch (e: Exception) {
            _error.postValue(e.localizedMessage)
        } finally {
            _loading.postValue(false)
        }
    }

}

class EmailLoginViewModelFactory(
    private val app: Application,
    private val authRepository: AuthRepository
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EmailLoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EmailLoginViewModel(app, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

enum class Page { SIGN_UP, SIGN_IN, RECOVER_ACCOUNT }