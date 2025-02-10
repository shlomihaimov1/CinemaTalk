package com.app.cinematalk.model

import android.net.Uri
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class FirebaseModel {

    private val db = Firebase.firestore
    private val storage = FirebaseStorage.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    companion object {
        const val REVIEWS_COLLECTION_PATH = "Reviews"
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
        // TODO
    }

    // Delete a review from Firestore
    fun deleteReview(reviewId: String, callback: () -> Unit) {
        // TODO
    }


}