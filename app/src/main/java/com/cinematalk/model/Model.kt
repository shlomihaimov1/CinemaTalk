package com.app.cinematalk.model

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.cinematalk.dao.AppLocalDatabase
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.Executors

class Model private constructor() {

    enum class LoadingState {
        LOADING,
        LOADED
    }

    private val database = AppLocalDatabase.db
    private var executor = Executors.newSingleThreadExecutor()
    private val firebaseModel = FirebaseModel()
    private val reviews: LiveData<MutableList<Review>>? = null
    private val myReviews: LiveData<MutableList<Review>>? = null
    val reviewsListLoadingState: MutableLiveData<LoadingState> =
        MutableLiveData(LoadingState.LOADED)

    companion object {
        val instance: Model = Model()
    }

    // Get all reviews from the local database or refresh from Firestore
    fun getAllReviews(): LiveData<MutableList<Review>> {
        refreshAllReviews()
        return reviews ?: database.reviewDao().getAll()
    }

    fun refreshAllReviews() {
        reviewsListLoadingState.value = LoadingState.LOADING

        // 1. Get last local update
        val lastUpdated: Long = Review.lastUpdated

        // 2. Get all updated records from firestore since last update locally
        firebaseModel.getAllReviews(lastUpdated) { list ->
            Log.i("TAG", "Firebase returned ${list.size}, lastUpdated: $lastUpdated")

            executor.execute {
                database.reviewDao().deleteAll()

                var time = lastUpdated
                for (review in list) {
                    database.reviewDao().insert(review)

                    review.lastUpdated?.let {
                        if (time < it)
                            time = review.lastUpdated ?: System.currentTimeMillis()
                    }
                }

                // 4. Update local data
                Review.lastUpdated = time
                reviewsListLoadingState.postValue(LoadingState.LOADED)
            }
        }
    }

    // Add a new review and refresh the lists
    fun addReview(review: Review, attachedPictureUri: Uri, callback: () -> Unit) {
        firebaseModel.createNewReview(review, attachedPictureUri) {
            refreshAllReviews()
            refreshMyReviews()
            callback()
        }
    }

    // Delete a review and refresh the lists
    fun deleteReview(reviewId: String, callback: () -> Unit) {
        // TODO
    }

    // Edit a review and refresh the lists
    fun editReview(reviewId: String, updatedTitle: String, updatedDescription: String, callback: () -> Unit) {
        // TODO
    }

    // Get the picture of a review
    fun getReviewPic(picture: String): Task<Uri> {
        return firebaseModel.getReviewPic(picture)
    }
}