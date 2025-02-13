package com.app.cinematalk

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivity : AppCompatActivity() {

    // Declare variables for navigation and UI elements
    private lateinit var navController: NavController
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var headerView: View
    private lateinit var toolbar: Toolbar
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize UI elements
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        headerView = navigationView.getHeaderView(0)
        toolbar = findViewById(R.id.toolbar)

        // Set up the toolbar
        setSupportActionBar(toolbar)
        setupNavController()
        setupDrawer()
        setupNavigationView()

        // Check if user is logged in and navigate to reviewsFragment if true
        val user: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            navController.navigate(R.id.action_loginFragment_to_reviewsFragment)
        }
    }

    // Set up the navigation controller
    private fun setupNavController() {
        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        navController = navHostFragment.findNavController()
        setupActionBarWithNavController(navController)
    }

    // Set up the drawer layout with a toggle button
    private fun setupDrawer() {
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    // Set up the navigation view and handle toolbar visibility
    private fun setupNavigationView() {
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.reviewsFragment,
                R.id.profileFragment,
                R.id.LogoutFragment,
            ), drawerLayout
        )
        findViewById<Toolbar>(R.id.toolbar)
            .setupWithNavController(navController, appBarConfiguration)

        // Hide toolbar in login and signup pages
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.loginFragment || destination.id == R.id.signupFragment) {
                toolbar.visibility = View.GONE
            } else {
                toolbar.visibility = View.VISIBLE
            }
        }

        navigationView.setupWithNavController(navController)
        handleLogout()
    }

    // Handle navigation up action
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    // Handle back button press to close drawer if open
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    // Set up logout menu item click listener
    private fun handleLogout() {
        navigationView.menu
            .findItem(R.id.LogoutFragment)
            .setOnMenuItemClickListener { _ ->
                logoutDialog()
                true
            }
    }

    // Show logout confirmation dialog
    private fun logoutDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                auth = FirebaseAuth.getInstance()
                auth.signOut()
                navController.navigate(R.id.loginFragment)
                drawerLayout.closeDrawer(GravityCompat.START)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}

