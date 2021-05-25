package com.example.habittracker

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.example.data.remote.RequestManager
import com.example.domain.repository.HabitRepository
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration

    @Inject
    lateinit var repository: HabitRepository

    @Inject
    lateinit var requestManager: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (application as HabitTrackerApplication).appComponent.inject(this)

        setContentView(R.layout.activity_main)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener(this)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_home, R.id.nav_info), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        val header = navView.inflateHeaderView(R.layout.nav_header_main)
        val avatarView: ImageView = header.findViewById(R.id.headerImageView)
        Glide.with(this)
            .load("https://data.whicdn.com/images/327972713/original.jpg")
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.error)
            .transform(CircleCrop())
            .into(avatarView)

        lifecycleScope.launch {
            Log.d("TAG-ARCHITECTURE", repository.toString())
            repository.refresh()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val navController = findNavController(R.id.nav_host_fragment)
        navController.removeOnDestinationChangedListener(this)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        currentFocus?.hideKeyboard()
    }

    fun View.hideKeyboard() {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    // TODO создать сервис для сохранения данных. Потому что сейчас нет гарантий,
    //  что данные сохранятся
    override fun onPause() {
        lifecycleScope.launch {
            requestManager.saveState()
        }

        super.onPause()
    }
}