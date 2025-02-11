package com.app.cinematalk.model

import android.net.Uri
import android.util.Log
import com.app.cinematalk.utils.getMovieRank
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.runBlocking

class FirebaseModel {

    private val db = Firebase.firestore
    private val storage = FirebaseStorage.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    companion object {
        const val REVIEWS_COLLECTION_PATH = "Reviews"
    }

    // Get all reviews from Firestore since a specific timestamp
    fun getAllReviews(since: Long, callback: (List<Review>) -> Unit) {
        db.collection(REVIEWS_COLLECTION_PATH)
            // .whereGreaterThanOrEqualTo(Review.LAST_UPDATED, Timestamp(since, 0))
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val reviews: MutableList<Review> = mutableListOf()

                        for (json in it.result) {
                            val review = Review.fromJSON(json.data)
                            reviews.add(review)
                        }
                        reviews.forEach { d -> d.rating = runBlocking { getMovieRank(d.imdbId) } }

                        callback(reviews)
                    }

                    false -> callback(listOf())
                }
            }

    }

    // Get reviews of the current user from Firestore since a specific timestamp
    fun getMyReviews(since: Long, callback: (List<Review>) -> Unit) {
        val email = currentUser?.email

        db.collection(REVIEWS_COLLECTION_PATH)
            .whereEqualTo("userEmail", email)
            // .whereGreaterThanOrEqualTo(Review.LAST_UPDATED, Timestamp(since, 0))
            .get()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        val reviews: MutableList<Review> = mutableListOf()

                        for (json in it.result) {
                            val review = Review.fromJSON(json.data)
                            reviews.add(review)
                        }
                        reviews.forEach { d -> d.rating = runBlocking { getMovieRank(d.imdbId) } }

                        callback(reviews)
                    }

                    false -> callback(listOf())
                }
            }

    }

    // Create a new review in Firestore and upload the attached picture
    fun createNewReview(review: Review, attachedPictureUri: Uri, callback: () -> Unit) {

        val getCurrentUser = FirebaseAuth.getInstance().currentUser

        val imageRefLocation =
            "productPicture/${getCurrentUser?.uid}/${attachedPictureUri.lastPathSegment}"
        val imageRef: StorageReference = storage.getReference(imageRefLocation)

        imageRef.putFile(attachedPictureUri)
            .addOnSuccessListener {
                if (getCurrentUser != null) {
                    getCurrentUser.email?.let {
                        review.userEmail = it
                        review.picture = imageRef.path
                    }
                }
                db.collection(REVIEWS_COLLECTION_PATH).add(review)
                    .addOnSuccessListener { documentReference ->

                        val reviewId = documentReference.id

                        db.collection(REVIEWS_COLLECTION_PATH)
                            .document(reviewId)
                            .update("id", reviewId)
                            .addOnCompleteListener {
                                callback()
                            }
                    }
            }
    }

    // Update a review in Firestore
    fun updateReview(reviewId: String, updatedTitle: String, updatedDescription: String, callback: () -> Unit) {
        val reviewRef = db.collection(REVIEWS_COLLECTION_PATH).document(reviewId)

        val updatedData = hashMapOf(
            "name" to updatedTitle,
            "description" to updatedDescription
        )

        reviewRef.update(updatedData as Map<String, Any>)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback()
                } else {
                    Log.e("EditReview", "Failed to update review", task.exception)
                }
            }
    }

    // Delete a review from Firestore
    fun deleteReview(reviewId: String, callback: () -> Unit) {
        db.collection(REVIEWS_COLLECTION_PATH).document(reviewId).delete()
            .addOnCompleteListener {
                when (it.isSuccessful) {
                    true -> {
                        callback()
                    }

                    false -> callback()
                }
            }
    }

    // Get the picture URL of a review from Firebase Storage
    fun getReviewPic(picture: String): Task<Uri> {
        return storage.reference.child(picture).downloadUrl
    }
}