package com.affirm.takehome.di

import android.app.Application
import com.affirm.takehome.data.RestaurantDatabase
import com.affirm.takehome.data.RestaurantRepository

open class MyApplication : Application() {

    // Instance of the AppComponent that will be used by all the Activities in the project
    val appComponent: AppComponent by lazy {
        // Creates an instance of AppComponent using its Factory constructor
        // We pass the applicationContext that will be used as Context in the graph
        DaggerAppComponent.factory().create(applicationContext)
    }

    val database by lazy {
        RestaurantDatabase.getInstance(this)
    }

    val repository by lazy {
        RestaurantRepository(database)
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: MyApplication
            private set
    }

}