package com.adyen.android.assignment.ui.categories.state

import com.adyen.android.assignment.api.model.Category

data class CategoryScreenState(
    val categories: List<Category> = emptyList(),
    val activeCategory: Category? = null
)
