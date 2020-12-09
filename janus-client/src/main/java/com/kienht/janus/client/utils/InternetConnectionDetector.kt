package com.kienht.janus.client.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.getSystemService
import android.util.Log
import java.lang.IllegalStateException

/**
 * @author kienht
 * @since 29/10/2020
 */
internal class InternetConnectionDetector(
    private val context: Context,
    private val callback: Callback
) {

    internal interface Callback {
        fun onConnectivityChanged(isConnected: Boolean)
    }

    private val connectivityManager by lazy {
        context.getSystemService<ConnectivityManager>() ?: throw IllegalStateException()
    }

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) = onConnectivityChange(network, true)
        override fun onLost(network: Network) = onConnectivityChange(network, false)
    }

    private val Network.isOnline: Boolean
        get() {
            val capabilities = connectivityManager.getNetworkCapabilities(this) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        }

    private val isOnline: Boolean
        get() = connectivityManager.allNetworks.any { it.isOnline }

    fun registerNetworkCallback() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()
        connectivityManager.registerNetworkCallback(request, networkCallback)
        callback.onConnectivityChanged(isOnline)
    }

    fun unregisterNetworkCallback() {
        try {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        } catch (e: Exception) {
            Log.e(TAG, "unregisterNetworkCallback: ", e)
        }
    }

    private fun onConnectivityChange(network: Network, isOnline: Boolean) {
        val isAnyOnline = connectivityManager.allNetworks.any {
            if (it == network) {
                isOnline
            } else {
                it.isOnline
            }
        }
        callback.onConnectivityChanged(isAnyOnline)
    }

    companion object{
        private val TAG: String = InternetConnectionDetector::class.java.simpleName
    }
}