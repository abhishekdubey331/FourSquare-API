package com.adyen.android.assignment.ui.main

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.adyen.android.assignment.R
import com.adyen.android.assignment.databinding.ActivityParentBinding
import com.adyen.android.assignment.databinding.RetryLayoutBinding
import com.adyen.android.assignment.extensions.gone
import com.adyen.android.assignment.extensions.hasLocationPermission
import com.adyen.android.assignment.extensions.isGpsEnabled
import com.adyen.android.assignment.extensions.visible
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ParentActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_ISSUE = 0
        private const val GPS_ISSUE = 1
        const val FINE_LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
        const val COARSE_LOCATION_PERMISSION = Manifest.permission.ACCESS_COARSE_LOCATION
    }

    private lateinit var binding: ActivityParentBinding

    private val requestFineLocationPermissionLauncher by lazy {
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[FINE_LOCATION_PERMISSION] == true || permissions[COARSE_LOCATION_PERMISSION] == true) {
                handleLocationStatus()
            } else {
                showRetryView()
                showRationaleDialog(
                    getString(R.string.rationale_permission_title),
                    getString(R.string.rationale_permission_desc),
                    PERMISSION_ISSUE,
                    getString(R.string.provide_permission)
                )
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityParentBinding.inflate(layoutInflater)
        setContentView(binding.root)
        handleLocationStatus()
    }

    private fun handleLocationStatus() {
        when {
            hasLocationPermission() && isGpsEnabled() -> navigateToVenuesScreen()

            hasLocationPermission().not() -> requestLocationPermissions()

            isGpsEnabled().not() -> {
                showRetryView()
                showRationaleDialog(
                    getString(R.string.gps_turned_off_title),
                    getString(R.string.gps_disabled_error_desc),
                    GPS_ISSUE,
                    getString(R.string.enable_gps)
                )
            }
        }
    }

    private fun requestLocationPermissions() {
        requestFineLocationPermissionLauncher.launch(
            arrayOf(
                FINE_LOCATION_PERMISSION,
                COARSE_LOCATION_PERMISSION
            )
        )
    }

    private fun navigateToVenuesScreen() = supportFragmentManager
        .findFragmentById(R.id.home_nav_fragment)?.let {
            if (it !is NavHostFragment) {
                showRetryView()
            } else {
                val inflater = it.navController.navInflater
                val graph = inflater.inflate(R.navigation.nav_graph)
                graph.setStartDestination(R.id.venueFragment)
                it.navController.setGraph(graph, null)
            }
        }.also {
            hideRetryView()
        }

    private fun goToAppSetting() {
        startActivity(
            Intent(
                Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:$packageName")
            ).apply {
                addCategory(Intent.CATEGORY_DEFAULT)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
        )
    }

    private fun showRetryView() {
        val retryLayoutBinding = RetryLayoutBinding.bind(binding.root)
        retryLayoutBinding.retryButton.setOnClickListener {
            handleLocationStatus()
        }
        binding.retryLayout.visible()
    }

    private fun hideRetryView() {
        binding.retryLayout.gone()
    }

    /**
     * Shows rationale dialog for displaying why the app needs permission
     * Only shown if the user has denied the permission request previously
     */
    private fun showRationaleDialog(
        title: String,
        message: String,
        issueType: Int,
        positiveButtonText: String
    ) {
        AlertDialog.Builder(this@ParentActivity).setTitle(title)
            .setMessage(message)
            .setCancelable(false)
            .setPositiveButton(positiveButtonText) { _, _ ->
                when (issueType) {
                    GPS_ISSUE -> {
                        startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS).apply {
                            addCategory(Intent.CATEGORY_DEFAULT)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }
                    PERMISSION_ISSUE -> {
                        goToAppSetting()
                    }
                    else -> Unit
                }
            }.show()
    }
}