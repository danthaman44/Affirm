package com.affirm.takehome.domain

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.affirm.takehome.data.Restaurant
import com.affirm.takehome.network.yelp.YelpResponse
import com.affirm.takehome.network.yelp.YelpRestaurantApiFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

private const val TAG = "YelpController"

class YelpController @Inject constructor() {

    private val restaurants: MutableLiveData<List<Restaurant>> by lazy {
        MutableLiveData<List<Restaurant>>()
    }

    private val restaurantApi by lazy {
        YelpRestaurantApiFactory.create()
    }

    private var offset = 0

    fun getRestaurants(): LiveData<List<Restaurant>> {
        return restaurants
    }

    private val apiCallback = object : Callback<YelpResponse> {
        override fun onFailure(call: Call<YelpResponse>?, t:Throwable?) {
            Log.e(TAG, "Problem calling Yelp API {${t?.message}}")
        }

        override fun onResponse(call: Call<YelpResponse>?, response: Response<YelpResponse>?) {
            response?.let { response ->
                if (!response.isSuccessful) {
                    return
                }
                // Should move this to a coroutine for performance
                response.body()?.let { body ->
                    val yelpRestaurants = mutableListOf<Restaurant>()
                    body.restaurants.forEach { yelpRestaurant ->
                        val restaurant = Restaurant(yelpRestaurant.id,
                            yelpRestaurant.name,
                            yelpRestaurant.image,
                            yelpRestaurant.rating)
                        yelpRestaurants.add(restaurant)
                        offset++
                    }
                    restaurants.value = yelpRestaurants
                }
            }
        }
    }

    fun loadRestaurants(location: Location) {
        val call = restaurantApi.getRestaurants(location.latitude, location.longitude, offset)
        call.enqueue(apiCallback)
    }

}