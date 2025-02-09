package com.app.cinematalk.utils

import android.util.Patterns
import android.widget.EditText
import com.app.cinematalk.login.UserCredentials

// Function to check user credentials
fun checkCredentials(
    credentials: UserCredentials, emailInput: EditText,
    passwordInput: EditText
): Boolean {
    if (credentials.email.isEmpty()) {
        emailInput.error = "Enter a email"
    } else if (credentials.password.isEmpty()) {
        passwordInput.error = "Enter a password"
    } else if (credentials.password.length <= 6) {
        passwordInput.error = "Password need to more than 6 characters long"
    } else if (!Patterns.EMAIL_ADDRESS.matcher(credentials.email).matches()) {
        emailInput.error = "Enter valid email format"
    } else {
        return true
    }
    return false
}

// Function to check if a string contains only letters
fun isString(value: String): Boolean {
    return value.all { it.isLetter() }
}