package com.kienht.janus.client.websocket.lifecycle

import com.tinder.scarlet.Lifecycle
import com.tinder.scarlet.LifecycleState
import com.tinder.scarlet.lifecycle.LifecycleRegistry

/**
 * @author kienht
 * @since 10/09/2020
 */
internal class CallSessionOnLifecycle constructor(
    private val lifecycleRegistry: LifecycleRegistry = LifecycleRegistry()
) : Lifecycle by lifecycleRegistry {

    fun start() {
        lifecycleRegistry.onNext(LifecycleState.Started)
    }

    fun end() {
        lifecycleRegistry.onNext(LifecycleState.Stopped)
    }
}