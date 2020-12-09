package com.kienht.janus.client.websocket.lifecycle

import android.content.Context
import com.kienht.janus.client.utils.InternetConnectionDetector
import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.LifecycleState
import com.tinder.scarlet.lifecycle.LifecycleRegistry

/**
 * @author kienht
 * @since 10/09/2020
 */
internal class ConnectivityOnLifecycle(
    context: Context,
    private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry()
) : Lifecycle by lifecycleRegistry, InternetConnectionDetector.Callback {

    private val internetConnectionDetector = InternetConnectionDetector(context, this)

    init {
        internetConnectionDetector.registerNetworkCallback()
    }

    override fun onConnectivityChanged(isConnected: Boolean) {
        lifecycleRegistry.onNext(toLifecycleState(isConnected))
    }

    fun close() {
        internetConnectionDetector.unregisterNetworkCallback()
    }

    private fun toLifecycleState(isConnected: Boolean): LifecycleState = if (isConnected) {
        LifecycleState.Started
    } else {
        LifecycleState.Stopped
    }
}