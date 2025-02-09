package com.app.cinematalk.login

import LoginViewModel
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.app.cinematalk.R
import com.app.cinematalk.common.SharedViewModel
import com.app.cinematalk.utils.checkCredentials
import com.app.cinematalk.utils.closeKeyboard

class LoginFragment : Fragment() {
    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var mainContent: RelativeLayout
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var loginButton: ImageButton
    private lateinit var signupButton: Button
    private lateinit var messageBox: TextView
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var progressBarLogin: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(
            R.layout.fragment_login, container, false
        )
        // Initialize UI components
        mainContent = view.findViewById(R.id.main_content)
        emailInput = view.findViewById(R.id.email_input)
        passwordInput = view.findViewById(R.id.password_input)
        loginButton = view.findViewById(R.id.login_button)
        signupButton = view.findViewById(R.id.sign_up_button)
        messageBox = view.findViewById(R.id.message_box)
        progressBarLogin = view.findViewById(R.id.progress_bar_login)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]

        messageBox.visibility = View.INVISIBLE

        handleLoginClick()
        handleSignUpClick()
        observeLoginResult()

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear login result when the view is destroyed
        loginViewModel.clearLoginResult()
    }

    // Show loading indicator
    private fun showLoading() {
        mainContent.visibility = View.GONE
        progressBarLogin.visibility = View.VISIBLE
    }

    // Hide loading indicator
    private fun hideLoading() {
        mainContent.visibility = View.VISIBLE
        progressBarLogin.visibility = View.GONE
    }

    // Handle login button click
    private fun handleLoginClick() {
        loginButton.setOnClickListener {
            messageBox.visibility = View.INVISIBLE
            val credentials = UserCredentials(emailInput.text.toString(), passwordInput.text.toString())
            if (checkCredentials(credentials, emailInput, passwordInput)) {
                showLoading()
                loginViewModel.loginUser(credentials)
            }
        }
    }

    // Observe login result and update UI accordingly
    private fun observeLoginResult() {
        loginViewModel.loginResult.observe(viewLifecycleOwner) { result: Pair<HashMap<String, Any>, String> ->
            if (result.first.isNotEmpty() && result.second.isNotEmpty()) {
                // Valid result
                updateSharedViewModel(result)
                closeKeyboard(requireContext(), requireView())
                findNavController().navigate(R.id.action_loginFragment_to_reviewsFragment)
            } else if (result.first.isEmpty() && result.second.isEmpty()) {
                hideLoading()
            } else {
                // Invalid credentials case
                hideLoading()
                messageBox.visibility = View.VISIBLE
                messageBox.text = getString(R.string.invalidCreds)
            }
        }
    }

    // Update shared view model with user data
    private fun updateSharedViewModel(result: Pair<HashMap<String, Any>, String>) {
        sharedViewModel.userMetaData.email = result.second
        sharedViewModel.userMetaData.firstName = result.first["firstName"].toString()
        sharedViewModel.userMetaData.lastName = result.first["lastName"].toString()
        sharedViewModel.userMetaData.profilePhoto = result.first["profilePhoto"].toString()
    }

    // Handle sign-up button click
    private fun handleSignUpClick() {
        signupButton.setOnClickListener {
            messageBox.visibility = View.INVISIBLE
            closeKeyboard(requireContext(), requireView())
            findNavController().navigate(R.id.action_loginFragment_to_signupFragment)
        }
    }
}