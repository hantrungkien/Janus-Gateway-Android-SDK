package com.kienht.janus.client.model.request

import com.kienht.janus.client.model.config.JanusCommandName
import com.kienht.janus.client.plugin.JanusPluginName
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * @author kienht
 * @since 07/09/2020
 */
@JsonClass(generateAdapter = true)
data class JanusAttachSessionRequest(
    @Json(name = "janus") val name: JanusCommandName,
    @Json(name = "transaction") val transactionId: String,
    @Json(name = "plugin") val plugin: JanusPluginName,
    @Json(name = "session_id") val sessionId: Long
)