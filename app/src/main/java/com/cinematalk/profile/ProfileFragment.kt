package com.app.cinematalk.profile

import ProfileViewModel
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app.cinematalk.R
import com.app.cinematalk.common.ReviewBaseFragment
import com.app.cinematalk.common.SharedViewModel
import com.app.cinematalk.model.Model
import com.app.cinematalk.reviews.ReviewCardsAdapter
import com.app.cinematalk.reviews.ReviewViewModel
import com.app.cinematalk.singup.UserProperties
import com.app.cinematalk.utils.RequestStatus
import com.app.cinematalk.utils.isString
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout

class ProfileFragment : ReviewBaseFragment(), ReviewCardsAdapter.OnReviewItemClickListener {
    // ViewModel instance
    private val profileViewModel: ProfileViewModel by viewModels()

    // Layouts
    private lateinit var profileMainLayout: LinearLayout
    private lateinit var editProfileLayout: LinearLayout
    private lateinit var myReviewsLayout: LinearLayout

    private lateinit var addNewReviewButton: ImageView
    private lateinit var editProfileButton: TextView
    private lateinit var userProfileString: TextView

    private lateinit var profileImagePlaceholder: ImageView
    private lateinit var changeProfilePictureButton: ImageView
    private lateinit var saveNewDetailsButton: Button
    private lateinit var progressBarProfilePhoto: ProgressBar

    private lateinit var profileImageEdit: ImageView
    private lateinit var profileImagePlaceholderEdit: ImageView
    private lateinit var firstNameInput: EditText
    private lateinit var lastNameInput: EditText
    private lateinit var profileImage: ImageView

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewModel: ReviewViewModel

    override fun getLayoutResourceId(): Int {
        return R.layout.fragment_profile
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View? = super.onCreateView(inflater, container, savedInstanceState)
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        if (view != null) {
            initViews(view)
        }

        // Initialize user name and set up event handlers
        initializeUserName()
        handleChangeProfilePicture()
        handleAddNewReviewClick()
        handleEditProfileClick()
        checkInitializationShareViewModel()
        handleSaveNewProfileInfo()
        observeShowProfilePhoto()
        observeChangeName()
        observeUploadProfileImage()

        // Set up RecyclerView for reviews
        viewModel = ViewModelProvider(this)[ReviewViewModel::class.java]
        viewModel.myReviews = Model.instance.getMyReviews()

        setupRecyclerView()
        checkInitializationShareViewModel()
        observeReviewViewModel()
        observeInitializeUserDataStatus()

        // Log and fetch profile image
        Log.d("ProfileFragment", "sharedViewModel.userMetaData: ${sharedViewModel.userMetaData}")
        profileViewModel.getProfileImage(sharedViewModel.userMetaData)
        return view
    }

    // Initialize views
    private fun initViews(view: View) {
        // Layout
        profileMainLayout = view.findViewById(R.id.profile_main_layout)
        editProfileLayout = view.findViewById(R.id.edit_profile_layout)
        myReviewsLayout = view.findViewById(R.id.my_reviews_layout)

        // Profile
        userProfileString = view.findViewById(R.id.user_profile_string)
        changeProfilePictureButton = view.findViewById(R.id.change_profile_picture_button)
        progressBarProfilePhoto = view.findViewById(R.id.progress_bar_profile_photo)
        profileImage = view.findViewById(R.id.profile_image)
        profileImagePlaceholder = view.findViewById(R.id.profile_image_placeholder)

        // Upload new review
        addNewReviewButton = view.findViewById(R.id.add_new_review_button)
        saveNewDetailsButton = view.findViewById(R.id.save_new_details_button)

        // Edit Profile
        profileImageEdit = view.findViewById(R.id.profile_image_edit)
        profileImagePlaceholderEdit = view.findViewById(R.id.profile_image_placeholder_edit)
        editProfileButton = view.findViewById(R.id.edit_profile)
        firstNameInput = view.findViewById(R.id.first_name_input)
        lastNameInput = view.findViewById(R.id.last_name_input)

        // My Reviews
        recyclerView = view.findViewById(R.id.reviews_recycler_view)
    }

    // Initialize user name
    private fun initializeUserName() {
        "Hello, ${sharedViewModel.userMetaData.firstName} ${sharedViewModel.userMetaData.lastName?: ""}".also { userProfileString.text = it }
    }

