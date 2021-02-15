package com.affirm.takehome.adapter

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.affirm.takehome.R
import com.affirm.takehome.data.Restaurant
import com.affirm.takehome.image.ImageLoader
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.restaurant_item_view.view.*

class RestaurantAdapter: RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    private val restaurantList = mutableListOf<Restaurant>()

    fun addRestaurants(restaurants: List<Restaurant>) {
        val oldPosition = restaurants.size
        restaurantList.addAll(restaurants)

        notifyItemRangeChanged(oldPosition, restaurants.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.restaurant_item_view, parent, false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        holder.bind(restaurantList[position])
    }

    override fun onViewRecycled(holder: RestaurantViewHolder) {
        super.onViewRecycled(holder)
        ImageLoader.get().cancelRequest(holder.itemView.imageView)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    class RestaurantViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(restaurant: Restaurant) {
            itemView.restaurantNameTextView.text = restaurant.name
            itemView.restaurantRatingTextView.text = restaurant.rating
            if (restaurant.image.isNotBlank()) {
                ImageLoader.get().load(restaurant.image).into(itemView.imageView)
            }
        }
    }
}