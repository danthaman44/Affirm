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
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.affirm.takehome.R
import com.affirm.takehome.adapter.RestaurantAdapter
import com.affirm.takehome.adapter.SavedAdapter
import com.affirm.takehome.data.Restaurant
import com.affirm.takehome.di.MyApplication
import com.affirm.takehome.domain.MockLocationProvider
import com.affirm.takehome.domain.YelpController
import com.affirm.takehome.domain.ZomatoController
import com.affirm.takehome.viewmodel.RestaurantViewModel
import com.affirm.takehome.viewmodel.SavedViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_saved.*
import javax.inject.Inject

private const val TAG = "SavedActivity"

class SavedActivity : AppCompatActivity() {

    lateinit var viewModel: SavedViewModel

    private val savedAdapter by lazy {
        SavedAdapter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_saved)
        viewModel = ViewModelProvider(this).get(SavedViewModel::class.java)
        viewModel.getSavedRestaurants().observe(this, Observer<List<Restaurant>>{ restaurants ->
            savedAdapter.setSavedRestaurants(restaurants)
        })

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        recyclerView.setHasFixedSize(true)
        recyclerView.adapter = savedAdapter;
        recyclerView.layoutManager = GridLayoutManager(this, 2);
        viewModel.getSavedRestaurants().value?.let { restaurants ->
            savedAdapter.setSavedRestaurants(restaurants)
        }

        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL))
        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
            override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                showRating(0)
            }

            override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                return true;
            }

            override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
            }
        })
    }

    private fun showRating(drawable: Int) {
        rating.alpha = 0.0f
        rating.animate()
            .alpha(1f)
            .setDuration(1000)
            .scaleX(2f)
            .scaleY(2f)
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    rating.visibility = View.GONE
                }
            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.saved_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.back_button -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

}