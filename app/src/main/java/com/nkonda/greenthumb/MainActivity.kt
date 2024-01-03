package com.nkonda.greenthumb

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.nkonda.greenthumb.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), ConnectivityChangeListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        connectivityManager.registerDefaultNetworkCallback(networkCallback)


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
        for (listener in connectivityListeners) {
            listener.onConnectivityChanged(isConnected)
        }
    }

    fun registerForConnectivityUpdates(listener: ConnectivityChangeListener) {
        connectivityListeners.add(listener)
    }

    fun unregisterFromConnectivityUpdates(listener: ConnectivityChangeListener) {
        connectivityListeners.remove(listener)
    }

}

interface ConnectivityChangeListener {
    fun onConnectivityChanged(isConnected: Boolean)
}