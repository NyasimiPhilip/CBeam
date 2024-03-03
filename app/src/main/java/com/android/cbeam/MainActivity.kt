package com.android.cbeam

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.cbeam.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        drawerLayout = binding.drawerLayout

        // Find the NavHostFragment
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment

        // Get the NavController from the NavHostFragment
        val navController = navHostFragment.navController

        // Create the navigation drawer configuration with the navController
        appBarConfiguration = AppBarConfiguration(navController.graph, drawerLayout)

        // Set up the ActionBar with NavController and the configuration
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up the navigation view with the NavController
        binding.navView.setupWithNavController(navController)

        // Set up the ActionBarDrawerToggle
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )

        // Set the color of the drawer indicator
        toggle.drawerArrowDrawable.color = ContextCompat.getColor(this, R.color.black)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Set the ActionBar title to empty
        supportActionBar?.title = ""

        // Set action bar color programmatically
        supportActionBar?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.colorPrimary)))

        // Find the menu item by ID
        val beamDesignMenuItem = binding.navView.menu.findItem(R.id.beamDesign)

        // Set an OnClickListener for the menu item
        beamDesignMenuItem.setOnMenuItemClickListener {
            // Navigate to the com.android.cbeam.ui.BeamDesignFragment when the menu item is clicked
            navController.navigate(R.id.beamDesignFragment)
            true // Return true to indicate that the click event has been handled
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Handle toggle button click
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragmentContainerView)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
