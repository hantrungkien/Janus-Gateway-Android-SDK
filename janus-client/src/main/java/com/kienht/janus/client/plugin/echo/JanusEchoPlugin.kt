package com.kienht.janus.client.plugin.echo

import android.util.Log
import com.kienht.janus.client.model.config.JanusCommand
import com.kienht.janus.client.model.config.JanusCommandName
import com.kienht.janus.client.model.config.JanusEventName
import com.kienht.janus.client.model.request.JanusBodyRequest
import com.kienht.janus.client.model.request.JanusCallRequest
import com.kienht.janus.client.model.request.JanusJsepRequest
import com.kienht.janus.client.model.response.JanusEventResponse
import com.kienht.janus.client.model.response.toSessionDescription
import com.kienht.janus.client.plugin.JanusPlugin
import com.kienht.janus.client.plugin.JanusPluginName
import com.kienht.janus.client.utils.randomTransactionId
import com.kienht.janus.client.websocket.JanusWSClient

/**
 * @author kienht
 * @since 09/09/2020
 */
internal class JanusEchoPlugin(janusClient: JanusWSClient) : JanusPlugin(janusClient) {

    override val plugin: JanusPluginName
        get() = JanusPluginName.ECHO

    override fun execute(command: JanusCommand) {
        super.execute(command)
        when (command) {
            is JanusCommand.Call -> {
                val request = JanusCallRequest(
                    name = JanusCommandName.MESSAGE,
                    transactionId = randomTransactionId(),
                    sessionId = sessionId,
                    handleId = handleId,
                    jsep = JanusJsepRequest(
                        command.sdp.type.canonicalForm(),
                        command.sdp.description
                    ),
                    body = JanusBodyRequest(
                        request = JanusBodyRequest.Request.CALL,
                        userId = command.userId,
                        audio = command.audio,
                        video = command.video
                    )
                )
                call(request)
            }
        }
    }

    override fun onEvent(event: JanusEventResponse) {
        val transactionId = event.transactionId
        when (event.event) {
            JanusEventName.SUCCESS -> {
                popJanusTransaction(transactionId)?.onSuccess(event)
            }
            JanusEventName.ERROR -> {
                popJanusTransaction(transactionId)?.onError(event)
            }
            JanusEventName.ACK -> {
            }
            else -> {
                if (event.hasSender && handleId == event.senderId) {
                    val description = event.toSessionDescription()
                    if (description != null) {
                        onIncomingCallEvent("KienHT", description)
                    }
                    val pluginData = event.pluginData
                    if (pluginData != null && plugin == pluginData.plugin) {
                        Log.e(TAG, "onEvent: ${pluginData.data}")
                    }
                }
            }
        }
    }
}