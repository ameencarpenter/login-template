package com.carpenter.login.welcomeScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.carpenter.login.login.AuthRepository
import com.carpenter.login.login.LoginWithGoogle
import com.carpenter.login.ui.theme.CarpenterLoginTheme

class WelcomeFragment : Fragment() {

    //todo: bug fix; popEnterAnim not working.
    //todo: hide status bar in login screens.
    //todo: for phone numbers, let users choose the country
    //      then add the code and validate the number based on that country.

    private val model by viewModels<WelcomeViewModel> {
        WelcomeViewModelFactory(requireActivity().application, AuthRepository.getInstance())
    }
    private val googleLoginLauncher = registerForActivityResult(LoginWithGoogle()) {
        it ?: return@registerForActivityResult
        model.loginWithGoogle(it)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        observeError()
        observeSignedIn()

        return getScreen()
    }

    private fun getScreen() = ComposeView(requireContext()).apply {
        setContent {
            WelcomeScreen(model, findNavController(), googleLoginLauncher)
        }
    }

    private fun observeError() = model.error.observe(viewLifecycleOwner) {
        Toast.makeText(requireContext(), it ?: return@observe, Toast.LENGTH_SHORT).show()
        model.removeError()
    }

    private fun observeSignedIn() = model.signedIn.observe(viewLifecycleOwner) {
        if (it) navigateToHome()
    }

    private fun navigateToHome() {
        findNavController().navigate(WelcomeFragmentDirections.actionGlobalHomeFragment())
    }

}