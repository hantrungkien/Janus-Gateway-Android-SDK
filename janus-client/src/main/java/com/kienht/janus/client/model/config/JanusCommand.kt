package com.kienht.janus.client.model.config

import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

/**
 * @author kienht
 * @since 07/09/2020
 */
sealed class JanusCommand {
    object Create : JanusCommand()

    object Attach : JanusCommand()

    data class Call(
        val sdp: SessionDescription,
        val audio: Boolean = true,
        val video: Boolean = true,
        val userId: String? = null
    ) : JanusCommand()

    data class JoinZoom(val zoomId: Int) : JanusCommand()

    data class Trickle(val ice: IceCandidate) : JanusCommand()

    data class Register(val janusToken: String?, val userId: String?) : JanusCommand()

    data class Answer(val sdp: SessionDescription) : JanusCommand()

    object Hangup : JanusCommand()

    object Claim : JanusCommand()

    object TrickleComplete : JanusCommand()
}