package com.affirm.takehome.di

import android.content.Context
import com.affirm.takehome.ui.MainActivity
import com.affirm.takehome.viewmodel.RestaurantViewModel
import dagger.BindsInstance
import dagger.Component

// Definition of a Dagger component
@Component
interface AppComponent {

    @Component.Factory
    interface Factory {
        // With @BindsInstance, the Context passed in will be available in the graph
        fun create(@BindsInstance context: Context): AppComponent
    }

    // Classes that can be injected by this Component
    fun inject(activity: MainActivity)

    fun inject(viewModel: RestaurantViewModel)
}