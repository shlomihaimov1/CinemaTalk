package com.app.cinematalk.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

// Function to manage view visibility
fun manageViews(vararg views: View, mode: String) {
    for (view in views) {
        if (mode == "GONE") {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
        }
    }
}

// Function to close the keyboard
fun closeKeyboard(context: Context, view: View) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}