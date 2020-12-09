package com.kienht.janus.client.websocket

import com.kienht.janus.client.model.request.*
import com.kienht.janus.client.model.request.JanusAttachSessionRequest
import com.kienht.janus.client.model.request.JanusCallRequest
import com.kienht.janus.client.model.request.JanusCreateSessionRequest
import com.kienht.janus.client.model.request.JanusKeepAliveSessionRequest
import com.kienht.janus.client.model.request.JanusRegisterRequest
import com.kienht.janus.client.model.response.JanusEventResponse
import com.tinder.scarlet.websocket.WebSocketEvent
import io.reactivex.Flowable

/**
 * @author kienht
 * @since 08/09/2020
 */
interface JanusWSClient {

    val observeWebSocketEvent: Flowable<WebSocketEvent>

    val observeJanusEvent: Flowable<JanusEventResponse>

    fun initWS(wss: String)

    fun closeWS()

    fun create(request: JanusCreateSessionRequest)

    fun attach(request: JanusAttachSessionRequest)

    fun keepAlive(request: JanusKeepAliveSessionRequest)

    fun register(request: JanusRegisterRequest)

    fun call(request: JanusCallRequest)

    fun answer(request: JanusAnswerRequest)

    fun hangup(request: JanusHangupRequest)

    fun trickleCandidate(request: JanusTrickleCandidateRequest)

    fun claim(request: JanusClaimRequest)
}