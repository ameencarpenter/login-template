package com.carpenter.login.emailLoginScreen

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.carpenter.login.R
import com.carpenter.login.ui.theme.CarpenterLoginTheme
import com.carpenter.login.utils.EmailInput
import com.carpenter.login.utils.Header
import com.carpenter.login.utils.PasswordInput
import com.carpenter.login.utils.SubmitButton

@ExperimentalComposeUiApi
@Composable
fun EmailLoginScreen(model: EmailLoginViewModel) {
    val loading by model.loading.observeAsState(false)
    val page by model.page.observeAsState(Page.SIGN_UP)
    val isDarkMode by model.isDarkTheme.collectAsState(initial = isSystemInDarkTheme())

    CarpenterLoginTheme(darkTheme = isDarkMode) {
        Crossfade(page) {
            when (page) {
                Page.SIGN_UP -> {
                    EmailSignUp(
                        loading,
                        model.changePage
                    ) { email: String, password: String, confirmedPassword: String ->
                        model.signUp(email, password, confirmedPassword)
                    }
                }
                Page.SIGN_IN -> {
                    EmailSignIn(loading, model.changePage) { email: String, password: String ->
                        model.signIn(email, password)
                    }
                }
                Page.RECOVER_ACCOUNT -> {
                    RecoverAccount(loading, model.changePage) { email ->
                        model.sendPasswordResetEmail(email)
                    }
                }
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
private fun EmailSignUp(
    loading: Boolean = false,
    onPageChange: (Page) -> Unit,
    onDone: (String, String, String) -> Unit
) {
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val confirmedPassword = rememberSaveable { mutableStateOf("") }
    val passwordVisibility = rememberSaveable { mutableStateOf(false) }
    val (passwordFocusRequester, confirmedPasswordFocusRequester) = FocusRequester.createRefs()
    val keyboardController = LocalSoftwareKeyboardController.current
    val valid = remember(email.value, password.value, confirmedPassword.value) {
        val emailIsNotEmpty = email.value.trim().isNotEmpty()
        val passwordIsNotEmpty = password.value.trim().isNotEmpty()
        val confirmedPasswordIsNotEmpty = confirmedPassword.value.trim().isNotEmpty()
        emailIsNotEmpty && passwordIsNotEmpty && confirmedPasswordIsNotEmpty
    }

    val modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.background)
        .verticalScroll(rememberScrollState())

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Header(textId = R.string.sign_up_with_email)
        EmailInput(emailState = email, enabled = !loading, onAction = KeyboardActions {
            passwordFocusRequester.requestFocus()
        })
        PasswordInput(
            modifier = Modifier.focusRequester(passwordFocusRequester),
            passwordState = password,
            enabled = !loading,
            passwordVisibility = passwordVisibility,
            imeAction = ImeAction.Next,
            onAction = KeyboardActions {
                confirmedPasswordFocusRequester.requestFocus()
            }
        )
        PasswordInput(
            modifier = Modifier.focusRequester(confirmedPasswordFocusRequester),
            passwordState = confirmedPassword,
            labelId = R.string.confirm_password,
            enabled = !loading,
            passwordVisibility = passwordVisibility,
            onAction = KeyboardActions {
                //The submit button is disabled unless the inputs are valid. wrap this in if statement to accomplish the same.
                if (!valid) return@KeyboardActions
                onDone(email.value.trim(), password.value.trim(), confirmedPassword.value.trim())
                keyboardController?.hide()
            }
        )
        SubmitButton(
            textId = R.string.sign_up,
            loading = loading,
            validInputs = valid
        ) {
            onDone(email.value.trim(), password.value.trim(), confirmedPassword.value.trim())
            keyboardController?.hide()
        }
        Text(
            text = stringResource(id = R.string.have_account_already_sign_in_instead),
            modifier = Modifier
                .clickable(enabled = !loading) { onPageChange(Page.SIGN_IN) }
                .padding(vertical = 20.dp, horizontal = 10.dp),
            color = MaterialTheme.colors.onBackground
        )
    }
}

@ExperimentalComposeUiApi
@Composable
private fun EmailSignIn(
    loading: Boolean = false,
    onPageChange: (Page) -> Unit,
    onDone: (String, String) -> Unit
) {
    val email = rememberSaveable { mutableStateOf("") }
    val password = rememberSaveable { mutableStateOf("") }
    val passwordVisibility = rememberSaveable { mutableStateOf(false) }
    val passwordFocusRequester = FocusRequester.Default
    val keyboardController = LocalSoftwareKeyboardController.current
    val valid = remember(email.value, password.value) {
        email.value.trim().isNotEmpty() && password.value.trim().isNotEmpty()
    }
    val modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.background)
        .verticalScroll(rememberScrollState())

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Header(textId = R.string.sign_in_with_email)
        EmailInput(emailState = email, enabled = !loading, onAction = KeyboardActions {
            passwordFocusRequester.requestFocus()
        })
        PasswordInput(
            modifier = Modifier.focusRequester(passwordFocusRequester),
            passwordState = password,
            enabled = !loading,
            passwordVisibility = passwordVisibility,
            onAction = KeyboardActions {
                //The submit button is disabled unless the inputs are valid. wrap this in if statement to accomplish the same.
                if (!valid) return@KeyboardActions
                onDone(email.value.trim(), password.value.trim())
                keyboardController?.hide()
            }
        )
        SubmitButton(
            textId = R.string.sign_in,
            loading = loading,
            validInputs = valid
        ) {
            onDone(email.value.trim(), password.value.trim())
            keyboardController?.hide()
        }
        Text(
            text = stringResource(id = R.string.forgot_password),
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 10.dp)
                .clickable(enabled = !loading) {
                    onPageChange(Page.RECOVER_ACCOUNT)
                },
            color = MaterialTheme.colors.onBackground
        )
        Text(
            text = stringResource(id = R.string.do_not_have_account_sign_up_instead),
            modifier = Modifier
                .clickable(enabled = !loading) { onPageChange(Page.SIGN_UP) }
                .padding(10.dp),
            color = MaterialTheme.colors.onBackground
        )
    }

}

@ExperimentalComposeUiApi
@Composable
private fun RecoverAccount(
    loading: Boolean = false,
    onPageChange: (Page) -> Unit,
    onDone: (String) -> Unit
) {
    val email = rememberSaveable { mutableStateOf("") }
    val valid = remember(email.value) { email.value.trim().isNotEmpty() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.background)
        .verticalScroll(rememberScrollState())

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Header(textId = R.string.recover_your_account)
        EmailInput(
            emailState = email,
            enabled = !loading,
            imeAction = ImeAction.Done,
            onAction = KeyboardActions {
                //The submit button is disabled unless the inputs are valid. wrap this in if statement to accomplish the same.
                if (!valid) return@KeyboardActions
                onDone(email.value.trim())
                keyboardController?.hide()
            })
        SubmitButton(
            textId = R.string.send,
            loading = loading,
            validInputs = valid
        ) {
            onDone(email.value.trim())
            keyboardController?.hide()
        }
        Text(
            text = stringResource(id = R.string.go_back),
            modifier = Modifier
                .clickable(enabled = !loading) { onPageChange(Page.SIGN_IN) }
                .padding(vertical = 20.dp, horizontal = 10.dp),
            color = MaterialTheme.colors.onBackground
        )
    }
}