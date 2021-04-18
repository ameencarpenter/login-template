package com.carpenter.login.welcomeScreen

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.launch
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.carpenter.login.R
import com.carpenter.login.utils.BurgerLayout

@Composable
fun WelcomeScreen(
    model: WelcomeViewModel,
    navController: NavController,
    googleLoginLauncher: ActivityResultLauncher<Unit>
) {
    val loading by model.loading.observeAsState(false)

    BurgerLayout(coverId = R.drawable.cover, showProgress = loading) {
        val modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 20.dp)

        Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
            AppName()
            BrandingSentence()
            //helper: when there's enough space it pushes the rest of the ui to the bottom.
            //when there's no enough space, it disappears and the scroll is enabled.
            Spacer(modifier = Modifier.weight(1f))
            //parameters defaults to email login
            SignInOption(enabled = !loading) { navController.navigateToEmailLogin() }
            GoogleSignInOption(enabled = !loading) { googleLoginLauncher.launch() }
            PhoneSignInOption(enabled = !loading) { navController.navigateToPhoneLogin() }
        }
    }
}

private fun NavController.navigateToPhoneLogin() {
    navigate(WelcomeFragmentDirections.actionWelcomeFragmentToPhoneLoginFragment())
}

private fun NavController.navigateToEmailLogin() {
    navigate(WelcomeFragmentDirections.actionWelcomeFragmentToEmailLoginFragment())
}

@Composable
private fun AppName() {
    Text(
        text = stringResource(id = R.string.app_name_placeholder),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp, end = 20.dp, bottom = 20.dp, start = 20.dp),
        fontSize = 30.sp,
        fontWeight = FontWeight.Bold,
        color = Color.White
    )
}

@Composable
private fun BrandingSentence() {
    Text(
        text = stringResource(id = R.string.branding_sentence),
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        fontSize = 26.sp,
        color = Color.White.copy(alpha = 0.5f)
    )
}

@Composable
private fun GoogleSignInOption(enabled: Boolean = true, onClick: () -> Unit) {
    SignInOption(
        textId = R.string.continue_with_google,
        textColor = Color.Black,
        icon = R.drawable.ic_google,
        background = Color.White,
        enabled = enabled
    ) { onClick() }
}

@Composable
private fun PhoneSignInOption(enabled: Boolean = true, onClick: () -> Unit) {
    SignInOption(
        textId = R.string.continue_with_phone,
        icon = R.drawable.ic_phone,
        background = Color(0xFF18771B),
        enabled = enabled
    ) { onClick() }
}

@Composable
private fun SignInOption(
    textId: Int = R.string.continue_with_email,
    textColor: Color = Color.White,
    icon: Int = R.drawable.ic_email,
    background: Color = Color.Red,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(background)
            .clickable(enabled = enabled) { onClick() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            Modifier
                .padding(10.dp)
                .size(40.dp)
        )

        Text(
            text = stringResource(id = textId),
            color = textColor,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
    }
}