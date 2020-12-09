package com.kienht.janus.client.websocket

import android.content.Context
import com.kienht.janus.client.model.request.*
import com.kienht.janus.client.model.request.JanusAnswerRequest
import com.kienht.janus.client.model.request.JanusCallRequest
import com.kienht.janus.client.model.request.JanusClaimRequest
import com.kienht.janus.client.model.request.JanusHangupRequest
import com.kienht.janus.client.model.request.JanusTrickleCandidateRequest
import com.kienht.janus.client.model.response.JanusEventResponse
import com.kienht.janus.client.websocket.lifecycle.CallSessionOnLifecycle
import com.kienht.janus.client.websocket.lifecycle.ConnectivityOnLifecycle
import com.squareup.moshi.Moshi
import com.tinder.scarlet.Scarlet
import com.tinder.scarlet.messageadapter.moshi.MoshiMessageAdapter
import com.tinder.scarlet.streamadapter.rxjava2.RxJava2StreamAdapterFactory
import com.tinder.scarlet.websocket.ShutdownReason
import com.tinder.scarlet.websocket.WebSocketEvent
import com.tinder.scarlet.websocket.okhttp.OkHttpWebSocket
import io.reactivex.Flowable
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * @author kienht
 * @since 08/09/2020
 */
internal class JanusWSClientImpl(
    private val context: Context,
    private val okHttpClient: OkHttpClient,
    private val moshi: Moshi
) : JanusWSClient {

    override val observeWebSocketEvent: Flowable<WebSocketEvent>
        get() = janusWSService?.observeWebSocketEvent() ?: throw ExceptionInInitializerError()

    override val observeJanusEvent: Flowable<JanusEventResponse>
        get() = janusWSService?.observeJanusEvent() ?: throw ExceptionInInitializerError()

    private var scarlet: Scarlet? = null
    private var janusWSService: JanusWSService? = null
    private var callSessionOnLifecycle: CallSessionOnLifecycle? = null
    private var connectivityOnLifecycle: ConnectivityOnLifecycle? = null

    override fun initWS(wss: String) {
        val callSessionOnLifecycle = CallSessionOnLifecycle()
            .also { callSessionOnLifecycle = it }

        val connectivityOnLifecycle = ConnectivityOnLifecycle(context)
            .also { connectivityOnLifecycle = it }

        val config = Scarlet.Configuration(
            lifecycle = callSessionOnLifecycle.combineWith(connectivityOnLifecycle),
            messageAdapterFactories = listOf(MoshiMessageAdapter.Factory(moshi = moshi)),
            streamAdapterFactories = listOf(RxJava2StreamAdapterFactory())
        )

        val scarlet = Scarlet(
            OkHttpWebSocket(
                okHttpClient,
                OkHttpWebSocket.SimpleRequestFactory({
                    Request.Builder()
                        .url(wss)
                        .header("Sec-WebSocket-Protocol", "janus-protocol")
                        .build()
                }, { ShutdownReason.GRACEFUL })
            ),
            config
        ).also { scarlet = it }

        this.janusWSService = scarlet.create()
        callSessionOnLifecycle.start()
    }

    override fun closeWS() {
        callSessionOnLifecycle?.end()
        this.scarlet = null
        this.janusWSService = null
        this.callSessionOnLifecycle = null
        this.connectivityOnLifecycle?.close()
        this.connectivityOnLifecycle = null
    }

    override fun create(request: JanusCreateSessionRequest) {
        janusWSService?.createSession(request)
    }

    override fun attach(request: JanusAttachSessionRequest) {
        janusWSService?.attachSession(request)
    }

    override fun keepAlive(request: JanusKeepAliveSessionRequest) {
        janusWSService?.keepAliveSession(request)
    }

    override fun register(request: JanusRegisterRequest) {
        janusWSService?.register(request)
    }

    override fun call(request: JanusCallRequest) {
        janusWSService?.call(request)
    }

    override fun answer(request: JanusAnswerRequest) {
        janusWSService?.answer(request)
    }

    override fun hangup(request: JanusHangupRequest) {
        janusWSService?.hangup(request)
    }

    override fun trickleCandidate(request: JanusTrickleCandidateRequest) {
        janusWSService?.trickleCandidate(request)
    }

    override fun claim(request: JanusClaimRequest) {
        janusWSService?.claim(request)
    }
}