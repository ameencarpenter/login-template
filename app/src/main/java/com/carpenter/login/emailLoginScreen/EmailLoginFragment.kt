package com.carpenter.login.emailLoginScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.carpenter.login.login.AuthRepository

class EmailLoginFragment : Fragment() {

    private val model by viewModels<EmailLoginViewModel> {
        EmailLoginViewModelFactory(requireActivity().application, AuthRepository.getInstance())
    }

    @ExperimentalComposeUiApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        observeError()
        observeSignedIn()

        return getScreen()
    }

    @ExperimentalComposeUiApi
    private fun getScreen() = ComposeView(requireContext()).apply {
        setContent { EmailLoginScreen(model) }
    }

    private fun observeError() = model.error.observe(viewLifecycleOwner) {
        Toast.makeText(requireContext(), it ?: return@observe, Toast.LENGTH_SHORT).show()
        model.removeError()
    }

    private fun observeSignedIn() = model.signedIn.observe(viewLifecycleOwner) {
        if (it) navigateToHome()
    }

    private fun navigateToHome() {
        findNavController().navigate(EmailLoginFragmentDirections.actionGlobalHomeFragment())
    }
}