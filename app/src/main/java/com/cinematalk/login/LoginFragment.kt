package com.app.cinematalk.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.app.cinematalk.R

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(
            R.layout.fragment_login, container, false
        )

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Clear login result when the view is destroyed
        loginViewModel.clearLoginResult()
    }

}