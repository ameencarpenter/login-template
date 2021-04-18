package com.carpenter.login.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Patterns
import com.carpenter.login.R
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

//Don't touch this. It's behind your reasoning!
@Suppress("UNCHECKED_CAST", "EXPERIMENTAL_API_USAGE")
suspend fun <T> Task<T>.await(): T {
    if (isComplete) {
        val e = exception
        return if (e == null) {
            if (isCanceled) throw CancellationException("Task $this was cancelled normally.")
            else result as T
        } else {
            throw e
        }
    }

    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener {
            val e = exception
            if (e == null) {
                if (isCanceled) cont.cancel() else cont.resume(result as T) {}
            } else {
                cont.resumeWithException(e)
            }
        }
    }
}

fun Context.validPasswordOrThrow(password: String) {
    if (password.length < 6) throw Exception(getString(R.string.password_should_be_at_least_6_characters))
}

fun Context.validEmailOrThrow(email: String) {
    if (!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        throw Exception(getString(R.string.not_a_valid_email))
}

//region
//note: this validates Egyptian numbers only.
//fun Context.validPhoneNumberOrThrow(phoneNumber: String) {
//    //starts with '+20', followed by 1, followed by 0 for 010 or 1 for 011 or 2 for 012 or 5 for 015, then the remaining 8 digits.
//    if (!Pattern.matches(
//            "\\+20[1][0125]\\d{8}",
//            phoneNumber
//        )
//    ) throw Exception(getString(R.string.not_a_valid_number))
//}
//endregion

private fun ConnectivityManager.isConnected(): Boolean {
    val capabilities = getNetworkCapabilities(activeNetwork) ?: return false
    val wifiConnected = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    val mobileDataActive = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    val ethernetConnected = capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
    return wifiConnected || mobileDataActive || ethernetConnected
}

fun Context.isConnected(): Boolean {
    return (this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).isConnected()
}

fun Context.connectedOrThrow() {
    if (!isConnected()) throw Exception(getString(R.string.you_are_offline))
}

fun Context.isSystemDarkMode(): Boolean {
    return (resources.configuration.uiMode + Configuration.UI_MODE_NIGHT_MASK) == UI_MODE_NIGHT_YES
}