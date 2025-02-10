package com.app.cinematalk.reviews

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.app.cinematalk.model.Review

/**
 * ViewModel for managing review data.
 */
class ReviewViewModel : ViewModel() {
    /**
     * LiveData holding a list of all reviews.
     */
    var reviews: LiveData<MutableList<Review>>? = null

    /**
     * LiveData holding a list of reviews created by the current user.
     */
    var myReviews: LiveData<MutableList<Review>>? = null
}
