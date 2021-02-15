package com.affirm.takehome.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.affirm.takehome.data.Restaurant
import com.affirm.takehome.di.MyApplication

class SavedViewModel constructor() : ViewModel() {

    private val repository by lazy {
        MyApplication.instance.repository
    }

    fun getSavedRestaurants(): LiveData<List<Restaurant>> {
        return repository.savedRestaurants
    }

}