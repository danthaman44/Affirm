package com.affirm.takehome.domain

import android.location.Location
import javax.inject.Inject

class MockLocationProvider @Inject constructor() {

    fun currentLocation(): Location {
        // Coordinates for the Empire State Building
        val location = Location("MockLocation")
        location.longitude = -73.9857
        location.latitude = 40.7484
        return location
    }

}