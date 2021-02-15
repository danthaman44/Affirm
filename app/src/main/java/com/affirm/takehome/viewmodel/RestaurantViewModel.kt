package com.affirm.takehome.viewmodel

import android.location.Location
import androidx.lifecycle.*
import com.affirm.takehome.data.Restaurant
import com.affirm.takehome.di.MyApplication
import com.affirm.takehome.domain.YelpController
import com.affirm.takehome.domain.ZomatoController
import kotlin.properties.Delegates

enum class RestaurantEnum {
    YELP,
    ZOMATO
}

private const val TAG = "RestaurantViewModel"

class RestaurantViewModel constructor() : ViewModel() {

    companion object Constants {
        const val OFFSET_BEFORE_FETCH = 5
    }

    private val yelpController by lazy {
        YelpController()
    }

    private val zomatoController by lazy {
        ZomatoController()
    }

    private val repository by lazy {
        MyApplication.instance.repository
    }

    private val restaurants: MutableLiveData<List<Restaurant>> by lazy {
        MutableLiveData<List<Restaurant>>()
    }

    private var currentLocation: Location? = null

    private var currentRestaurantType = RestaurantEnum.YELP

    var index: Int by Delegates.observable(0) { _, _, newValue ->
        // Fetch more restaurants when we are close the end of the current list
        if (newValue == restaurants.value?.size?.minus(OFFSET_BEFORE_FETCH)) {
            loadRestaurants()
        }
    }

    private val yelpObserver = Observer<List<Restaurant>> { yelpRestaurants ->
        val newRestaurants = mutableListOf<Restaurant>()
        restaurants.value?.let { restaurantList ->
            newRestaurants.addAll(restaurantList)
        }
        yelpRestaurants?.let { restaurantList ->
            newRestaurants.addAll(restaurantList)
        }
        restaurants.value = newRestaurants
    }

    private val zomatoObserver = Observer<List<Restaurant>> { zomatoRestaurants ->
        val newRestaurants = mutableListOf<Restaurant>()
        restaurants.value?.let { restaurantList ->
            newRestaurants.addAll(restaurantList)
        }
        zomatoRestaurants?.let { restaurantList ->
            newRestaurants.addAll(restaurantList)
        }
        restaurants.value = newRestaurants
    }

    init {
        yelpController.getRestaurants().observeForever(yelpObserver)
        zomatoController.getRestaurants().observeForever(zomatoObserver)
    }

    fun setLocation(location: Location) {
        currentLocation = location
    }

    fun getRestaurants(): LiveData<List<Restaurant>> {
        return restaurants
    }

    fun getCurrentRestaurant(): Restaurant? {
        return restaurants.value?.get(index)
    }

    fun isCurrentRestaurantSaved(): Boolean {
        getCurrentRestaurant()?.let { currentRestaurant ->
            getSavedRestaurants().value?.forEach() { savedRestaurant ->
                if (currentRestaurant.id == savedRestaurant.id) {
                    return true
                }
            }
            return false
        }
        return false
    }

    fun getSavedRestaurants(): LiveData<List<Restaurant>> {
        return repository.savedRestaurants
    }

    fun loadRestaurants() {
        currentLocation?.let { location ->
            when(currentRestaurantType) {
                RestaurantEnum.YELP -> {
                    yelpController.loadRestaurants(location)
                    currentRestaurantType = RestaurantEnum.ZOMATO
                }
                RestaurantEnum.ZOMATO -> {
                    zomatoController.loadRestaurants(location)
                    currentRestaurantType = RestaurantEnum.YELP
                }
            }
        }
    }

    fun saveCurrentRestaurant() {
        restaurants.value?.let { restaurantList ->
            val current = restaurantList[index]
            repository.insertRestaurant(current)
        }
    }

    fun removeCurrentRestaurant() {
        restaurants.value?.let { restaurantList ->
            val current = restaurantList[index]
            repository.deleteRestaurant(current)
        }
    }

    override fun onCleared() {
        // Unregister observers to avoid memory leaks
        yelpController.getRestaurants().removeObserver(yelpObserver)
        zomatoController.getRestaurants().removeObserver(zomatoObserver)
        super.onCleared()
    }

}