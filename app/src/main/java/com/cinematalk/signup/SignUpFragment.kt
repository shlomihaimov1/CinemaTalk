package com.app.cinematalk.singup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.app.cinematalk.R


class SignUpFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(
            R.layout.fragment_signup, container, false
        )

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
    // private fun backToLogin() {
    //     backToLogin.setOnClickListener {
    //         findNavController().navigate(R.id.action_signupFragment_to_loginFragment)
    //     }
    // }

}