package com.app.cinematalk.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.cinematalk.model.Review

@Dao
interface ReviewDao {

    /**
     * Retrieves all reviews created by a specific user.
     * @param email The email of the user whose reviews are to be fetched.
     * @return LiveData containing a list of the user's reviews.
     */
    @Query("SELECT * FROM Review WHERE userEmail = :email")
    fun getMy(email: String): LiveData<MutableList<Review>>

    /**
     * Retrieves all reviews from the database.
     * @return LiveData containing a list of all reviews.
     */
    @Query("SELECT * FROM Review")
    fun getAll(): LiveData<MutableList<Review>>

    /**
     * Retrieves a single review by its unique ID.
     * @param id The ID of the review to fetch.
     * @return LiveData containing the requested review.
     */
    @Query("SELECT * FROM Review WHERE id = :id")
    fun getReviewById(id: String): LiveData<Review>

    /**
     * Inserts one or more reviews into the database.
     * If a review with the same ID exists, it will be replaced.
     * @param reviews The review(s) to insert.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg reviews: Review)

    /**
     * Deletes a review from the database by its ID.
     * @param reviewId The ID of the review to delete.
     */
    @Query("DELETE FROM Review WHERE id = :reviewId")
    fun delete(reviewId: String)

    /**
     * Deletes all reviews from the database.
     */
    @Query("DELETE FROM Review")
    fun deleteAll()
}