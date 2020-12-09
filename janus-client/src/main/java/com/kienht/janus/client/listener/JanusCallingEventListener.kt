package com.kienht.janus.client.listener

import com.kienht.janus.client.model.config.JanusError
import org.webrtc.SessionDescription

/**
 * @author kienht
 * @since 22/09/2020
 */
interface JanusCallingEventListener {

    fun onJanusIncoming(userId: String, remoteSdp: SessionDescription)

    fun onJanusAccepted(userId: String, remoteSdp: SessionDescription)

    fun onJanusHangup()

    fun onJanusError(error: JanusError)

}