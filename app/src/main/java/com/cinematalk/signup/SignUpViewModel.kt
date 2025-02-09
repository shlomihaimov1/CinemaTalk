package com.app.cinematalk.singup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.app.cinematalk.login.UserCredentials
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore

class SignUpViewModel : ViewModel() {
    // LiveData to hold the result of the sign-up process
    private val _signUpResult = MutableLiveData<String>()
    val signUpResult: LiveData<String> get() = _signUpResult
    private lateinit var auth: FirebaseAuth
    private val db = FirebaseFirestore.getInstance()

    // Function to sign up a user with given credentials and properties
    fun signUpUser(credentials: UserCredentials, userProperties: UserProperties) {
        auth = Firebase.auth
        auth.createUserWithEmailAndPassword(credentials.email, credentials.password)
            .addOnSuccessListener {
                Log.d("SignUp", "User created successfully with email: ${credentials.email}")
                val user = returnUserAsJson(userProperties)
                Log.w("APP", "Userz created: $user")

                // Save user data to Firestore
                db.collection("Users").document(credentials.email)
                    .set(user)
                    .addOnSuccessListener {
                        Log.w("APP", "User created")
                        _signUpResult.value = "Success"
                    }
                    .addOnFailureListener { e ->
                        Log.e("SignUp", "Failed to upload user data: ${e.message}")
                        _signUpResult.value = "Cannot upload profile image"
                    }
            }
            .addOnFailureListener {
                Log.v("APP", "The email is already in use")
                _signUpResult.value = "The email is already in use"
            }
    }

    // Function to clear the sign-up result
    fun clearSignUpResult() {
        _signUpResult.value = ""
    }

    // Function to convert user properties to a JSON-like map
    private fun returnUserAsJson(userProperties: UserProperties)
            : MutableMap<String, Any> {
        val user: MutableMap<String, Any> = HashMap()
        user["firstName"] = userProperties.firstName.replaceFirstChar(Char::titlecase)
        user["lastName"] = userProperties.lastName.replaceFirstChar(Char::titlecase)
        user["profilePhoto"] = ""
        return user
    }
}
