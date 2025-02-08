package com.app.cinematalk.profile

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.app.cinematalk.R


class ProfileFragment : ReviewBaseFragment(), ReviewCardsAdapter.OnReviewItemClickListener {


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

    // Initialize views
    private fun initViews(view: View) {
    }


}
