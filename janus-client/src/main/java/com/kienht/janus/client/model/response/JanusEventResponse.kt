package com.kienht.janus.client.model.response

import com.kienht.janus.client.model.config.JanusEventName
import com.kienht.janus.client.plugin.JanusPluginName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import org.webrtc.SessionDescription

/**
 * @author kienht
 * @since 08/09/2020
 */
@JsonClass(generateAdapter = true)
data class JanusEventResponse(
    @Json(name = "janus") val event: JanusEventName?,
    @Json(name = "transaction") val transactionId: String?,
    @Json(name = "session_id") val sessionId: Long?,
    @Json(name = "sender") val senderId: Long?,
    @Json(name = "data") val `data`: Data?,
    @Json(name = "plugindata") val pluginData: PluginData?,
    @Json(name = "jsep") val jsep: Jsep?,
    @Json(name = "error") val error: Error?
) {

    val hasSender: Boolean
        get() = senderId != null

    @JsonClass(generateAdapter = true)
    data class Data(
        @Json(name = "id") val id: Long?
    )

    @JsonClass(generateAdapter = true)
    data class PluginData(
        @Json(name = "plugin") val plugin: JanusPluginName?,
        @Json(name = "data") val `data`: Any?
    )

    @JsonClass(generateAdapter = true)
    data class Jsep(
        @Json(name = "type") val type: String?,
        @Json(name = "sdp") val sdp: String?
    )

    @JsonClass(generateAdapter = true)
    data class Error(
        @Json(name = "code") val error: Int?,
        @Json(name = "reason") val reason: String?
    )
}

internal fun JanusEventResponse.toSessionDescription(): SessionDescription? {
    val jsep = jsep ?: return null
    val type = SessionDescription.Type.fromCanonicalForm(jsep.type)
    return SessionDescription(type, jsep.sdp)
}