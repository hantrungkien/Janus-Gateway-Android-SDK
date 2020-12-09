package com.kienht.janus.example

import android.util.Log
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

/**
 * @author kienht
 * @since 08/09/2020
 */
abstract class RTCSdpObserver(private val name: String) : SdpObserver {

    override fun onCreateSuccess(sessionDescription: SessionDescription?) {
        Log.e(TAG, "$name => onCreateSuccess: ${sessionDescription?.type?.canonicalForm()}")
    }

    override fun onSetSuccess() {
        Log.e(TAG, "$name => onSetSuccess: ")
    }

    override fun onSetFailure(reason: String?) {
        Log.e(TAG, "$name => onSetFailure: $reason")
    }

    override fun onCreateFailure(reason: String?) {
        Log.e(TAG, "$name => onCreateFailure: $reason")
    }

    companion object {
        private val TAG: String = RTCSdpObserver::class.java.simpleName
    }
}