    // Handle saving new profile information
    private fun handleSaveNewProfileInfo() {
        saveNewDetailsButton.setOnClickListener {

            // Gather and save the new information
            val newFirstName = firstNameInput.text.toString()
            val newLastName = lastNameInput.text.toString()
            val userProperties = UserProperties(newFirstName, newLastName)

            if (userProperties.firstName.isNotEmpty() && userProperties.lastName.isNotEmpty()) {
                val newUser = sharedViewModel.userMetaData
                newUser.firstName = userProperties.firstName
                newUser.lastName = userProperties.lastName
                profileViewModel.changeUserName(newUser)
            }
            else {
                showDialogResponse("Enter valid first name and last name")
            }

            // Hide edit profile layout and show the rest
            editProfileLayout.visibility = View.INVISIBLE
            profileMainLayout.visibility = View.VISIBLE
            myReviewsLayout.visibility = View.VISIBLE
        }
    }

    // Handle edit profile button click
    private fun handleEditProfileClick() {
        editProfileButton.setOnClickListener {
            // Show edit profile layout and hide the rest
            editProfileLayout.visibility = View.VISIBLE
            profileMainLayout.visibility = View.INVISIBLE
            myReviewsLayout.visibility = View.INVISIBLE

            // Set in hints the current first name and last name
            firstNameInput.setText(sharedViewModel.userMetaData.firstName)
            lastNameInput.setText(sharedViewModel.userMetaData.lastName ?: "")
        }
    }

    // Observe changes to user name
    private fun observeChangeName() {
        profileViewModel.changeNameResult.observe(viewLifecycleOwner) { result: UserProperties? ->
            if (viewLifecycleOwner.lifecycle.currentState == Lifecycle.State.RESUMED) {
                if (result != null) {
                    sharedViewModel.userMetaData.firstName = result.firstName
                    sharedViewModel.userMetaData.lastName = result.lastName
                    initializeUserName()
                    showDialogResponse("Name changed successfully")
                } else {
                    showDialogResponse("Profile info update failed")
                }
            }
        }
    }

    // Observe changes to profile photo
    private fun observeShowProfilePhoto() {
        profileViewModel.showProfilePhoto.observe(viewLifecycleOwner) { result: Uri? ->
            if (result is Uri) {
                Glide.with(this)
                    .load(result)
                    .into(profileImage)
                Glide.with(this)
                    .load(result)
                    .into(profileImageEdit)
                profileImage.visibility = View.VISIBLE
                profileImageEdit.visibility = View.VISIBLE
                profileImagePlaceholder.visibility = View.GONE
                progressBarProfilePhoto.visibility = View.GONE
                profileImagePlaceholderEdit.visibility = View.GONE
            } else {
                profileImagePlaceholder.visibility = View.VISIBLE
                profileImagePlaceholderEdit.visibility = View.VISIBLE
                profileImage.visibility = View.GONE
                profileImageEdit.visibility = View.GONE
                progressBarProfilePhoto.visibility = View.GONE
            }
        }
    }

    // Observe upload profile image result
    private fun observeUploadProfileImage() {
        profileViewModel.uploadProfileImageResult.observe(viewLifecycleOwner) { result: RequestStatus ->
            when (result) {
                RequestStatus.SUCCESS -> {
                    profileImage.visibility = View.VISIBLE
                    progressBarProfilePhoto.visibility = View.GONE
                }

                RequestStatus.IN_PROGRESS -> {
                    profileImage.visibility = View.GONE
                    progressBarProfilePhoto.visibility = View.VISIBLE
                }

                RequestStatus.FAILURE -> {
                    profileImage.visibility = View.GONE
                    profileImagePlaceholder.visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "Upload failed", Toast.LENGTH_SHORT).show()
                }

                else -> {
                }
            }
        }
    }

    // Handle changing profile picture
    private val pickImageContract = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                profileImageEdit.visibility = View.VISIBLE
                profileImagePlaceholderEdit.visibility = View.GONE
                Glide.with(this).load(it).into(profileImageEdit)
                profileViewModel.uploadProfileImage(sharedViewModel.userMetaData, it) // Save the image
            }
    }

    private fun handleChangeProfilePicture() {
        changeProfilePictureButton.setOnClickListener {
            pickImageContract.launch("image/*")
        }
    }

    // Set up RecyclerView for reviews
    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    // Observe review view model
    private fun observeReviewViewModel() {
        observeReviewViewModel(recyclerView, viewModel.myReviews)
    }

    // Observe initialization of user data status
    private fun observeInitializeUserDataStatus() {
        observeInitializeUserDataStatusBase()
    }

    // Handle add new review button click
    private fun handleAddNewReviewClick() {
        addNewReviewButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileFragment_to_newReviewFragment)
        }
    }


}
