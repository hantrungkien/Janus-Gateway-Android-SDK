package com.kienht.janus.example

import android.util.Log
import org.webrtc.*

/**
 * @author kienht
 * @since 14/09/2020
 */
internal abstract class RTCPeerConnectionObserver : PeerConnection.Observer {

    override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
        super.onConnectionChange(newState)
        Log.e(TAG, "onConnectionChange: $newState")
    }

    override fun onIceGatheringChange(iceGatheringState: PeerConnection.IceGatheringState?) {
        Log.e(TAG, "onIceGatheringChange: ")
    }

    override fun onTrack(transceiver: RtpTransceiver?) {
        Log.e(TAG, "onTrack: ")
    }

    override fun onIceCandidate(iceCandidate: IceCandidate?) {
        Log.e(TAG, "onIceCandidate: ")
    }

    override fun onSignalingChange(state: PeerConnection.SignalingState?) {
        Log.e(TAG, "onSignalingChange: $state")
    }

    override fun onIceConnectionChange(state: PeerConnection.IceConnectionState?) {
        Log.e(TAG, "onIceConnectionChange: $state")
    }

    override fun onIceConnectionReceivingChange(value: Boolean) {
        Log.e(TAG, "onIceConnectionReceivingChange: $value")
    }

    override fun onIceCandidatesRemoved(iceCandidates: Array<out IceCandidate>?) {
        Log.e(TAG, "onIceCandidatesRemoved: $iceCandidates")
    }

    override fun onAddStream(mediaStream: MediaStream?) {
        Log.e(TAG, "onAddStream: $mediaStream")
    }

    override fun onRemoveStream(mediaStream: MediaStream?) {
        Log.e(TAG, "onRemoveStream: $mediaStream")
    }

    override fun onDataChannel(dataChannel: DataChannel?) {
        Log.e(TAG, "onDataChannel: $dataChannel")
    }

    override fun onRenegotiationNeeded() {
        Log.e(TAG, "onRenegotiationNeeded: ")
    }

    override fun onAddTrack(rtpReceiver: RtpReceiver?, mediaStreams: Array<out MediaStream>?) {
        Log.e(TAG, "onAddTrack: $mediaStreams")
    }

    companion object {
        private val TAG: String = RTCPeerConnectionObserver::class.java.simpleName
    }
}