package com.adyen.android.assignment.api.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Category(
    val icon: Icon,
    val id: String,
    val name: String,
) : Parcelable {
    fun getCategoryUrl(): String {
        return this.icon.prefix.plus("88").plus(this.icon.suffix)
    }
}
