package com.app.cinematalk.reviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.app.cinematalk.R
import com.app.cinematalk.common.ReviewBaseFragment

/**
 * Fragment for displaying a list of reviews.
 */
class ReviewsFragment : ReviewBaseFragment(), ReviewCardsAdapter.OnReviewItemClickListener {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View? = super.onCreateView(inflater, container, savedInstanceState)
        if (view != null) {
            initViews(view)
        }

        return view
    }


}
