package com.kienht.janus.client.listener

import com.kienht.janus.client.model.response.JanusEventResponse

/**
 * @author kienht
 * @since 08/09/2020
 */
interface JanusTransactionListener {

    fun onSuccess(response: JanusEventResponse)

    fun onError(response: JanusEventResponse) {

    }
}