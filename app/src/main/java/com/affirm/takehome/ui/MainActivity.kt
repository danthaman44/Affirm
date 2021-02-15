package com.affirm.takehome.ui

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.affirm.takehome.R
import com.affirm.takehome.adapter.RestaurantAdapter
import com.affirm.takehome.data.Restaurant
import com.affirm.takehome.di.MyApplication
import com.affirm.takehome.domain.MockLocationProvider
import com.affirm.takehome.domain.YelpController
import com.affirm.takehome.domain.ZomatoController
import com.affirm.takehome.viewmodel.RestaurantViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

private const val LOCATION_PERMISSION_CODE = 101
private const val THUMB_UP = R.drawable.thumb_up
private const val THUMB_DOWN = R.drawable.thumb_down
private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private var animating = false

    private val restaurantAdapter by lazy {
        RestaurantAdapter()
    }

    // Mock the location with dependency injection since the Location API isn't working on my emulator
    @Inject
    lateinit var locationProvider: MockLocationProvider;

    @Inject
    lateinit var yelpController: YelpController;

    @Inject
    lateinit var zomatoController: ZomatoController;

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    lateinit var viewModel: RestaurantViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        (application as MyApplication).appComponent.inject(this)

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        setupViewPager()
        setupButtons()

        // Use LiveData for a more reactive architecture
        viewModel = ViewModelProvider(this).get(RestaurantViewModel::class.java)
        viewModel.getRestaurants().observe(this, Observer<List<Restaurant>>{ restaurants ->
            restaurantAdapter.addRestaurants(restaurants)
            if (viewModel.isCurrentRestaurantSaved()) {
                showIcon(THUMB_UP)
            }
        })

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkAndRequestPermissionsForLocation()
    }

    private fun setupViewPager() {
        viewPager.adapter = restaurantAdapter
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                viewModel.index = position
                if (viewModel.isCurrentRestaurantSaved()) {
                    showIcon(THUMB_UP)
                    handleSavedItem()
                } else {
                    hideIcon(THUMB_UP)
                    handleNonsavedItem()
                }
                super.onPageSelected(position)
            }
        })
    }

    private fun setupButtons() {
        saveButton.setOnClickListener {
            viewModel.saveCurrentRestaurant()
            if (!animating) {
                showIcon(THUMB_UP)
                handleSavedItem()
            }
        }

        deleteButton.setOnClickListener {
            viewModel.removeCurrentRestaurant()
            if (!animating) {
                animateIcon(THUMB_DOWN)
                handleNonsavedItem()
            }
        }
    }

    private fun handleSavedItem() {
        saveButton.isEnabled = false
        saveButton.alpha = 0.5f
        deleteButton.isEnabled = true
        deleteButton.alpha = 1.0f
    }

    private fun handleNonsavedItem() {
        saveButton.isEnabled = true
        saveButton.alpha = 1.0f
        deleteButton.isEnabled = false
        deleteButton.alpha = 0.5f
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.saved_button -> {
                val intent = Intent(this, SavedActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showIcon(drawable: Int) {
        animating = true
        icon.setImageDrawable(ContextCompat.getDrawable(this, drawable))
        icon.alpha = 0.0f
        icon.animate()
            .alpha(1f)
            .setDuration(300)
            .scaleX(2f)
            .scaleY(2f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    icon.visibility = View.VISIBLE
                    animating = false
                }
            })
    }

    private fun hideIcon(drawable: Int) {
        animating = true
        icon.setImageDrawable(ContextCompat.getDrawable(this, drawable))
        icon.alpha = 1.0f
        icon.animate()
                .alpha(0.0f)
                .setDuration(300)
                .scaleX(2f)
                .scaleY(2f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        icon.visibility = View.GONE
                        animating = false
                    }
                })
    }

    private fun animateIcon(drawable: Int) {
        animating = true
        icon.setImageDrawable(ContextCompat.getDrawable(this, drawable))
        icon.alpha = 1.0f
        icon.visibility = View.VISIBLE
        icon.animate()
                .alpha(0.0f)
                .setDuration(1000)
                .scaleX(2f)
                .scaleY(2f)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        icon.visibility = View.GONE
                        animating = false
                    }
                })
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED)
            ) {
                loadLocation()
            } else {
                Toast.makeText(this, getString(R.string.no_permission), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkAndRequestPermissionsForLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_CODE
            )
        } else {
            loadLocation()
        }
    }

    private fun loadLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                location?.let { nonNullLocation: Location ->
                    viewModel.setLocation(nonNullLocation)
                    viewModel.loadRestaurants()
                } ?: run {
                    viewModel.setLocation(locationProvider.currentLocation())
                    viewModel.loadRestaurants()
                }
            }
    }

}