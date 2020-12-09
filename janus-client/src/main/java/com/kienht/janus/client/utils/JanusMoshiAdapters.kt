package com.kienht.janus.client.utils

import com.kienht.janus.client.model.config.JanusCommandName
import com.kienht.janus.client.model.config.JanusError
import com.kienht.janus.client.model.config.JanusEventName
import com.kienht.janus.client.model.config.JanusPluginDataEvent
import com.kienht.janus.client.model.request.JanusBodyRequest
import com.kienht.janus.client.plugin.JanusPluginName
import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson

/**
 * @author kienht
 * @since 08/09/2020
 */
internal object JanusMoshiAdapters {

    @FromJson
    fun janusEventNameFromJson(string: String): JanusEventName? {
        return JanusEventName.values().find { it.value == string }
    }

    @ToJson
    fun janusEventNameToJson(data: JanusEventName?): String? {
        return data?.value
    }

    @FromJson
    fun janusCommandFromJson(string: String): JanusCommandName? {
        return JanusCommandName.values().find { it.command == string }
    }

    @ToJson
    fun janusCommandToJson(data: JanusCommandName?): String? {
        return data?.command
    }

    @FromJson
    fun janusPluginFromJson(string: String): JanusPluginName? {
        return JanusPluginName.values().find { it.plugin == string }
    }

    @ToJson
    fun janusPluginToJson(data: JanusPluginName?): String? {
        return data?.plugin
    }

    @FromJson
    fun janusBodyRequestFromJson(string: String): JanusBodyRequest.Request? {
        return JanusBodyRequest.Request.values().find { it.value == string }
    }

    @ToJson
    fun janusBodyRequestToJson(data: JanusBodyRequest.Request?): String? {
        return data?.value
    }

    @FromJson
    fun janusVideoCallEventPluginDataFromJson(string: String): JanusPluginDataEvent? {
        return JanusPluginDataEvent.values().find { it.value == string }
    }

    @ToJson
    fun janusVideoCallEventPluginDataToJson(data: JanusPluginDataEvent?): String? {
        return data?.value
    }

    @FromJson
    fun janusErrorFromJson(code: Int): JanusError? {
        return JanusError.values().find { it.code == code }
    }

    @ToJson
    fun janusErrorToJson(data: JanusError?): Int? {
        return data?.code
    }
}