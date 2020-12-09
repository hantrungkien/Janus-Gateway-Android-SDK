package com.kienht.janus.client.plugin.videoroom

import com.kienht.janus.client.model.config.JanusCommand
import com.kienht.janus.client.model.response.JanusEventResponse
import com.kienht.janus.client.plugin.JanusPlugin
import com.kienht.janus.client.plugin.JanusPluginName
import com.kienht.janus.client.websocket.JanusWSClient
import com.squareup.moshi.Moshi

/**
 * @author kienht
 * @since 23/09/2020
 */
internal class JanusVideoRoomPlugin(
    janusClient: JanusWSClient,
    private val moshi: Moshi
) : JanusPlugin(janusClient) {

    override val plugin: JanusPluginName
        get() = JanusPluginName.VIDEO_ZOOM

    override fun execute(command: JanusCommand) {
        super.execute(command)
        when (command) {
            is JanusCommand.JoinZoom -> {
            }
        }
    }

    override fun onEvent(event: JanusEventResponse) {

    }
}