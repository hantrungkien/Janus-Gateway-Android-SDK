package com.kienht.janus.client.listener

import com.kienht.janus.client.model.config.JanusState
import com.kienht.janus.client.plugin.JanusPluginName

/**
 * @author kienht
 * @since 10/09/2020
 */
interface OnJanusListener {

    fun onJanusStateChanged(plugin: JanusPluginName, state: JanusState)

    fun onJanusConnectionChanged(isConnected: Boolean)
}