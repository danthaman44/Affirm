package com.affirm.takehome.domain

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.affirm.takehome.data.Restaurant
import com.affirm.takehome.network.zomato.ZomatoResponse
import com.affirm.takehome.network.zomato.ZomatoRestaurantApiFactory
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

private const val TAG = "ZomatoController"

class ZomatoController @Inject constructor() {

    private val restaurants: MutableLiveData<List<Restaurant>> by lazy {
        MutableLiveData<List<Restaurant>>()
    }

    private val restaurantApi by lazy {
        ZomatoRestaurantApiFactory.create()
    }

    private var offset = 0

    fun getRestaurants(): LiveData<List<Restaurant>> {
        return restaurants
    }

    private val apiCallback = object : Callback<ZomatoResponse> {
        override fun onFailure(call: Call<ZomatoResponse>?, t:Throwable?) {
            Log.e(TAG, "Problem calling Zomato API {${t?.message}}")
        }

        override fun onResponse(call: Call<ZomatoResponse>?, response: Response<ZomatoResponse>?) {
            response?.let { response ->
                if (!response.isSuccessful) {
                    return
                }
                // Should move this to a coroutine for performance
                response.body()?.let { body ->
                    val zomatoRestaurants = mutableListOf<Restaurant>()
                    body.restaurants.forEach { zomatoRestaurant ->
                        val restaurant = Restaurant(zomatoRestaurant.restaurantDetail.id,
                            zomatoRestaurant.restaurantDetail.name,
                            zomatoRestaurant.restaurantDetail.image,
                            zomatoRestaurant.restaurantDetail.userRating.rating)
                        zomatoRestaurants.add(restaurant)
                        offset++
                    }
                    restaurants.value = zomatoRestaurants
                }
            }
        }
    }

    fun loadRestaurants(location: Location) {
        val call = restaurantApi.getRestaurants(location.latitude, location.longitude, offset)
        call.enqueue(apiCallback)
    }

}