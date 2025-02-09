package com.app.cinematalk.singup

import android.os.Bundle
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
import androidx.navigation.fragment.findNavController
import com.app.cinematalk.R
import com.app.cinematalk.login.UserCredentials
import com.app.cinematalk.utils.checkCredentials
import com.app.cinematalk.utils.closeKeyboard
import com.app.cinematalk.utils.isString

class SignUpFragment : Fragment() {

    private val signUpViewModel: SignUpViewModel by activityViewModels()
    private lateinit var mainContent: RelativeLayout
    private lateinit var backToLogin: ImageButton
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var signUpButton: ImageButton
    private lateinit var messageBox: TextView
    private lateinit var progressBarSignUp: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(
            R.layout.fragment_signup, container, false
        )
        // Initialize UI elements
        mainContent = view.findViewById(R.id.main_content)
        backToLogin = view.findViewById(R.id.back_to_login)
        emailInput = view.findViewById(R.id.email_input)
        passwordInput = view.findViewById(R.id.password_input)
        firstNameInput = view.findViewById(R.id.first_name_input)
        lastNameInput = view.findViewById(R.id.last_name_input)
        signUpButton = view.findViewById(R.id.sign_up_button)
        messageBox = view.findViewById(R.id.message_box)
        progressBarSignUp = view.findViewById(R.id.progress_bar_sign_up)

        // Set up event handlers
        handleSignUpClick(signUpButton)
        observeSignUpResult()
        backToLogin()

        return view
    }

    override fun onResume() {
        resetParameters()
        super.onResume()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        signUpViewModel.clearSignUpResult()
    }

    // Navigate back to login screen
    private fun backToLogin() {
        backToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
        }
    }

    // Reset input fields and message box
    private fun resetParameters() {
        emailInput.text.clear()
        passwordInput.text.clear()
        firstNameInput.text.clear()
        lastNameInput.text.clear()
        messageBox.text = ""
    }

    // Show loading indicator
    private fun showLoading() {
        mainContent.visibility = View.GONE
        progressBarSignUp.visibility = View.VISIBLE
    }

    // Hide loading indicator
    private fun hideLoading() {
        mainContent.visibility = View.VISIBLE
        progressBarSignUp.visibility = View.GONE
    }

    // Observe sign-up result and handle navigation or error messages
    private fun observeSignUpResult() {
        signUpViewModel.signUpResult.observe(viewLifecycleOwner) { result: String ->
            if (result == "Success") {
                findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
            } else {
                resetParameters()
                hideLoading()
                messageBox.visibility = View.VISIBLE
                messageBox.text = result
            }
        }
    }

    // Handle sign-up button click
    private fun handleSignUpClick(signUpButton: ImageButton) {
        signUpButton.setOnClickListener {
            closeKeyboard(requireContext(), requireView())
            messageBox.visibility = View.INVISIBLE
            val credentials =
                UserCredentials(emailInput.text.toString(), passwordInput.text.toString())
            val userProperties =
                UserProperties(firstNameInput.text.toString(), lastNameInput.text.toString())
            if (checkCredentials(credentials, emailInput, passwordInput) &&
                checkUserProperties(userProperties, firstNameInput, lastNameInput)
            ) {
                showLoading()
                signUpViewModel.signUpUser(credentials, userProperties)
            }
        }
    }

    // Validate user properties
    private fun checkUserProperties(
        userProperties: UserProperties, firstNameInput: EditText,
        lastNameInput: EditText
    ): Boolean {
        if (userProperties.firstName.isEmpty() || !isString(userProperties.firstName)) {
            firstNameInput.error = "Enter valid first name"
        } else if (userProperties.lastName.isEmpty() || !isString(userProperties.lastName)) {
            lastNameInput.error = "Enter valid last name"
        } else {
            return true
        }
        return false
    }
}