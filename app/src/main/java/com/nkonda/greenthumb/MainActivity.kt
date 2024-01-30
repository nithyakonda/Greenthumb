package com.nkonda.greenthumb

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nkonda.greenthumb.databinding.ActivityMainBinding

private const val PERMISSION_REQUEST_CODE = 10
class MainActivity : AppCompatActivity(), ConnectivityChangeListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private var _isInternetAvailable = false
    val isInternetAvailable
        get() = _isInternetAvailable
    private val connectivityManager by lazy {
        getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            onConnectivityChanged(true)
        }

        override fun onLost(network: Network) {
            onConnectivityChanged(false)
        }
    }

    private val connectivityListeners = mutableSetOf<ConnectivityChangeListener>()
    private val requestNotificationPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted->
            if (isGranted) {createChannel()}
            else showPermissionRationaleDialog()
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        connectivityManager.registerDefaultNetworkCallback(networkCallback)
        checkPermissionAndCreateChannel()
        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.navigation_plant_details) {
                navView.visibility = View.GONE
            } else {
                navView.visibility = View.VISIBLE
            }
        }
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
            R.id.navigation_home, R.id.navigation_myplants, R.id.navigation_search))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() ||  super.onSupportNavigateUp()
    }

    override fun onConnectivityChanged(isConnected: Boolean) {
        _isInternetAvailable = isConnected
        for (listener in connectivityListeners) {
            listener.onConnectivityChanged(isConnected)
        }
    }

    fun registerForConnectivityUpdates(listener: ConnectivityChangeListener) {
        connectivityListeners.add(listener)
        listener.onConnectivityChanged(isInternetAvailable)
    }

    fun unregisterFromConnectivityUpdates(listener: ConnectivityChangeListener) {
        connectivityListeners.remove(listener)
    }

    private fun checkPermissionAndCreateChannel() {
        val permission = Manifest.permission.POST_NOTIFICATIONS

        when {
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED -> {
                createChannel()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                showPermissionRationaleDialog()
                }
            else -> {
                requestNotificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                getString(R.string.notification_channel_id),
                getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_HIGH
            )

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Task reminders"

            val notificationManager = ContextCompat.getSystemService(
                this,
                NotificationManager::class.java
            ) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun showPermissionRationaleDialog() {
        AlertDialog.Builder(this)
            .setTitle("Are you sure?")
            .setMessage("Looks like you're missing out on task reminders. Wanna stay in the loop? Give us a quick nod in your settings to allow notifications. Thanks a bunch!")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}

interface ConnectivityChangeListener {
    fun onConnectivityChanged(isConnected: Boolean)
}