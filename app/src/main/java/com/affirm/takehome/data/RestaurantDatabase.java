package com.affirm.takehome.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Restaurant.class}, version = 1)
public abstract class RestaurantDatabase extends RoomDatabase {

    private static RestaurantDatabase INSTANCE;

    public static RestaurantDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (RestaurantDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, RestaurantDatabase.class, "restaurant-db").build();
                }
            }
        }
        return INSTANCE;
    }

    public abstract RestaurantDAO restaurantDAO();
}
