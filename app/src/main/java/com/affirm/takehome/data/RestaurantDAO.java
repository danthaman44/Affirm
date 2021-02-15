package com.affirm.takehome.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface RestaurantDAO {
    @Query("SELECT * FROM restaurant")
    LiveData<List<Restaurant>> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Restaurant restaurant);

    @Delete
    void delete(Restaurant restaurant);
}