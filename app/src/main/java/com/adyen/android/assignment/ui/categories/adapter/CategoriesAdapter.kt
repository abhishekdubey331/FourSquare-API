package com.adyen.android.assignment.ui.categories.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.adyen.android.assignment.api.model.Category
import com.adyen.android.assignment.databinding.ViewholderCategoryItemBinding

class CategoriesAdapter(
    private val selectedCategoryId: String?,
    private val categorySelect: (category: Category) -> Unit
) :
    ListAdapter<Category, CategoriesAdapter
    .CategoryViewHolder>(object : DiffUtil.ItemCallback<Category>() {
        override fun areItemsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Category, newItem: Category): Boolean {
            return oldItem.id == newItem.id
        }
    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ViewholderCategoryItemBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryViewHolder(binding, selectedCategoryId, categorySelect)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = getItem(position)
        holder.bind(category)
    }

    class CategoryViewHolder(
        private val binding: ViewholderCategoryItemBinding,
        private val selectedCategoryId: String?,
        private val categorySelect: (categoryId: Category) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(category: Category) {
            if (selectedCategoryId == category.id) {
                binding.categoryText.setBackgroundColor(Color.LTGRAY)
            }
            binding.root.setOnClickListener {
                categorySelect(category)
            }
            binding.categoryText.text = category.name
        }
    }
}