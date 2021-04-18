package com.carpenter.login.phoneLoginScreen

import android.app.Activity
import android.app.Application
import androidx.lifecycle.*
import com.carpenter.login.isDarkTheme
import com.carpenter.login.login.AuthRepository
import com.carpenter.login.utils.connectedOrThrow
import com.carpenter.login.utils.isSystemDarkMode
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class PhoneLoginViewModel(
    private val app: Application,
    private val authRepository: AuthRepository
) : AndroidViewModel(app) {

    //note: You have to enable SafetyNet for phone login to work correctly:
    //https://firebase.google.com/docs/auth/android/phone-auth#enable-app-verification

    val isDarkTheme: Flow<Boolean> = app.isDarkTheme().map { it ?: app.isSystemDarkMode() }

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _signedIn = MutableLiveData(false)
    val signedIn: LiveData<Boolean> = _signedIn

    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error

    private val _page = MutableLiveData(PhoneLoginPage.ENTER_PHONE_NUMBER)
    val page: LiveData<PhoneLoginPage> = _page

    private val _sendingCodeAgain = MutableLiveData(false)
    val sendingCodeAgain: LiveData<Boolean> = _sendingCodeAgain

    private val _phoneNumber = MutableLiveData<String?>(null)
    val phoneNumber: LiveData<String?> = _phoneNumber

    private var verificationId: String? = null

    fun removeError() {
        _error.value = null
    }

    fun sendCode(phoneNumber: String, activity: Activity) = viewModelScope.launch {
        try {
            app.connectedOrThrow()
//            app.validPhoneNumberOrThrow(phoneNumber)
            _phoneNumber.postValue(phoneNumber)
            _loading.postValue(true)
            //It's okay to pass the activity here (in a view model) since it's a one time shot.
            //from firebase docs: The callbacks will be auto-detached when the Activity stops,
            //so you can freely write UI transition code in the callback methods.
            val options = PhoneAuthOptions.newBuilder(Firebase.auth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        } catch (e: Exception) {
            _error.postValue(e.localizedMessage)
        }
    }

    fun sendCodeAgain(activity: Activity) {
        //different from sendCode(), because sending code again shows different progressbar.
        //also, number has already been validated, no need to validate it again.
        try {
            app.connectedOrThrow()
            _phoneNumber.postValue(_phoneNumber.value!!)
            _sendingCodeAgain.postValue(true)
            //It's okay to pass the activity here (in a view model) since it's a one time shot.
            //from firebase docs: The callbacks will be auto-detached when the Activity stops,
            //so you can freely write UI transition code in the callback methods.
            val options = PhoneAuthOptions.newBuilder(Firebase.auth)
                .setPhoneNumber(_phoneNumber.value!!)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(activity)
                .setCallbacks(callbacks)
                .build()
            PhoneAuthProvider.verifyPhoneNumber(options)
        } catch (e: Exception) {
            _error.postValue(e.localizedMessage)
        }
    }

    fun verify(code: String) {
        try {
            app.connectedOrThrow()
            val credential = PhoneAuthProvider.getCredential(verificationId!!, code)
            login(credential)
        } catch (e: Exception) {
            _error.postValue(e.localizedMessage)
        }
    }

    private fun login(credential: PhoneAuthCredential) = viewModelScope.launch {
        try {
            _loading.postValue(true)
            authRepository.signInWithPhoneNumber(credential)
            _signedIn.postValue(true)
        } catch (e: Exception) {
            _error.postValue(e.localizedMessage)
        } finally {
            _loading.postValue(false)
        }
    }

    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            login(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            _error.postValue(e.localizedMessage)
            _loading.postValue(false)
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            this@PhoneLoginViewModel.verificationId = verificationId
            _loading.postValue(false)
            _page.postValue(PhoneLoginPage.VERIFY)
        }
    }

}

class PhoneLoginViewModelFactory(
    private val app: Application,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PhoneLoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PhoneLoginViewModel(app, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

enum class PhoneLoginPage { ENTER_PHONE_NUMBER, VERIFY }