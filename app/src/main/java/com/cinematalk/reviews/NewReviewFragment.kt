package com.app.cinematalk.reviews

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.app.cinematalk.R
import com.app.cinematalk.model.GenreType
import com.app.cinematalk.model.Model
import com.app.cinematalk.model.Review
import com.app.cinematalk.utils.getMovieId
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.runBlocking

/**
 * Fragment for creating a new review.
 */
class NewReviewFragment : Fragment() {

    private lateinit var view: View
    private lateinit var title: EditText
    private lateinit var type: Spinner
    private lateinit var description: TextInputEditText
    private lateinit var attachPictureButton: ImageButton
    private lateinit var submitButton: MaterialButton
    private lateinit var progressBar: ProgressBar

    private var attachedPicture: Uri = Uri.EMPTY
    private var selectedProductType: String = GenreType.OTHER.type

    /**
     * Initializes the fragment's view.
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        view = inflater.inflate(
            R.layout.fragment_new_review, container, false
        )

        initViews(view)
        handleSubmitButton()
        handleAttachProductPicture()

        return view
    }

    /**
     * Initializes the views in the fragment.
     */
    private fun initViews(view: View) {
        title = view.findViewById(R.id.movie_name_input)
        type = view.findViewById(R.id.review_type)
        description = view.findViewById(R.id.review_description)
        attachPictureButton = view.findViewById(R.id.review_attach_picture_button)
        submitButton = view.findViewById(R.id.review_submit)
        progressBar = view.findViewById(R.id.progress_bar_create_new_review)


        // initialize spinner options
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            GenreType.entries.map { it.type }
        )

        type.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: View?,
                position: Int,
                id: Long
            ) {
                selectedProductType = parentView.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                selectedProductType = GenreType.OTHER.type
            }
        }
        type.adapter = adapter
    }

    /**
     * Handles the click event for the submit button.
     */
    private fun handleSubmitButton() {
        submitButton.setOnClickListener {
            createNewReview()
        }
    }

    /**
     * Shows a dialog with a response message.
     */
    private fun showDialogResponse(message: String) {
        val rootView: View = requireView()
        val snackBar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT)
        val snackBarView: View = snackBar.view
        snackBarView.setBackgroundColor(resources.getColor(R.color.black))
        val textView: TextView =
            snackBarView.findViewById(com.google.android.material.R.id.snackbar_text)
        textView.setTextColor(resources.getColor(R.color.white)) // Set your desired text color
        snackBar.show()
    }

    /**
     * Creates a new review.
     */
    private fun createNewReview() {
        val titleValue = title.text.toString()
        val descriptionValue = description.text.toString()

        if (titleValue.isEmpty()) {
            showDialogResponse("Please enter a title")
            return
        }
        if (descriptionValue.isEmpty()) {
            showDialogResponse("Please enter a description")
            return
        }
        if (attachedPicture == Uri.EMPTY) {
            showDialogResponse("Please pick a picture")
            return
        }

        val imdbId = runBlocking {
            getMovieId(title.text.toString())
        }

        val newReview = Review(
            title.text.toString(),
            GenreType.fromString(selectedProductType),
            description.text.toString(),
            imdbId
        )

        Model.instance.addReview(newReview, attachedPicture) {
            findNavController().popBackStack()
        }
    }

    /**
     * Handles the result of picking an image.
     */
    private val pickImageContract =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                attachedPicture = it
            }
        }

    /**
     * Handles the click event for attaching a product picture.
     */
    private fun handleAttachProductPicture() {
        attachPictureButton.setOnClickListener {
            pickImageContract.launch("image/*")
        }
    }

}
