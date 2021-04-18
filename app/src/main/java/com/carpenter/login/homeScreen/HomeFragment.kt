package com.carpenter.login.homeScreen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController

class HomeFragment : Fragment() {

    private val model by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        observeSignedIn()

        return getScreen()
    }

    private fun getScreen() = ComposeView(requireContext()).apply {
        setContent { HomeScreen(model) }
    }

    private fun observeSignedIn() = model.signedIn.observe(viewLifecycleOwner) {
        if (!it) navigateToLogin()
    }

    private fun navigateToLogin() {
        findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToWelcomeFragment())
    }

}