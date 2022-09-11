package com.adyen.android.assignment.ui.venue.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adyen.android.assignment.R
import com.adyen.android.assignment.api.model.Category
import com.adyen.android.assignment.api.model.Result
import com.adyen.android.assignment.databinding.ViewholderVenuesItemBinding

class VenueAdapter :
    ListAdapter<Result, VenueAdapter
    .CategoryViewHolder>(object : DiffUtil.ItemCallback<Result>() {
        override fun areItemsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: Result, newItem: Result): Boolean {
            return oldItem.name == newItem.name
        }
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ViewholderVenuesItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category)
    }

    class CategoryViewHolder(
        private val binding: ViewholderVenuesItemBinding,
        private val context: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(venue: Result) {
            binding.textviewVenueTitle.text = stringOrDefault(venue.name)
            binding.textviewCategoryVenue.text = stringOrDefault(getCategory(venue.categories))
            binding.textviewAddress.text = stringOrDefault(venue.location.formatted_address)
            binding.textviewDistance.text = context
                .getString(R.string.venue_distance_str, venue.distance)
        }

        private fun stringOrDefault(string: String): String {
            return if (string.isEmpty() || string.isBlank()) {
                context.getString(R.string.if_data_not_available_str)
            } else {
                string
            }
        }

        private fun getCategory(categoryList: List<Category>): String {
            return categoryList.joinToString(", ", transform = {
                return@joinToString it.name
            })
        }
    }
}