package com.app.cinematalk.reviews

import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.app.cinematalk.R
import com.app.cinematalk.common.ReviewBaseFragment
import com.app.cinematalk.model.Model
import com.app.cinematalk.model.Review
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import android.graphics.Color
import android.widget.Button

/**
 * Adapter for displaying review cards in a RecyclerView.
 */
class ReviewCardsAdapter(
    private val reviews: List<Review>,
) : RecyclerView.Adapter<ReviewCardsAdapter.ReviewViewHolder>() {

    private var onReviewItemClickListener: OnReviewItemClickListener? = null
    private val userEmail = FirebaseAuth.getInstance().currentUser?.email as String

    interface OnReviewItemClickListener {
        fun onReviewItemClicked(
            reviewId: String, reviewEmail: String,
            holder: ReviewViewHolder, mode: String
        )
    }

    /**
     * ViewHolder for review cards.
     */
    class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userEmail: TextView = itemView.findViewById(R.id.card_user_email)
        val title: TextView = itemView.findViewById(R.id.card_title)
        val type: TextView = itemView.findViewById(R.id.card_type)
        val description: TextView = itemView.findViewById(R.id.card_description)
        val image: ImageView = itemView.findViewById(R.id.card_image)
        val rating: TextView = itemView.findViewById(R.id.card_rating)
        val settingsButton: ImageView = itemView.findViewById(R.id.settings_button)
        val dropdownMenu: LinearLayout = itemView.findViewById(R.id.dropdown_menu)
        val deleteCardButton: TextView = itemView.findViewById(R.id.delete_option)
        val editCardButton: TextView = itemView.findViewById(R.id.edit_option)
        val submitEditCardButton: Button = itemView.findViewById(R.id.save_edits_button)

    }

    /**
     * Creates a new ViewHolder for a review card.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.review_card_item, parent, false)
        return ReviewViewHolder(view)
    }

    /**
     * Sets the listener for review item clicks.
     */
    fun setOnReviewItemClickListener(listener: ReviewBaseFragment) {
        this.onReviewItemClickListener = listener
    }

    /**
     * Binds data to the ViewHolder for a review card.
     */
    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]

        Model.instance.getReviewPic(review.picture).addOnSuccessListener {
            Glide.with(holder.itemView)
                .load(it)
                .into(holder.image)
        }

        holder.userEmail.text = "${review.userEmail}"
        holder.title.text = review.name
        holder.type.text = "-  ${review.genreType}"
        holder.description.text = review.description
        holder.rating.text = "IMDB Rating: ${review.rating}"


        if (review.userEmail == userEmail) {
            holder.settingsButton.visibility = View.VISIBLE

            holder.settingsButton.setOnClickListener {
                if (holder.dropdownMenu.visibility == View.GONE) {
                    holder.dropdownMenu.visibility = View.VISIBLE
                    handleClickDeleteCard(holder, position)
                    handleClickEditCard(holder, position)
                } else {
                    holder.dropdownMenu.visibility = View.GONE
                }
            }
        }
    }

    /**
     * Handles the click event for deleting a review card.
     */
    private fun handleClickDeleteCard(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.deleteCardButton.setOnClickListener {
            // TODO
        }
    }

    /**
     * Handles the click event for editing a review card.
     */
    private fun handleClickEditCard(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]
        holder.editCardButton.setOnClickListener {
            // TODO
        }
    }

    /**
     * Returns the total number of review cards.
     */
    override fun getItemCount(): Int {
        return reviews.size
    }
}