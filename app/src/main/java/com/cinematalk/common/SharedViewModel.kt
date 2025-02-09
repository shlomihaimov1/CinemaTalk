package com.app.cinematalk.common

import androidx.lifecycle.ViewModel
import com.app.cinematalk.profile.UserMetaData

// ViewModel to share user metadata across different fragments
class SharedViewModel : ViewModel() {
    // Holds user metadata information
    var userMetaData: UserMetaData = UserMetaData(
        firstName = "", lastName = "",
        email = "", profilePhoto = ""
    )
}