package com.carpenter.login.login

import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.carpenter.login.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class LoginWithGoogle : ActivityResultContract<Unit, String?>() {
    override fun createIntent(context: Context, input: Unit?): Intent {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.google_cloud_server_client_id))
            .requestEmail().build()
        val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
        return googleSignInClient.signInIntent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? {
        val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
        return try {
            val account = task.getResult(ApiException::class.java)!!
            account.idToken!!
        } catch (e: Exception) {
            null
        }
    }
}