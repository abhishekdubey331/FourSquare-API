package com.adyen.android.assignment.utils

import android.content.Context
import com.adyen.android.assignment.R

interface StringUtils {
    fun noNetworkErrorMessage(): String
    fun somethingWentWrong(): String
    fun loading(): String
}

class StringUtilsImpl(private val appContext: Context) : StringUtils {
    override fun noNetworkErrorMessage() =
        appContext.getString(R.string.message_no_network_connected_str)

    override fun somethingWentWrong() =
        appContext.getString(R.string.message_something_went_wrong_str)

    override fun loading() =
        appContext.getString(R.string.loading_str)
}
