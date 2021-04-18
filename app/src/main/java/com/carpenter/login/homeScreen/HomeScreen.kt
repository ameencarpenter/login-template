package com.carpenter.login.homeScreen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ContentAlpha
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.carpenter.login.R
import com.carpenter.login.ui.theme.CarpenterLoginTheme

@Composable
fun HomeScreen(model: HomeViewModel = viewModel()) {
    val isDarkMode by model.isDarkTheme.collectAsState(initial = isSystemInDarkTheme())

    CarpenterLoginTheme(darkTheme = isDarkMode) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = MaterialTheme.colors.background)
                .verticalScroll(rememberScrollState())
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(5.dp)) {
                Text(
                    modifier = Modifier
                        .padding(10.dp)
                        .clickable { model.signOut() },
                    text = stringResource(id = R.string.log_out),
                    color = MaterialTheme.colors.error,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.weight(1f))
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { model.setIsDarkTheme(!isDarkMode) },
                )
            }
            Text(
                text = stringResource(R.string.welcome),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 30.dp, start = 15.dp, end = 15.dp),
                textAlign = TextAlign.Center,
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onBackground
            )
            Text(
                text = stringResource(R.string.description),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                color = MaterialTheme.colors.onBackground.copy(alpha = ContentAlpha.medium)
            )
            Image(
                painter = painterResource(id = R.drawable.carpenter),
                contentDescription = null,
                modifier = Modifier
                    .align(CenterHorizontally)
                    .size(75.dp)
                    .clip(CircleShape)
            )
            Text(
                text = stringResource(id = R.string.supported_login_methods_label),
                modifier = Modifier.padding(top = 30.dp, start = 5.dp, end = 5.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onBackground
            )
            Text(
                text = stringResource(id = R.string.supported_login_methods),
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 10.dp),
                lineHeight = 30.sp,
                color = MaterialTheme.colors.onBackground
            )
            Text(
                text = stringResource(id = R.string.components_used_label),
                modifier = Modifier.padding(top = 30.dp, start = 5.dp, end = 5.dp),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.onBackground
            )
            Text(
                text = stringResource(id = R.string.components_used),
                modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 10.dp),
                lineHeight = 30.sp,
                color = MaterialTheme.colors.onBackground
            )
        }
    }
}

@Preview
@Composable
fun Preview() {
    HomeScreen()
}