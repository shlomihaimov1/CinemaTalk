package com.app.cinematalk.reviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.cinematalk.R
import com.app.cinematalk.common.ReviewBaseFragment
import com.app.cinematalk.model.Model
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Fragment for displaying a list of reviews.
 */
class ReviewsFragment : ReviewBaseFragment(), ReviewCardsAdapter.OnReviewItemClickListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var addNewReviewButton: FloatingActionButton
    private lateinit var viewModel: ReviewViewModel

    /**
     * Returns the layout resource ID for this fragment.
     */
    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_reviews
    }

    /**
     * Initializes the fragment's view.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View? = super.onCreateView(inflater, container, savedInstanceState)
        if (view != null) {
            initViews(view)
        }

        viewModel = ViewModelProvider(this)[ReviewViewModel::class.java]
        viewModel.reviews = Model.instance.getAllReviews()

        setupRecyclerView()
        checkInitializationShareViewModel()
        handleAddNewClick()

        observeReviewViewModel()
        observeInitializeUserDataStatus()

        return view
    }

    /**
     * Initializes the views in the fragment.
     */
    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.reviews_recycler_view)
        addNewReviewButton = view.findViewById(R.id.add_new_review_button)
    }

    /**
     * Sets up the RecyclerView with a LinearLayoutManager.
     */
    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    /**
     * Observes changes in the review data.
     */
    private fun observeReviewViewModel() {
        observeReviewViewModel(recyclerView, viewModel.reviews)
    }

    /**
     * Handles the click event for adding a new review.
     */
    private fun handleAddNewClick() {
        addNewReviewButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_newReviewFragment)
        }
    }

    /**
     * Observes the status of user data initialization.
     */
    private fun observeInitializeUserDataStatus() {
        observeInitializeUserDataStatusBase()
    }
}
