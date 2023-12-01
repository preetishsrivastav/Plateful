package com.example.plateful.ui.activities

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.plateful.R
import com.example.plateful.databinding.ActivityMainBinding
import com.example.plateful.model.notification.NotifyWorker
import com.example.plateful.utils.Constants
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var mNavController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mNavController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dishes, R.id.navigation_favourites, R.id.navigation_random
            )
        )
        setupActionBarWithNavController(mNavController, appBarConfiguration)
        binding.navView.setupWithNavController(mNavController)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Dexter.withContext(this)
                .withPermission(Manifest.permission.POST_NOTIFICATIONS)
                .withListener(object : PermissionListener {

                    override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                        startWork()
                    }

                    override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                        Toast.makeText(
                            this@MainActivity,
                            "You have denied the storage permission to select image.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(
                        p0: PermissionRequest?,
                        p1: PermissionToken?
                    ) {
                          showRationalDialogForPermissions()
                    }
                }).onSameThread()
                .check()
        }else {
            startWork()
        }
        if (intent.hasExtra(Constants.NOTIFICATION_ID)) {
            val notificationId = intent.getIntExtra(Constants.NOTIFICATION_ID, 0)
            Log.i("Notification Id", "$notificationId")

            // The Random Dish Fragment is selected when user is redirect in the app via Notification.
            binding.navView.selectedItemId = R.id.navigation_random
        }
    }

    //This override function will handle the backstack
    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(mNavController, null)
    }

    //    This function will hide Bottom Navigation Bar
    fun hideBottomNavView() {
        binding.navView.clearAnimation()
        binding.navView.animate().translationY(binding.navView.height.toFloat()).duration = 300
        binding.navView.visibility = View.GONE
    }

    //This function will show bottom Navigation Bar
    fun showBottomNavView() {
        binding.navView.clearAnimation()
        binding.navView.animate().translationY(0f).duration = 300
        binding.navView.visibility = View.VISIBLE
    }

    private fun setConstraints() = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
        .setRequiresCharging(false)
        .setRequiresBatteryNotLow(true)
        .build()

    private fun setWorkRequest() = PeriodicWorkRequestBuilder<NotifyWorker>(
        15,TimeUnit.MINUTES)
        .setConstraints(setConstraints())
        .build()

    private fun startWork(){
        WorkManager.getInstance(this)
            .enqueueUniquePeriodicWork("Plateful Work",
            ExistingPeriodicWorkPolicy.KEEP,
            setWorkRequest())
    }

    private fun showRationalDialogForPermissions() {
        AlertDialog.Builder(this)
            .setMessage("It Looks like you have turned off permissions required for this feature. It can be enabled under Application Settings")
            .setPositiveButton(
                "GO TO SETTINGS"
            ) { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }.show()
    }
}