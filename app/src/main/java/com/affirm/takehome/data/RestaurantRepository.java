package com.affirm.takehome.data;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class RestaurantRepository {

    private RestaurantDatabase mDatabase;

    private LiveData<List<Restaurant>> mSavedRestaurants;

    public RestaurantRepository(RestaurantDatabase database) {
        mDatabase = database;
        mSavedRestaurants = mDatabase.restaurantDAO().getAll();
    }

    public LiveData<List<Restaurant>> getSavedRestaurants() {
        return mSavedRestaurants;
    }

    public void insertRestaurant(Restaurant restaurant) {
       new InsertRestaurantAsyncTask(mDatabase.restaurantDAO()).execute(restaurant);
    }

    public void deleteRestaurant(Restaurant restaurant) {
        new DeleteRestaurantAsyncTask(mDatabase.restaurantDAO()).execute(restaurant);
    }

    private static class InsertRestaurantAsyncTask extends AsyncTask<Restaurant, Void, Void> {

        private RestaurantDAO mDao;

        InsertRestaurantAsyncTask(RestaurantDAO dao) {
            mDao = dao;
        }

        @Override
        protected Void doInBackground(final Restaurant... params) {
            mDao.insert((Restaurant) params[0]);
            return null;
        }
    }

    private static class DeleteRestaurantAsyncTask extends AsyncTask<Restaurant, Void, Void> {

        private RestaurantDAO mDao;

        DeleteRestaurantAsyncTask(RestaurantDAO dao) {
            mDao = dao;
        }

        @Override
        protected Void doInBackground(final Restaurant... params) {
            mDao.delete((Restaurant) params[0]);
            return null;
        }
    }
}
