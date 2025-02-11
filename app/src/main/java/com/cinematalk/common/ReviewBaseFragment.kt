package com.app.cinematalk.common

import ProfileViewModel
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.app.cinematalk.R
import com.app.cinematalk.model.Model
import com.app.cinematalk.model.Review
import com.app.cinematalk.profile.UserMetaData
import com.app.cinematalk.reviews.ReviewCardsAdapter
import com.app.cinematalk.utils.closeKeyboard
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar

abstract class ReviewBaseFragment : Fragment(), ReviewCardsAdapter.OnReviewItemClickListener {
    // Shared ViewModel to access user metadata
    lateinit var sharedViewModel: SharedViewModel
    private val profileViewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(getLayoutResourceId(), container, false)
        // Initialize the shared ViewModel
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        return view
    }

    // Abstract method to get the layout resource ID
    abstract fun getLayoutResourceId(): Int

    // Observe changes in the review ViewModel and update the RecyclerView
    fun observeReviewViewModel(
        recyclerView: RecyclerView,
        reviews: LiveData<MutableList<Review>>?
    ) {
        reviews?.observe(viewLifecycleOwner) { currReviews: List<Review> ->
            val reviewCardsAdapter = ReviewCardsAdapter(currReviews)
            reviewCardsAdapter.setOnReviewItemClickListener(this)
            recyclerView.adapter = reviewCardsAdapter
            closeKeyboard(requireContext(), requireView())
        }
    }

    // Observe the initialization status of user data and update the shared ViewModel
    fun observeInitializeUserDataStatusBase() {
        profileViewModel.initializeUserDataStatus.observe(viewLifecycleOwner) { result: UserMetaData? ->
            if (result!!.email != "") {
                sharedViewModel.userMetaData = result
            } else {
                findNavController().navigate(R.id.loginFragment)
            }
        }
    }

    // Show a dialog response with a message
    open fun showDialogResponse(message: String) {
        val rootView: View = requireView()
        val snackBar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT)
        val snackBarView: View = snackBar.view
        snackBarView.setBackgroundColor(resources.getColor(R.color.black))
        val textView: TextView =
            snackBarView.findViewById(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(resources.getColor(R.color.white))
        snackBar.show()
    }

    // Handle review item click events
    override fun onReviewItemClicked(
        reviewId: String, reviewEmail: String,
        holder: ReviewCardsAdapter.ReviewViewHolder, mode: String
    ) {
        if(mode == "EditItem") {
            val updatedTitle = holder.title.text.toString()
            val updatedDescription = holder.description.text.toString()

            editCardHandler(reviewId, updatedTitle, updatedDescription)
        }
        if (mode == "DeleteItem") {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Delete Review")
                .setMessage("Are you sure you want to delete this review?")
                .setPositiveButton("Delete") { _, _ ->
                    deleteCardHandler(reviewId)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }
    }

    // Handle review deletion
    private fun deleteCardHandler(reviewId: String) {
        Model.instance.deleteReview(reviewId) {}
    }

    // Handle review editing
    private fun editCardHandler(reviewId: String, updatedTitle: String, updatedDescription: String) {
        Model.instance.editReview(reviewId, updatedTitle, updatedDescription) {
            findNavController().popBackStack()
        }
    }

    // Check if the shared ViewModel is initialized, if not, fetch user metadata
    fun checkInitializationShareViewModel() {
        if (sharedViewModel.userMetaData.email == "") {
            profileViewModel.getUserMetaData()
        }
    }
}