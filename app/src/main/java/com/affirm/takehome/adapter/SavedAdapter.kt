package com.affirm.takehome.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.affirm.takehome.R
import com.affirm.takehome.data.Restaurant
import com.affirm.takehome.image.ImageLoader
import kotlinx.android.synthetic.main.restaurant_grid_item_view.view.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.restaurant_item_view.view.*

class SavedAdapter: RecyclerView.Adapter<SavedAdapter.SavedViewHolder>() {

    private var savedList = listOf<Restaurant>()

    fun setSavedRestaurants(restaurants: List<Restaurant>) {
        savedList = restaurants
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.restaurant_grid_item_view, parent, false)
        return SavedViewHolder(view)
    }

    override fun onBindViewHolder(holder: SavedViewHolder, position: Int) {
        holder.bind(savedList[position])
    }

    override fun onViewRecycled(holder: SavedViewHolder) {
        super.onViewRecycled(holder)
        ImageLoader.get().cancelRequest(holder.itemView.imageView)
    }

    override fun getItemCount(): Int {
        return savedList.size
    }

    class SavedViewHolder(view: View): RecyclerView.ViewHolder(view) {
        fun bind(restaurant: Restaurant) {
            itemView.gridNameTextView.text = restaurant.name
            if (restaurant.image.isNotBlank()) {
                ImageLoader.get().load(restaurant.image).into(itemView.gridImageView)
            }
        }
    }
}