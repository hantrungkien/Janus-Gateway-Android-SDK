package com.kienht.janus.client.websocket

import com.kienht.janus.client.model.request.*
import com.kienht.janus.client.model.request.JanusAttachSessionRequest
import com.kienht.janus.client.model.request.JanusCallRequest
import com.kienht.janus.client.model.request.JanusCreateSessionRequest
import com.kienht.janus.client.model.request.JanusKeepAliveSessionRequest
import com.kienht.janus.client.model.request.JanusRegisterRequest
import com.kienht.janus.client.model.response.JanusEventResponse
import com.tinder.scarlet.websocket.WebSocketEvent
import com.tinder.scarlet.ws.Receive
import com.tinder.scarlet.ws.Send
import io.reactivex.Flowable

/**
 * @author kienht
 * @since 08/09/2020
 */
internal interface JanusWSService {

    @Receive
    fun observeWebSocketEvent(): Flowable<WebSocketEvent>

    @Receive
    fun observeJanusEvent(): Flowable<JanusEventResponse>

    @Send
    fun createSession(request: JanusCreateSessionRequest): Boolean

    @Send
    fun attachSession(request: JanusAttachSessionRequest): Boolean

    @Send
    fun register(request: JanusRegisterRequest): Boolean

    @Send
    fun keepAliveSession(request: JanusKeepAliveSessionRequest): Boolean

    @Send
    fun call(request: JanusCallRequest): Boolean

    @Send
    fun answer(request: JanusAnswerRequest): Boolean

    @Send
    fun hangup(request: JanusHangupRequest): Boolean

    @Send
    fun trickleCandidate(request: JanusTrickleCandidateRequest): Boolean

    @Send
    fun claim(request: JanusClaimRequest): Boolean
}