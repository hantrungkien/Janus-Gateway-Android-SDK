package com.kienht.janus.client.plugin.videocall

import com.kienht.janus.client.model.config.*
import com.kienht.janus.client.model.config.JanusCommandName
import com.kienht.janus.client.model.config.JanusEventName
import com.kienht.janus.client.model.config.JanusPluginDataEvent
import com.kienht.janus.client.model.request.*
import com.kienht.janus.client.model.request.JanusAnswerRequest
import com.kienht.janus.client.model.request.JanusBodyRequest
import com.kienht.janus.client.model.request.JanusCallRequest
import com.kienht.janus.client.model.request.JanusJsepRequest
import com.kienht.janus.client.model.request.JanusRegisterRequest
import com.kienht.janus.client.model.response.JanusEventResponse
import com.kienht.janus.client.model.response.toSessionDescription
import com.kienht.janus.client.plugin.JanusPlugin
import com.kienht.janus.client.plugin.JanusPluginName
import com.kienht.janus.client.utils.randomTransactionId
import com.kienht.janus.client.websocket.JanusWSClient
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import org.webrtc.SessionDescription

/**
 * @author kienht
 * @since 09/09/2020
 */
internal class JanusVideoCallPlugin(
    janusClient: JanusWSClient,
    private val moshi: Moshi
) : JanusPlugin(janusClient) {

    override val plugin: JanusPluginName
        get() = JanusPluginName.VIDEO_CALL

    override fun execute(command: JanusCommand) {
        super.execute(command)
        when (command) {
            is JanusCommand.Register -> register(command.janusToken, command.userId)

            is JanusCommand.Call -> call(
                command.sdp,
                command.audio,
                command.video,
                command.userId
            )

            is JanusCommand.Answer -> answer(command.sdp)

            is JanusCommand.Hangup -> hangup()
        }
    }

    override fun onEvent(event: JanusEventResponse) {
        val transactionId = event.transactionId
        when (event.event) {
            JanusEventName.SUCCESS -> {
                popJanusTransaction(transactionId)?.onSuccess(event)
            }
            JanusEventName.ERROR -> {
                val janusTransactionListener = popJanusTransaction(transactionId)
                if (janusTransactionListener != null) {
                    janusTransactionListener.onError(event)
                } else if (event.error != null) {
                    val error = event.error.error ?: return
                    onErrorEvent(error)
                }
            }
            JanusEventName.ACK -> {
            }
            else -> {
                if (event.hasSender && handleId == event.senderId) {
                    when (event.event) {
                        JanusEventName.EVENT -> onResultEvent(event)
                        JanusEventName.DETACHED -> onLeaving()
                    }
                }
            }
        }
    }

    private fun onResultEvent(event: JanusEventResponse) {
        val description = event.toSessionDescription()
        val pluginData = parsePluginData(event.pluginData)
        val result = pluginData?.result
        when (result?.event) {
            JanusPluginDataEvent.REGISTERED -> {
                onRegistered(result.userId.orEmpty())
            }
            JanusPluginDataEvent.ACCEPTED -> {
                val userId = result.userId
                if (description != null) {
                    onAcceptedCallEvent(userId.orEmpty(), description)
                }
            }
            JanusPluginDataEvent.HANGUP -> {
                onHangup()
            }
            JanusPluginDataEvent.INCOMING_CALL -> {
                val userId = result.userId
                if (description != null) {
                    onIncomingCallEvent(userId.orEmpty(), description)
                }
            }
            else -> {
                val error = pluginData?.errorCode
                if (error != null) {
                    onErrorEvent(error)
                }
            }
        }
    }

    private fun onErrorEvent(errorCode: Int) {
        val error = JanusError.get(errorCode) ?: return
        onError(error)
    }

    private fun register(token: String?, userId: String?) {
        val request = JanusRegisterRequest(
            name = JanusCommandName.MESSAGE,
            transactionId = randomTransactionId(),
            sessionId = sessionId,
            handleId = handleId,
            body = JanusBodyRequest(
                token = token,
                sid = sessionId.toString(),
                request = JanusBodyRequest.Request.REGISTER,
                userId = userId
            )
        )
        register(request)
    }

    private fun call(
        sdp: SessionDescription,
        audio: Boolean = true,
        video: Boolean = true,
        userId: String? = null
    ) {
        val request = JanusCallRequest(
            name = JanusCommandName.MESSAGE,
            transactionId = randomTransactionId(),
            sessionId = sessionId,
            handleId = handleId,
            jsep = JanusJsepRequest(
                sdp.type.canonicalForm(),
                sdp.description
            ),
            body = JanusBodyRequest(
                request = JanusBodyRequest.Request.CALL,
                userId = userId,
                audio = audio,
                video = video
            )
        )
        call(request)
    }

    private fun answer(sdp: SessionDescription) {
        val request = JanusAnswerRequest(
            name = JanusCommandName.MESSAGE,
            transactionId = randomTransactionId(),
            sessionId = sessionId,
            handleId = handleId,
            jsep = JanusJsepRequest(
                sdp.type.canonicalForm(),
                sdp.description
            ),
            body = JanusBodyRequest(
                request = JanusBodyRequest.Request.ACCEPT
            )
        )
        answer(request)
    }

    private fun hangup() {
        val request = JanusHangupRequest(
            name = JanusCommandName.MESSAGE,
            transactionId = randomTransactionId(),
            sessionId = sessionId,
            handleId = handleId,
            body = JanusBodyRequest(
                request = JanusBodyRequest.Request.HANGUP
            )
        )
        hangup(request)
    }

    private fun parsePluginData(pluginData: JanusEventResponse.PluginData?): JanusVideoCallPluginData? {
        return if (pluginData != null && plugin == pluginData.plugin && pluginData.data != null) {
            val map = pluginData.data as? Map<*, *>
            val type =
                Types.newParameterizedType(Map::class.java, String::class.java, Any::class.java)
            val adapter = moshi.adapter<Map<*, *>>(type)
            val json = adapter.toJson(map)
            moshi.adapter(JanusVideoCallPluginData::class.java).fromJson(json)
        } else null
    }
}