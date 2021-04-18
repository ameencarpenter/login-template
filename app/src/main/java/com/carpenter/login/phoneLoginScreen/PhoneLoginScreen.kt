package com.carpenter.login.phoneLoginScreen

import android.app.Activity
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.carpenter.login.R
import com.carpenter.login.ui.theme.CarpenterLoginTheme
import com.carpenter.login.utils.Header
import com.carpenter.login.utils.InputField
import com.carpenter.login.utils.PhoneNumberInput
import com.carpenter.login.utils.SubmitButton

@ExperimentalComposeUiApi
@Composable
fun PhoneLoginScreen(model: PhoneLoginViewModel, activity: Activity) {
    val page by model.page.observeAsState(PhoneLoginPage.ENTER_PHONE_NUMBER)
    val loading by model.loading.observeAsState(false)
    val sendingCodeAgain by model.sendingCodeAgain.observeAsState(false)
    val phoneNumber by model.phoneNumber.observeAsState(null)
    val isDarkMode by model.isDarkTheme.collectAsState(initial = isSystemInDarkTheme())

    CarpenterLoginTheme(darkTheme = isDarkMode) {
        Crossfade(page) {
            when (page) {
                PhoneLoginPage.ENTER_PHONE_NUMBER -> {
                    PhoneLogin(loading) { number ->
                        model.sendCode(number, activity)
                    }
                }
                PhoneLoginPage.VERIFY -> {
                    CodeVerification(
                        phoneNumber = phoneNumber!!,
                        loading = loading,
                        sendingCodeAgain = sendingCodeAgain,
                        onSendAgain = { model.sendCodeAgain(activity) },
                        onDone = { model.verify(it) }
                    )
                }
            }
        }
    }
}

@ExperimentalComposeUiApi
@Composable
private fun PhoneLogin(
    loading: Boolean = false,
    onDone: (String) -> Unit
) {
    val phoneNumber = rememberSaveable { mutableStateOf("") }
    val valid = remember(phoneNumber.value) { phoneNumber.value.trim().isNotEmpty() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.background)
        .verticalScroll(rememberScrollState())

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Header(textId = R.string.login_with_phone)
        PhoneNumberInput(numberState = phoneNumber, enabled = !loading, onAction = KeyboardActions {
            //The submit button is disabled unless the inputs are valid. wrap this in if statement to accomplish the same.
            if (!valid) return@KeyboardActions
            onDone(phoneNumber.value.trim())
            keyboardController?.hide()
        })
        SubmitButton(
            textId = R.string.send_code,
            loading = loading,
            validInputs = valid
        ) {
            onDone(phoneNumber.value.trim())
            keyboardController?.hide()
        }
    }
}

@ExperimentalComposeUiApi
@Composable
private fun CodeVerification(
    phoneNumber: String,
    loading: Boolean = false,
    sendingCodeAgain: Boolean = false,
    onSendAgain: () -> Unit,
    onDone: (String) -> Unit
) {
    val code = rememberSaveable { mutableStateOf("") }
    val valid = remember(code.value) { code.value.trim().isNotEmpty() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val modifier = Modifier
        .fillMaxSize()
        .background(MaterialTheme.colors.background)
        .verticalScroll(rememberScrollState())

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Header(textId = R.string.verify_your_number)
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            text = stringResource(id = R.string.code_sent_to_number, phoneNumber),
            color = MaterialTheme.colors.onBackground
        )
        InputField(
            valueState = code,
            labelId = R.string.verification_code,
            enabled = !loading && !sendingCodeAgain,
            imeAction = ImeAction.Done,
            onAction = KeyboardActions {
                //The submit button is disabled unless the inputs are valid. wrap this in if statement to accomplish the same.
                if (!valid) return@KeyboardActions
                onDone(code.value.trim())
                keyboardController?.hide()
            }
        )
        SubmitButton(
            textId = R.string.verify,
            loading = loading,
            validInputs = valid
        ) {
            onDone(code.value.trim())
            keyboardController?.hide()
        }
        Text(
            text = stringResource(id = R.string.did_not_receive_code),
            modifier = Modifier
                .clickable(enabled = !loading) { onSendAgain() }
                .padding(start = 20.dp, end = 20.dp, top = 20.dp),
            color = MaterialTheme.colors.onBackground
        )
        if (sendingCodeAgain) CircularProgressIndicator(modifier = Modifier.size(25.dp))
    }
}